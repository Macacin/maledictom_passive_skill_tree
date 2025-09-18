package daripher.skilltree.generation;

import com.google.gson.JsonObject;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.config.CraftConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class CraftTierGenerator {

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        CraftConfig config = CraftConfig.INSTANCE;
        if (!config.recipeTiers.isEmpty()) {
            return;
        }

        MinecraftServer server = event.getServer();
        var recipeManager = server.getRecipeManager();

        // Получаем Stream всех crafting-рецептов (публичный метод)
        var recipeStream = recipeManager.getAllRecipesFor(RecipeType.CRAFTING);

        // Собираем в Map: ID -> tier 0
        Map<ResourceLocation, Integer> defaultTiers = recipeStream.stream()
                .collect(Collectors.toMap(
                        Recipe::getId,
                        recipe -> 0,
                        (existing, replacement) -> existing,
                        HashMap::new
                ));

        config.save(defaultTiers);
        config.load();
    }
}