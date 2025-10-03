package daripher.skilltree.skill.bonus.event.intelligence;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.intelligence.CraftingMaterialSaveBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class CraftingMaterialSaveEvent {
    private static final Random RANDOM = new Random();

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.level().isClientSide) return;

        double chance = PlayerSkillsProvider.get(player).getCachedBonus(CraftingMaterialSaveBonus.class);
        if (chance == 0) return;

        Container matrix = event.getInventory();
        Level level = player.level();

        for (int slot = 0; slot < matrix.getContainerSize(); slot++) {
            ItemStack stack = matrix.getItem(slot);
            if (stack.isEmpty()) continue;

            if (RANDOM.nextDouble() < chance) {
                ItemStack savedStack = stack.copy();
                savedStack.setCount(1); 
                if (!player.getInventory().add(savedStack)) {
                    player.drop(savedStack, false);
                }
            }
        }
    }
}