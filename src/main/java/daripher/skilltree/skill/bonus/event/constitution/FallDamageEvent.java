package daripher.skilltree.skill.bonus.event.constitution;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.constitution.FallDamageResistanceBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class FallDamageEvent {
    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        double bonus = PlayerSkillsProvider.get(player).getCachedBonus(FallDamageResistanceBonus.class);
        if (bonus == 0) return;
        float newDamageMultiplier = event.getDamageMultiplier() * (float) (1 - bonus);
        event.setDamageMultiplier(newDamageMultiplier);
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!event.getSource().is(DamageTypes.FLY_INTO_WALL)) return;
        double bonus = PlayerSkillsProvider.get(player).getCachedBonus(FallDamageResistanceBonus.class);
        if (bonus == 0) return;
        event.setAmount(event.getAmount() * (float) (1 - bonus));
    }
}