package daripher.skilltree.skill.bonus.event.endurance;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.endurance.MaxHealthBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class MaxHealthEvent {
    private static final UUID MAX_HEALTH_BONUS_UUID = UUID.fromString("a1233367-b89c-def0-1234-56339abcdef0"); // Unique UUID for this bonus

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side.isClient()) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        if (!player.isAlive() || player.isDeadOrDying()) return;

        double bonus = PlayerSkillsProvider.get(player).getCachedBonus(MaxHealthBonus.class);

        AttributeInstance attribute = player.getAttribute(Attributes.MAX_HEALTH);
        if (attribute == null) return;

        AttributeModifier existingModifier = attribute.getModifier(MAX_HEALTH_BONUS_UUID);
        double existingBonus = (existingModifier != null) ? existingModifier.getAmount() : 0;

        if (bonus == existingBonus) return;

        attribute.removeModifier(MAX_HEALTH_BONUS_UUID);

        if (bonus != 0) {
            float prevMaxHealth = player.getMaxHealth();
            attribute.addTransientModifier(new AttributeModifier(
                    MAX_HEALTH_BONUS_UUID,
                    "Endurance Max Health Bonus",
                    bonus,
                    AttributeModifier.Operation.ADDITION
            ));

            float newMaxHealth = player.getMaxHealth();
            if (newMaxHealth > prevMaxHealth && player.getHealth() > 0) {
                player.heal(newMaxHealth - prevMaxHealth);
            } else if (newMaxHealth < prevMaxHealth) {
                if (player.getHealth() > newMaxHealth) {
                    player.setHealth(newMaxHealth);
                }
            }
        }
    }
}