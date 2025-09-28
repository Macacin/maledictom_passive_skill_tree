package daripher.skilltree.skill.bonus.event.constitution;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.constitution.FullArmorSetBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class ArmorEvent {
    private static final UUID FULL_ARMOR_SET_BONUS_UUID = UUID.fromString("a1234567-b89c-def0-1234-56789abcdef0");
    private static final EquipmentSlot[] ARMOR_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side.isClient()) return;
        ServerPlayer player = (ServerPlayer) event.player;
        if (!player.isAlive() || player.isDeadOrDying()) return;

        AttributeInstance armorAttr = player.getAttribute(Attributes.ARMOR);
        if (armorAttr != null) {
            armorAttr.removeModifier(FULL_ARMOR_SET_BONUS_UUID);
        }

        double bonus = PlayerSkillsProvider.get(player).getCachedBonus(FullArmorSetBonus.class);
        if (bonus == 0) return;

        if (!hasFullArmorSet(player)) return;

        int currentArmor = player.getArmorValue();
        double extraArmor = currentArmor * bonus;

        if (armorAttr != null) {
            armorAttr.addTransientModifier(new AttributeModifier(
                    FULL_ARMOR_SET_BONUS_UUID,
                    "Full Armor Set Bonus",
                    extraArmor,
                    AttributeModifier.Operation.ADDITION
            ));
        }
    }

    private static boolean hasFullArmorSet(ServerPlayer player) {
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            if (!(player.getItemBySlot(slot).getItem() instanceof ArmorItem)) {
                return false;
            }
        }
        return true;
    }
}