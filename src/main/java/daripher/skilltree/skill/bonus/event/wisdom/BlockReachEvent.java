package daripher.skilltree.skill.bonus.event.wisdom;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.wisdom.BlockReachBonus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class BlockReachEvent {
    private static final UUID BLOCK_REACH_UUID = UUID.fromString("a2234527-b890-cdef-0123-456789abcdef");
    private static final ResourceLocation BLOCK_REACH_ID = new ResourceLocation(SkillTreeMod.MOD_ID, "block_reach_bonus");

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side.isClient()) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        if (!player.isAlive()) return;

        AttributeInstance attr = player.getAttribute(ForgeMod.BLOCK_REACH.get());
        if (attr == null) return;

        attr.removeModifier(BLOCK_REACH_UUID);

        double bonus = PlayerSkillsProvider.get(player).getCachedBonus(BlockReachBonus.class);
        if (bonus == 0) return;

        attr.addTransientModifier(new AttributeModifier(
                BLOCK_REACH_UUID,
                BLOCK_REACH_ID.toString(),
                bonus,
                AttributeModifier.Operation.ADDITION
        ));
    }
}