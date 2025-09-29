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
        String side = player.level().isClientSide() ? "client" : "server";
        int foodLevel = player.getFoodData().getFoodLevel();

        // Always log event fire and current state for debug
        System.out.println("Full hunger damage event fired on " + side + ": bonus=" + bonus + ", current food level=" + foodLevel + " for player " + player.getName().getString());

        if (bonus == 0) {
            System.out.println("No bonus available on " + side + " for player " + player.getName().getString());
            return;
        }

        // Apply only if full hunger
        if (foodLevel == 20) {
            float originalDamage = event.getAmount();
            float newDamage = originalDamage * (1f + (float) bonus);
            event.setAmount(newDamage);

            // Success log
            System.out.println("Full hunger damage bonus applied on " + side + ": original=" + originalDamage + ", new=" + newDamage + ", bonus=" + bonus + " for player " + player.getName().getString());
        } else {
            // Failure log for debug
            System.out.println("Hunger not full on " + side + " (food level=" + foodLevel + ") - bonus not applied for player " + player.getName().getString());
        }
    }
}