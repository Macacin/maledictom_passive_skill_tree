package daripher.skilltree.event;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.assist.DamageAssistProvider;
import daripher.skilltree.capability.grind.GrindTracker;
import daripher.skilltree.capability.grind.GrindTrackerProvider;
import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.network.NetworkDispatcher;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class AssistXPHandler {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        try {
            DamageSource source = event.getSource();
            if (source.getEntity() instanceof Player attacker) {
                LivingEntity target = event.getEntity();
                if (target instanceof Player) return;
                DamageAssistProvider.get(target).markAssisted(attacker.getUUID());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide) return;
        LivingEntity enemy = event.getEntity();
        DamageSource source = event.getSource();
        Player killer = source.getEntity() instanceof Player ? (Player) source.getEntity() : null;
        if (killer == null) return;

        float maxHP = enemy.getMaxHealth();
        double mobType = getMobType(maxHP);
        double base = maxHP * 0.1;
        double B = Math.pow(base, 1.2) * mobType;
        double r = 0.3 + 0.4 * (1 - Math.exp(-0.005 * maxHP));
        int xpAssistBase = (int) (B * r);

        if (xpAssistBase <= 0) return;

        Map<UUID, Boolean> assisted = DamageAssistProvider.get(enemy).getAssistedPlayers();
        Vec3 deathPos = enemy.position();

        for (Map.Entry<UUID, Boolean> entry : assisted.entrySet()) {
            UUID uuid = entry.getKey();
            if (uuid.equals(killer.getUUID())) continue;

            Player assister = enemy.level().getPlayerByUUID(uuid);
            if (assister == null) continue;

            double distance = assister.position().distanceTo(deathPos);
            if (distance > 50) continue;

            String mobTypeStr = enemy.getType().toString();
            GrindTracker grind = GrindTrackerProvider.get(assister);
            double multiplier = grind.getPenaltyMultiplier(mobTypeStr);
            grind.updateLastTime(assister, mobTypeStr);

            double previousXP = grind.getLastXP(mobTypeStr);
            if (previousXP <= 0) previousXP = xpAssistBase;
            int xpAssist = (int) (previousXP * multiplier);
            xpAssist = Math.max(1, xpAssist);
            grind.setLastXP(mobTypeStr, xpAssist);
            if (xpAssist > 0) {
                IPlayerSkills skills = PlayerSkillsProvider.get(assister);
                skills.addSkillExperience(xpAssist);
                if (assister instanceof ServerPlayer serverPlayer) {
                    NetworkDispatcher.network_channel.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SyncPlayerSkillsMessage(serverPlayer));
                }
            }
        }

        DamageAssistProvider.get(enemy).clear();
    }

    private static double getMobType(float maxHP) {
        if (maxHP <= 12) return 0.0;
        else if (maxHP <= 300) return 1.2;
        else if (maxHP <= 1500) return 1.8;
        else return 3.0;
    }
}