package daripher.skilltree.skill;

import daripher.skilltree.config.CraftConfig;
import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class CraftHelper {
    private static final int[] BASE_MULTIPLIERS = {0, 4, 13, 40, 100};

    public static double calculateCraftXP(ResourceLocation recipeId, Player player) {
        int tier = CraftConfig.INSTANCE.getTier(recipeId);
        int level = Math.max(1, PlayerSkillsProvider.get(player).getCurrentLevel());
        int B = BASE_MULTIPLIERS[tier];
        return B * (1 + 4 * Math.pow(level - 1, 2));
    }
}
