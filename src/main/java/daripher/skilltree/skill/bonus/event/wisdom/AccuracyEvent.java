package daripher.skilltree.skill.bonus.event.wisdom;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.wisdom.AccuracyBonus;
import daripher.skilltree.config.Config;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class AccuracyEvent {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side.isClient()) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        if (!player.isAlive()) return;

        double bonus = PlayerSkillsProvider.get(player).getCachedBonus(AccuracyBonus.class);
        if (bonus == 0) return;

        double newAccuracy = Config.getBaseAccuracy() + bonus;
        PlayerSkillsProvider.get(player).setAccuracy(newAccuracy);
    }
}