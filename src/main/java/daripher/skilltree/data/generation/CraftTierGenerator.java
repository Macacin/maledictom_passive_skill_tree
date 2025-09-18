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
            System.out.println("DEBUG: Craft tiers already loaded, skipping generation.");
            return;
        }

        MinecraftServer server = event.getServer();
        var recipeManager = server.getRecipeManager();

        // Получаем Stream всех crafting-рецептов (публичный метод)
        var recipeStream = recipeManager.getAllRecipesFor(RecipeType.CRAFTING);

        // Собираем в Map: ID -> tier 0
        Map<ResourceLocation, Integer> defaultTiers = recipeStream.stream()
                .collect(Collectors.toMap(
                        Recipe::getId,  // Ключ: ResourceLocation из рецепта
                        recipe -> 0,    // Значение: 0 по умолчанию
                        (existing, replacement) -> existing,  // Merger на случай дубликатов (редко)
                        HashMap::new    // Collector в HashMap
                ));

        config.save(defaultTiers);
        config.load();  // Перезагружаем, чтобы подхватить сгенерированный конфиг

        System.out.println("INFO: Generated default craft tiers config with " + defaultTiers.size() + " recipes (all tier 0). Edit config/skilltree/craft_tiers.json to customize tiers.");
    }
}