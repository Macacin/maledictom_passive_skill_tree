package daripher.skilltree.skill.bonus.event.constitution;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.constitution.ShieldBlockBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class ShieldBlockEventHandler {
    @SubscribeEvent
    public static void onShieldBlock(ShieldBlockEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        double bonus = PlayerSkillsProvider.get(player).getCachedBonus(ShieldBlockBonus.class);
        if (bonus == 0) return;
        float newBlocked = event.getBlockedDamage() * (float) (1 + bonus);
        event.setBlockedDamage(newBlocked);
    }
}