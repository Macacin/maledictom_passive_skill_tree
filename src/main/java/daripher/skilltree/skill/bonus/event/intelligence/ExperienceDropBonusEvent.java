package daripher.skilltree.skill.bonus.event.intelligence;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.intelligence.ExperienceDropBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class ExperienceDropBonusEvent {
    @SubscribeEvent
    public static void onLivingExperienceDrop(LivingExperienceDropEvent event) {
        if (event.getAttackingPlayer() == null) return;
        if (!(event.getAttackingPlayer() instanceof ServerPlayer player)) return;
        if (player.level().isClientSide) return;

        double bonus = PlayerSkillsProvider.get(player).getCachedBonus(ExperienceDropBonus.class);
        if (bonus == 0) return;

        int originalXP = event.getDroppedExperience();
        int newXP = (int) Math.round(originalXP * (1 + bonus));
        event.setDroppedExperience(newXP);
    }
}