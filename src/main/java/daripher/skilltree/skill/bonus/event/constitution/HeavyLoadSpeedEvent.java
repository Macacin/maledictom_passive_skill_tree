package daripher.skilltree.skill.bonus.event.constitution;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.constitution.HeavyLoadSpeedBonus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import com.jabroni.weightmod.capability.WeightCapabilities;
import com.jabroni.weightmod.config.WeightConfig;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class HeavyLoadSpeedEvent {
    private static final UUID HEAVY_LOAD_MOVEMENT_UUID = UUID.fromString("b1234117-c89d-def0-1234-51189abcdef1");
    private static final UUID HEAVY_LOAD_ROLL_UUID = UUID.fromString("c2245678-d90e-ef01-2345-2289abcdef12");
    private static Attribute ROLL_DISTANCE_ATTR = null;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side.isClient()) return;
        ServerPlayer player = (ServerPlayer) event.player;

        if (!ModList.get().isLoaded("weightmod")) return;

        AttributeInstance movementAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementAttr != null) {
            movementAttr.removeModifier(HEAVY_LOAD_MOVEMENT_UUID);
        }

        if (ModList.get().isLoaded("combatroll")) {
            if (ROLL_DISTANCE_ATTR == null) {
                ROLL_DISTANCE_ATTR = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("combatroll", "distance"));
            }
            if (ROLL_DISTANCE_ATTR != null) {
                AttributeInstance rollAttr = player.getAttribute(ROLL_DISTANCE_ATTR);
                if (rollAttr != null) {
                    rollAttr.removeModifier(HEAVY_LOAD_ROLL_UUID);
                }
            }
        }

        double heavyLoadBonus = PlayerSkillsProvider.get(player).getCachedBonus(HeavyLoadSpeedBonus.class);
        if (heavyLoadBonus == 0) return;

        int level = getOverloadLevel(player);
        if (level != 4) return;

        if (movementAttr != null) {
            movementAttr.addTransientModifier(new AttributeModifier(
                    HEAVY_LOAD_MOVEMENT_UUID,
                    "Heavy Load Speed Bonus",
                    heavyLoadBonus,
                    AttributeModifier.Operation.MULTIPLY_BASE
            ));
        }

        if (ModList.get().isLoaded("combatroll") && ROLL_DISTANCE_ATTR != null) {
            AttributeInstance rollAttr = player.getAttribute(ROLL_DISTANCE_ATTR);
            if (rollAttr != null) {
                rollAttr.addTransientModifier(new AttributeModifier(
                        HEAVY_LOAD_ROLL_UUID,
                        "Heavy Load Roll Bonus",
                        heavyLoadBonus,
                        AttributeModifier.Operation.MULTIPLY_BASE
                ));
            }
        }
    }

    private static int getOverloadLevel(Player player) {
        return player.getCapability(WeightCapabilities.CAPABILITY).map(cap -> {
            int totalWeight = calculateArmorWeight(player);
            double percentage = cap.getCapacity() > 0 ? (double) totalWeight / cap.getCapacity() : 0;
            if (percentage <= WeightConfig.LEVEL1_THRESHOLD.get()) return 1;
            if (percentage <= WeightConfig.LEVEL2_THRESHOLD.get()) return 2;
            if (percentage <= WeightConfig.LEVEL3_THRESHOLD.get()) return 3;
            return 4;
        }).orElse(0);
    }

    private static int calculateArmorWeight(Player player) {
        int totalWeight = 0;
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.getItem() instanceof ArmorItem armor) {
                int defense = armor.getDefense();
                double coef = getArmorCoef(stack);
                double x = getArmorX(stack);
                double weight = (Math.pow(defense + x, 1.25)) * coef;
                totalWeight += (int) Math.round(weight);
            }
        }
        return totalWeight;
    }

    private static double getArmorCoef(ItemStack stack) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (id == null) return 1.0;
        String itemId = id.toString();

        if (WeightConfig.LIGHT_POOL.get().contains(itemId)) {
            return WeightConfig.LIGHT_COEF.get();
        } else if (WeightConfig.MEDIUM_POOL.get().contains(itemId)) {
            return WeightConfig.MEDIUM_COEF.get();
        } else if (WeightConfig.HEAVY_POOL.get().contains(itemId)) {
            return WeightConfig.HEAVY_COEF.get();
        } else if (WeightConfig.VERY_HEAVY_POOL.get().contains(itemId)) {
            return WeightConfig.VERY_HEAVY_COEF.get();
        } else if (WeightConfig.EXTRA_LARGE_POOL.get().contains(itemId)) {
            return WeightConfig.VERY_HEAVY_COEF.get();
        }
        return 1.0;
    }

    private static double getArmorX(ItemStack stack) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (id == null) return 1.0;
        String itemId = id.toString();

        if (WeightConfig.LIGHT_POOL.get().contains(itemId)) {
            return WeightConfig.LIGHT_X.get();
        } else if (WeightConfig.MEDIUM_POOL.get().contains(itemId)) {
            return WeightConfig.MEDIUM_X.get();
        } else if (WeightConfig.HEAVY_POOL.get().contains(itemId)) {
            return WeightConfig.HEAVY_X.get();
        } else if (WeightConfig.VERY_HEAVY_POOL.get().contains(itemId)) {
            return WeightConfig.VERY_HEAVY_X.get();
        } else if (WeightConfig.EXTRA_LARGE_POOL.get().contains(itemId)) {
            return WeightConfig.EXTRA_LARGE_X.get();
        }
        return 1.0;
    }
}