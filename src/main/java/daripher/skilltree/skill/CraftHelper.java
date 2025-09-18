package daripher.skilltree.skill;

import daripher.skilltree.config.CraftConfig;
import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class CraftHelper {
    private static final int[] BASE_MULTIPLIERS = {0, 4, 13, 40, 100};  // B для Tier 0-4

    public static int calculateCraftXP(ResourceLocation recipeId, Player player) {
        int tier = CraftConfig.INSTANCE.getTier(recipeId);
        int level = Math.max(1, PlayerSkillsProvider.get(player).getCurrentLevel());  // L >= 1
        int B = BASE_MULTIPLIERS[tier];
        double xp = B * (1 + 4 * Math.pow(level - 1, 2));
        return (int) Math.round(xp);  // Round to int, 0 if B=0
    }
}
