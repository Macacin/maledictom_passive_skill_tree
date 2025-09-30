package daripher.skilltree.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.config.Config;
import daripher.skilltree.entity.EquippedEntity;

import java.util.ArrayList;
import java.util.List;

import daripher.skilltree.init.PSTAttributes;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.event.agility.JumpHeightEvent;
import daripher.skilltree.skill.bonus.player.strength.ArmorIgnoreBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements EquippedEntity {
    private final List<ItemStack> equippedItems = new ArrayList<>();

    @Inject(method = "dropAllDeathLoot", at = @At("HEAD"))
    private void storeEquipmentBeforeDeath(DamageSource damageSource, CallbackInfo callbackInfo) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack itemInSlot = getItemBySlot(slot);
            if (itemInSlot.isEmpty()) continue;
            equippedItems.add(itemInSlot);
        }
    }

    @SuppressWarnings("ConstantValue")
    @ModifyReturnValue(method = "getJumpPower", at = @At("RETURN"))
    private float applyJumpHeightBonus(float original) {
        if (!((Object) this instanceof ServerPlayer player)) return original;
        return original * JumpHeightEvent.getJumpHeightMultiplier(player);
    }

    @Override
    public boolean hasItemEquipped(ItemStack stack) {
        return equippedItems.stream().anyMatch(equipped -> ItemStack.matches(stack, equipped));
    }

    public abstract @Shadow ItemStack getItemBySlot(EquipmentSlot slot);

    @Inject(
            method = "knockback(DDD)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preventShieldKnockback(double strength, double x, double z, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof Player player && player.isBlocking()) {
            ci.cancel();
        }
    }

    @Inject(method = "getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F", at = @At("HEAD"), cancellable = true)
    private void applyArmorIgnore(DamageSource source, float damage, CallbackInfoReturnable<Float> callback) {
        if (!(source.getEntity() instanceof ServerPlayer player)) return;
        double totalIgnore = PlayerSkillsProvider.get(player).getCachedBonus(ArmorIgnoreBonus.class);
        if (totalIgnore <= 0) return;
        totalIgnore = Math.min(1.0, totalIgnore);
        LivingEntity target = (LivingEntity) (Object) this;
        double armor = target.getAttributeValue(Attributes.ARMOR);
        double toughness = target.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
        double effectiveArmor = armor * (1 - totalIgnore);

        float modifiedDamage = CombatRules.getDamageAfterAbsorb(damage, (float) effectiveArmor, (float) toughness);

        callback.setReturnValue(modifiedDamage);
    }
}
