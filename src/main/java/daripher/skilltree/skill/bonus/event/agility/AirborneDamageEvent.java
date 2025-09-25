package daripher.skilltree.skill.bonus.event.agility;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.player.agility.AirborneDamageBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class AirborneDamageEvent {
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            if (!player.onGround()) {
                double totalBonus = PlayerSkillsProvider.get(player).getCachedBonus(AirborneDamageBonus.class);
                if (totalBonus == 0) return;

                float damageIncrease = (float) totalBonus;

                float newDamage = event.getAmount() * (1 + damageIncrease);

                event.setAmount(newDamage);
            }
        }
    }
}