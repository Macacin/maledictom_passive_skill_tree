package daripher.skilltree.skill.bonus.event.constitution;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.constitution.RegenerationBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class RegenerationEvent {
    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (event.getAmount() != 1.0F) return; // Apply only to regeneration heals (natural and effect-based, which heal 1.0F at a time)
        double bonus = PlayerSkillsProvider.get(player).getCachedBonus(RegenerationBonus.class);
        if (bonus == 0) return;
        float newAmount = event.getAmount() * (float) (1 + bonus);
        event.setAmount(newAmount);
    }
}