package daripher.skilltree.skill.bonus.event.agility;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.agility.JumpHeightBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class JumpHeightEvent {
    private static final double BLOCKS_PER_REDUCTION = 0.05;

    public static float getJumpHeightMultiplier(ServerPlayer player) {
        IPlayerSkills skills = PlayerSkillsProvider.get(player);
        double cachedMultiplier = skills.getCachedBonus(JumpHeightBonus.class);
        return 1f + (float) cachedMultiplier;
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        float totalMultiplier = getJumpHeightMultiplier(player);
        double extraHeight = approximateDeltaHeight(totalMultiplier - 1);

        double reduction = extraHeight * BLOCKS_PER_REDUCTION;
        reduction = Math.min(reduction, 0.6);

        event.setDamageMultiplier((float) (1.0 - reduction));
    }

    private static double approximateDeltaHeight(float extraAmount) {
        return 2.4 * extraAmount;
    }
}