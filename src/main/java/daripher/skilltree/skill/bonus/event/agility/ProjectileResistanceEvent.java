package daripher.skilltree.skill.bonus.event.agility;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.player.agility.ProjectileResistanceBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class ProjectileResistanceEvent {
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (event.getSource().getDirectEntity() instanceof AbstractArrow) {
                float totalResistance = 0;
                for (PassiveSkill skill : PlayerSkillsProvider.get(player).getPlayerSkills()) {
                    for (SkillBonus<?> bonus : skill.getBonuses()) {
                        if (bonus instanceof ProjectileResistanceBonus resistanceBonus) {
                            float amount = resistanceBonus.getResistanceBonus(player);
                            if (resistanceBonus.operation == AttributeModifier.Operation.MULTIPLY_BASE) {
                                totalResistance += amount;
                            }
                        }
                    }
                }

                totalResistance = Math.min(totalResistance, 0.7f);

                float damageReduction = totalResistance;
                float newDamage = event.getAmount() * (1 - damageReduction);

                event.setAmount(newDamage);
            }
        }
    }
}