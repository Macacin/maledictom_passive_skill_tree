package daripher.skilltree.skill.bonus.event.agility;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.player.agility.SwimSpeedBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class SwimSpeedEvent {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side.isClient()) return;
        ServerPlayer player = (ServerPlayer) event.player;


        double vanillaBase = 1.0D;

        double totalAddition = 0;
        double totalMultiplier = 0;
        for (PassiveSkill skill : PlayerSkillsProvider.get(player).getPlayerSkills()) {
            for (SkillBonus<?> bonus : skill.getBonuses()) {
                if (bonus instanceof SwimSpeedBonus swimBonus) {
                    float amount = swimBonus.getSpeedBonus(player);
                    if (swimBonus.operation == AttributeModifier.Operation.ADDITION) {
                        totalAddition += amount;
                    } else if (swimBonus.operation == AttributeModifier.Operation.MULTIPLY_BASE) {
                        totalMultiplier += amount;
                    }
                }
            }
        }

        double newValue = vanillaBase * (1 + totalMultiplier) + totalAddition;

        player.getAttribute(ForgeMod.SWIM_SPEED.get()).setBaseValue(newValue);
    }
}