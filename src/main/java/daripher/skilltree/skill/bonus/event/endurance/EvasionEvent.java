package daripher.skilltree.skill.bonus.event.endurance;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.endurance.EvasionBonusMagic;
import daripher.skilltree.skill.bonus.player.endurance.EvasionBonusPhysical;
import daripher.skilltree.skill.bonus.player.endurance.EvasionBonusProjectile;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class EvasionEvent {
    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!player.isAlive() || player.isDeadOrDying()) return;

        DamageSource source = event.getSource();
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;

        RandomSource random = player.getRandom();
        double physicalBonus = PlayerSkillsProvider.get(player).getCachedBonus(EvasionBonusPhysical.class);
        double magicBonus = PlayerSkillsProvider.get(player).getCachedBonus(EvasionBonusMagic.class);
        double projectileBonus = PlayerSkillsProvider.get(player).getCachedBonus(EvasionBonusProjectile.class);

        boolean isProjectile = source.is(DamageTypes.ARROW) || source.getDirectEntity() instanceof Projectile;
        boolean isMagic = source.is(DamageTypes.MAGIC) || source.is(DamageTypes.INDIRECT_MAGIC);
        boolean isPhysical = source.is(DamageTypes.PLAYER_ATTACK) || source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.MOB_ATTACK_NO_AGGRO);

        double evasionChance = 0;
        if (isProjectile) evasionChance = projectileBonus;
        else if (isMagic) evasionChance = magicBonus;
        else if (isPhysical) evasionChance = physicalBonus;

        if (evasionChance > 0 && random.nextFloat() < Math.min(evasionChance, 1.0)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (!(event.getRayTraceResult().getType() == net.minecraft.world.phys.HitResult.Type.ENTITY)) return;
        net.minecraft.world.phys.EntityHitResult hit = (net.minecraft.world.phys.EntityHitResult) event.getRayTraceResult();
        if (!(hit.getEntity() instanceof ServerPlayer player)) return;
        if (!player.isAlive() || player.isDeadOrDying()) return;

        Projectile projectile = event.getProjectile();
        RandomSource random = player.getRandom();
        double projectileBonus = PlayerSkillsProvider.get(player).getCachedBonus(EvasionBonusProjectile.class);
        double magicBonus = PlayerSkillsProvider.get(player).getCachedBonus(EvasionBonusMagic.class);

        boolean isMagicProjectile = projectile instanceof ThrownPotion || projectile instanceof AbstractHurtingProjectile;

        double evasionChance = 0;
        if (isMagicProjectile) evasionChance = magicBonus;
        else evasionChance = projectileBonus;

        if (evasionChance > 0 && random.nextFloat() < Math.min(evasionChance, 1.0)) {
            event.setCanceled(true);
            // Optional: player.playSound(...);
        }
    }
}