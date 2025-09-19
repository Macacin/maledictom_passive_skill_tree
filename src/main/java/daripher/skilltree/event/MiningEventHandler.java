package daripher.skilltree.event;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.MiningXPUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MiningEventHandler {
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;

        BlockState state = event.getState();
        double b = MiningXPUtil.getOreB(state.getBlock());
        if (b > 0.0) {
            int level = MiningXPUtil.getPlayerLevel(player);
            double xp = MiningXPUtil.calculateXP(b, level);
            if (xp > 0) {
                MiningXPUtil.addXP(player, xp);
            }
        }
    }
}