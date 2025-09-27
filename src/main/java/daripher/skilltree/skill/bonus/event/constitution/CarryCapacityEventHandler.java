package daripher.skilltree.skill.bonus.event.constitution;

import com.jabroni.weightmod.event.CarryCapacityCalculationEvent;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.constitution.CarryCapacityBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModList;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class CarryCapacityEventHandler {
    @SubscribeEvent
    public static void onCarryCapacityCalculation(CarryCapacityCalculationEvent event) {
        if (!ModList.get().isLoaded("weightmod")) return;
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;
        double bonus = PlayerSkillsProvider.get(player).getCachedBonus(CarryCapacityBonus.class);
        if (bonus == 0) return;
        event.setCapacity(event.getCapacity() + (int) bonus);
    }
}