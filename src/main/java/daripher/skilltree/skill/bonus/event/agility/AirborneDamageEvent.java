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
        // Проверяем, что атакующим является игрок
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            // Проверяем, находится ли игрок в воздухе
            if (!player.onGround()) {
                // Суммируем бонусы
                float totalBonus = 0;
                for (PassiveSkill skill : PlayerSkillsProvider.get(player).getPlayerSkills()) {
                    for (SkillBonus<?> bonus : skill.getBonuses()) {
                        if (bonus instanceof AirborneDamageBonus damageBonus) {
                            float amount = damageBonus.getDamageBonus(player);
                            if (damageBonus.operation == AttributeModifier.Operation.MULTIPLY_BASE) {
                                totalBonus += amount;
                            }
                        }
                    }
                }

                // Устанавливаем кап в 50% (опционально, можно убрать или изменить)
                float damageIncrease = totalBonus;

                // Увеличиваем урон
                float newDamage = event.getAmount() * (1 + damageIncrease);

                // Применяем новый урон
                event.setAmount(newDamage);

                // Лог для отладки (опционально)
                System.out.println("Airborne damage increased from " + event.getAmount() / (1 + damageIncrease) + " to " + newDamage + ", Bonus: " + (damageIncrease * 100) + "%");
            }
        }
    }
}