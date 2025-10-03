package daripher.skilltree.event.base;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.config.Config;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraft.world.entity.projectile.AbstractArrow;

import static daripher.skilltree.SkillTreeMod.SHIELD_SLOWDOWN_UUID;
import static daripher.skilltree.SkillTreeMod.getShieldSlowdownModifier;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class CombatMechanicsHandler {

    @SubscribeEvent
    public static void handleAccuracy(LivingAttackEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;
        if (player.level().isClientSide) return;

        IPlayerSkills skills = PlayerSkillsProvider.get(player);
        double accuracyChance = skills.getAccuracy() / 100.0f;
        if (player.getRandom().nextFloat() > accuracyChance) {
            System.out.println("Attack cancelled");
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void weakenRangedDamage(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof Player)) return;
        if (!(source.getDirectEntity() instanceof AbstractArrow)) return;
        if (event.getEntity().level().isClientSide) return;

        event.setAmount((float) (event.getAmount() * Config.RANGED_DAMAGE_MULTIPLIER.get()));
    }

    @SubscribeEvent
    public static void weakenProjectileVelocity(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof AbstractArrow projectile)) return;
        if (!(projectile.getOwner() instanceof Player)) return;
        if (event.getLevel().isClientSide) return;

        projectile.setDeltaMovement(projectile.getDeltaMovement().scale(Config.RANGED_VELOCITY_MULTIPLIER.get()));
    }

    @SubscribeEvent
    public static void onShieldBlock(ShieldBlockEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;
        if (event.getDamageSource().is(DamageTypeTags.BYPASSES_SHIELD)) return;

        float originalBlocked = event.getBlockedDamage();
        float reduction = (float) Config.getShieldReduction();
        event.setBlockedDamage(originalBlocked * reduction);
    }

    @SubscribeEvent
    public static void applyShieldSlowdown(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide) return;
        Player player = event.player;

        ItemStack mainhand = player.getMainHandItem();
        ItemStack offhand = player.getOffhandItem();
        AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);

        boolean hasShield = mainhand.getItem() instanceof ShieldItem || offhand.getItem() instanceof ShieldItem;

        if (hasShield) {
            if (!speedAttr.hasModifier(getShieldSlowdownModifier())) {
                speedAttr.addTransientModifier(getShieldSlowdownModifier());
            }
        } else {
            speedAttr.removeModifier(SHIELD_SLOWDOWN_UUID);
        }
    }

    @SubscribeEvent
    public static void weakenMagicDamage(LivingHurtEvent event) {
        if (!ModList.get().isLoaded("irons_spellbooks")) return;
        if (!(event.getEntity() instanceof Player)) return;
        if (event.getEntity().level().isClientSide) return;

        DamageSource source = event.getSource();

        boolean containsMagic = source.getMsgId().contains("magic");
        if (!containsMagic) return;

        double reduction = Config.getMagicDamageReduction();
        event.setAmount((float) (event.getAmount() * (1 - reduction)));
    }
}