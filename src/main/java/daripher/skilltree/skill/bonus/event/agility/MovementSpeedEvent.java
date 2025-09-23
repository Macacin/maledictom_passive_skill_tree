package daripher.skilltree.skill.bonus.event.agility;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.agility.MovementSpeedBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class MovementSpeedEvent {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side.isClient()) return;
        ServerPlayer player = (ServerPlayer) event.player;
        double totalBonus = PlayerSkillsProvider.get(player).getPlayerSkills().stream()
                .flatMap(skill -> skill.getBonuses().stream())
                .filter(bonus -> bonus instanceof MovementSpeedBonus)
                .mapToDouble(speedBonus -> ((MovementSpeedBonus) speedBonus).getSpeedBonus(player))
                .sum();
        double originalBase = 0.1D;
        double newValue = originalBase * (1 + totalBonus);
        player.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(newValue);
    }
}
