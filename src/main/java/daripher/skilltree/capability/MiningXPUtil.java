package daripher.skilltree.capability;

import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.config.Config;
import daripher.skilltree.network.NetworkDispatcher;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.network.NetworkDirection;

public class MiningXPUtil {
    public static double getOreB(Block block) {
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
        if (blockId == null) return 0.0;
        String idStr = blockId.toString();
        if (Config.getTier1Ores().contains(idStr)) {
            return Config.getTier1OreB();
        } else if (Config.getTier2Ores().contains(idStr)) {
            return Config.getTier2OreB();
        } else if (Config.getTier3Ores().contains(idStr)) {
            return Config.getTier3OreB();
        } else if (Config.getTier4Ores().contains(idStr)) {
            return Config.getTier4OreB();
        }
        return 0.0;
    }

    public static double calculateXP(double b, int playerLevel) {
        if (b == 0.0) return 0;
        return (b * (1 + 2.5 * (playerLevel - 1) / 119.0));
    }

    public static int getPlayerLevel(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            int level = serverPlayer.getCapability(PlayerSkillsProvider.CAPABILITY)
                    .map(IPlayerSkills::getCurrentLevel)
                    .orElse(1);
            System.out.println("Getting player level for " + serverPlayer.getName().getString() + ": " + level);
            return level + 1;
        }
        System.out.println("Fallback level for non-server player: 1");
        return 1;
    }

    public static void addXP(ServerPlayer player, double amount) {
        player.getCapability(PlayerSkillsProvider.CAPABILITY)
                .ifPresent(skills -> {
                    long currentTime = player.level().getGameTime(); // Время в тиках
                    long lastTime = skills.getLastMiningXPTime();
                    double multiplier = 1.0;
                    int consecutive = 1;

                    if (currentTime - lastTime <= Config.getMiningGrindTimeWindow() * 20L) {
                        consecutive = skills.getConsecutiveMiningActions() + 1;
                        multiplier = Math.max(Config.getMiningGrindMinMultiplier(), 1.0 - (consecutive - 1) * Config.getMiningGrindPenaltyStep());
                    }

                    skills.setConsecutiveMiningActions(consecutive);
                    skills.setLastMiningXPTime(currentTime);

                    double modifiedAmount = amount * multiplier;

                    skills.addSkillExperience(modifiedAmount);
                    NetworkDispatcher.network_channel.sendTo(
                            new SyncPlayerSkillsMessage(player),
                            player.connection.connection,
                            NetworkDirection.PLAY_TO_CLIENT
                    );
                });
    }
}
