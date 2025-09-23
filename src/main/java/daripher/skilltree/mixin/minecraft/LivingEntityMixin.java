package daripher.skilltree.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import daripher.skilltree.config.Config;
import daripher.skilltree.entity.EquippedEntity;

import java.util.ArrayList;
import java.util.List;

import daripher.skilltree.skill.bonus.event.agility.JumpHeightEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
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
        if (!((Object) this instanceof Player player)) return original;
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
}
