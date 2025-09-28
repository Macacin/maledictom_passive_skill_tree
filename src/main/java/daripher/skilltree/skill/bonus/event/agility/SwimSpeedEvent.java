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
        if (!player.isAlive() || player.isDeadOrDying()) return;

        double vanillaBase = 1.0D;

        double totalAddition = 0;  // Если есть ADDITION-бонусы, добавь кэш для них
        double totalMultiplier = PlayerSkillsProvider.get(player).getCachedBonus(SwimSpeedBonus.class);  // Sum для MULTIPLY_BASE
        if (totalMultiplier == 0) return;  // Оптимизация

        double newValue = vanillaBase * (1 + totalMultiplier) + totalAddition;

        player.getAttribute(ForgeMod.SWIM_SPEED.get()).setBaseValue(newValue);
    }
}