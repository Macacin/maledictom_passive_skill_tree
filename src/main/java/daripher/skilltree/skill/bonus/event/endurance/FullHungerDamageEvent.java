package daripher.skilltree.skill.bonus.event.endurance;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.endurance.FullHungerDamageBonus;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FullHungerDamageEvent {
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof Player player)) return;
        if (!player.isAlive()) return;

        double bonus = PlayerSkillsProvider.get(player).getCachedBonus(FullHungerDamageBonus.class);
        int foodLevel = player.getFoodData().getFoodLevel();

        if (bonus == 0) {
            return;
        }
        if (foodLevel == 20) {
            float originalDamage = event.getAmount();
            float newDamage = originalDamage * (1f + (float) bonus);
            event.setAmount(newDamage);
        }
    }
}