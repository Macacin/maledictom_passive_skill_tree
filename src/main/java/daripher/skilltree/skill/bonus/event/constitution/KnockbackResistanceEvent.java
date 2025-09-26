package daripher.skilltree.skill.bonus.event.constitution;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.constitution.KnockbackResistanceBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class KnockbackResistanceEvent {
    @SubscribeEvent
    public static void onLivingKnockBack(LivingKnockBackEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        double bonus = PlayerSkillsProvider.get(player).getCachedBonus(KnockbackResistanceBonus.class);
        if (bonus == 0) return;
        float newStrength = event.getStrength() * (float) (1 - bonus);
        event.setStrength(newStrength);
    }
}