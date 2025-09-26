package daripher.skilltree.skill.bonus.event.constitution;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.constitution.ShieldRegenerationBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class ShieldRegenerationEvent {
    private static final int TICKS_PER_SECOND = 20;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side.isClient()) return;
        ServerPlayer player = (ServerPlayer) event.player;
        if (!player.isBlocking()) return;
        if (player.getHealth() >= player.getMaxHealth()) return;

        double bonus = PlayerSkillsProvider.get(player).getCachedBonus(ShieldRegenerationBonus.class);
        if (bonus == 0) return;

        if (player.tickCount % TICKS_PER_SECOND == 0) {
            float regenAmount = (float) bonus;
            player.heal(regenAmount);
        }
    }
}