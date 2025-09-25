package daripher.skilltree.skill.bonus.event.agility;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.agility.AttackSpeedBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class AttackSpeedEvent {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side.isClient()) return;
        ServerPlayer player = (ServerPlayer) event.player;
        double totalBonus = PlayerSkillsProvider.get(player).getCachedBonus(AttackSpeedBonus.class);
        if (totalBonus == 0) return;

        double originalBase = 4.0D;
        double newValue = originalBase * (1 + totalBonus);
        player.getAttribute(Attributes.ATTACK_SPEED).setBaseValue(newValue);
    }
}