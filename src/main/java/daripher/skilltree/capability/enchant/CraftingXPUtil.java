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
    public static int calculateXP(int playerLevel) {
        return (int) (Config.ENCHANTMENT_COEFFICIENT.get() * Math.sqrt(playerLevel));
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

    public static void addXP(ServerPlayer player, int amount) {
        player.getCapability(PlayerSkillsProvider.CAPABILITY)
                .ifPresent(skills -> {
                    System.out.println("Adding " + amount + " XP to player " + player.getName().getString() + " (current exp: " + skills.getSkillExperience() + ")");
                    skills.addSkillExperience(amount);
                    NetworkDispatcher.network_channel.sendTo(
                            new SyncPlayerSkillsMessage(player),
                            player.connection.connection,
                            NetworkDirection.PLAY_TO_CLIENT
                    );
                });
    }
}