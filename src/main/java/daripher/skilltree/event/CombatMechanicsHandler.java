package daripher.skilltree.event;  // Подставь свой пакет, если другой

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.config.Config;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraft.world.entity.projectile.AbstractArrow;

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
}