package daripher.skilltree.event;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.network.NetworkDispatcher;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import daripher.skilltree.skill.CraftHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class CraftEventListener {

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (event.getEntity().level().isClientSide) return;

        var player = event.getEntity();
        var inventory = (CraftingContainer) event.getInventory();
        var level = player.level();

        // Получи RecipeManager
        var server = player.getServer();
        if (server == null) return;
        var recipeManager = server.getRecipeManager();

        // Простой lookup recipe по matrix и world (стандарт для Forge 1.20.1)
        Optional<? extends Recipe<?>> optionalRecipe = recipeManager.getRecipeFor(RecipeType.CRAFTING, inventory, level);
        if (optionalRecipe.isEmpty()) {
            System.out.println("DEBUG: No matching recipe for craft by " + player.getName().getString());
            return;
        }

        Recipe<?> recipe = optionalRecipe.get();
        ResourceLocation recipeId = recipe.getId();
        int xp = CraftHelper.calculateCraftXP(recipeId, player);
        if (xp <= 0) return;

        PlayerSkillsProvider.get(player).addSkillExperience(xp);

        // Sync
        if (player instanceof ServerPlayer serverPlayer) {
            SyncPlayerSkillsMessage msg = new SyncPlayerSkillsMessage(serverPlayer);
            NetworkDispatcher.network_channel.send(PacketDistributor.PLAYER.with(() -> serverPlayer), msg);
        }

        System.out.println("INFO: Granted " + xp + " craft XP for recipe " + recipeId + " to " + player.getName().getString());
    }
}