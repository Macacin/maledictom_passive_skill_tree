package daripher.skilltree.skill.bonus.event.endurance;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.endurance.MiningSpeedBonus;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MiningSpeedEvent {
    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        if (player == null || !player.isAlive() || player.isDeadOrDying()) return;

        double bonus = PlayerSkillsProvider.get(player).getCachedBonus(MiningSpeedBonus.class);

        if (bonus == 0) return;

        float originalSpeed = event.getNewSpeed();
        float newSpeed = originalSpeed * (1f + (float) bonus);
        event.setNewSpeed(newSpeed);
    }
}