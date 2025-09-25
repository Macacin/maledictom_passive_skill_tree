package daripher.skilltree.skill.bonus.event;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.player.agility.SprintDamageBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class SprintDamageEvent {
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            if (player.isSprinting()) {
                double totalBonus = PlayerSkillsProvider.get(player).getCachedBonus(SprintDamageBonus.class);
                if (totalBonus == 0) return;  // Оптимизация

                float damageIncrease = (float) totalBonus;  // Sum amount для MULTIPLY_BASE

                float newDamage = event.getAmount() * (1 + damageIncrease);

                event.setAmount(newDamage);
            }
        }
    }
}