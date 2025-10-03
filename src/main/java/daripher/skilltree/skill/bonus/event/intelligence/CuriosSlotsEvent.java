package daripher.skilltree.skill.bonus.event.intelligence;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.intelligence.AmuletSlotBonus;
import daripher.skilltree.skill.bonus.player.intelligence.BeltSlotBonus;
import daripher.skilltree.skill.bonus.player.intelligence.BraceletSlotBonus;
import daripher.skilltree.skill.bonus.player.intelligence.NecklaceSlotBonus;
import daripher.skilltree.skill.bonus.player.intelligence.RingSlotBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class CuriosSlotsEvent {
    private static final UUID AMULET_UUID = UUID.fromString("c640acb2-54b6-433b-a7d1-e06853a3e9d5");
    private static final UUID RING_UUID = UUID.fromString("8e805ad4-0c17-405b-9149-bc46d6448789");
    private static final UUID BRACELET_UUID = UUID.fromString("ba0512ae-d098-4e19-9c8c-acce620033c9");
    private static final UUID BELT_UUID = UUID.fromString("7e2b8ffa-9004-43a7-bf33-eee1a88ba638");
    private static final UUID NECKLACE_UUID = UUID.fromString("144d6dbd-64af-4728-b3e9-2f7d62b7fb64");

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side.isClient()) return;

        if (!ModList.get().isLoaded("curios")) return;

        if (!(event.player instanceof ServerPlayer player)) return;
        if (!player.isAlive()) return;

        CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            applySlotModifier(handler, AmuletSlotBonus.getSlotType(), AmuletSlotBonus.class, AMULET_UUID);
            applySlotModifier(handler, RingSlotBonus.getSlotType(), RingSlotBonus.class, RING_UUID);
            applySlotModifier(handler, BraceletSlotBonus.getSlotType(), BraceletSlotBonus.class, BRACELET_UUID);
            applySlotModifier(handler, BeltSlotBonus.getSlotType(), BeltSlotBonus.class, BELT_UUID);
            applySlotModifier(handler, NecklaceSlotBonus.getSlotType(), NecklaceSlotBonus.class, NECKLACE_UUID);
        });
    }

    private static void applySlotModifier(top.theillusivec4.curios.api.type.capability.ICuriosItemHandler handler, String slotType, Class<?> bonusClass, UUID uuid) {
        double bonus = PlayerSkillsProvider.get((ServerPlayer) handler.getWearer()).getCachedBonus(bonusClass);
        if (bonus == 0) {
            handler.removeSlotModifier(slotType, uuid);
            return;
        }

        int amount = (int) bonus;
        handler.removeSlotModifier(slotType, uuid);
        handler.addTransientSlotModifier(slotType, uuid, "SkillTree Bonus", amount, Operation.ADDITION);
    }
}