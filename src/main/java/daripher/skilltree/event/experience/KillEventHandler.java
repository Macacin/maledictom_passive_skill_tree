package daripher.skilltree.event.experience;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.capability.grind.GrindTracker;
import daripher.skilltree.capability.grind.GrindTrackerProvider;
import daripher.skilltree.config.Config;
import daripher.skilltree.network.NetworkDispatcher;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class KillEventHandler {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide) return;
        LivingEntity enemy = event.getEntity();
        DamageSource source = event.getSource();
        Player player = source.getEntity() instanceof Player ? (Player) source.getEntity() : null;
        if (player == null) return;
        if (enemy.deathTime > 0) return;

        float maxHP = enemy.getMaxHealth();
        double mobType = getMobType(maxHP);
        double base = maxHP * 0.1;
        double B = Math.pow(base, 1.2) * mobType;

        String mobTypeStr = enemy.getType().toString();
        GrindTracker grind = GrindTrackerProvider.get(player);
        double multiplier = grind.getPenaltyMultiplier(mobTypeStr);
        grind.updateLastTime(player, mobTypeStr);

        double previousXP = grind.getLastXP(mobTypeStr);
        if (previousXP <= 0) previousXP = B;
        double amount = Math.max(1, (previousXP * multiplier));
        grind.setLastXP(mobTypeStr, amount);
        if (amount > 0) {
            IPlayerSkills skills = PlayerSkillsProvider.get(player);
            skills.addSkillExperience(amount);
            if (player instanceof ServerPlayer serverPlayer) {
                NetworkDispatcher.network_channel.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SyncPlayerSkillsMessage(serverPlayer));
            }
        }
    }

    private static double getMobType(float maxHP) {
        if (maxHP <= Config.TIER0_MOB_HP.get()) return 0.0;
        else if (maxHP <= Config.TIER1_MOB_HP.get()) return Config.TIER1_MOB_B.get();
        else if (maxHP <= Config.TIER2_MOB_HP.get()) return Config.TIER2_MOB_B.get();
        else return Config.TIER3_MOB_B.get();
    }
}