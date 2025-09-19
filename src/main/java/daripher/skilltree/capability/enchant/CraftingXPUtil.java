package daripher.skilltree.capability.enchant;

import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.config.Config;
import daripher.skilltree.network.NetworkDispatcher;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;

public class CraftingXPUtil {
    public static double calculateXP(int playerLevel) {
        return (Config.ENCHANTMENT_COEFFICIENT.get() * Math.sqrt(playerLevel));
    }

    public static int getPlayerLevel(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            int level = serverPlayer.getCapability(PlayerSkillsProvider.CAPABILITY)
                    .map(IPlayerSkills::getCurrentLevel)
                    .orElse(1);
            return level + 1;
        }
        return 1;
    }

    public static void addXP(ServerPlayer player, double amount) {
        player.getCapability(PlayerSkillsProvider.CAPABILITY)
                .ifPresent(skills -> {
                    long currentTime = player.level().getGameTime(); // Время в тиках
                    long lastTime = skills.getLastCraftingXPTime();
                    double multiplier = 1.0;
                    int consecutive = 1;

                    if (currentTime - lastTime <= Config.getCraftingGrindTimeWindow() * 20L) {
                        consecutive = skills.getConsecutiveCraftingActions() + 1;
                        multiplier = Math.max(Config.getCraftingGrindMinMultiplier(), 1.0 - (consecutive - 1) * Config.getCraftingGrindPenaltyStep());
                    }

                    skills.setConsecutiveCraftingActions(consecutive);
                    skills.setLastCraftingXPTime(currentTime);

                    double modifiedAmount = (amount * multiplier);

                    skills.addSkillExperience(modifiedAmount);
                    NetworkDispatcher.network_channel.sendTo(
                            new SyncPlayerSkillsMessage(player),
                            player.connection.connection,
                            NetworkDirection.PLAY_TO_CLIENT
                    );
                });
    }
}