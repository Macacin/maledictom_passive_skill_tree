package daripher.skilltree.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import daripher.skilltree.SkillTreeMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager; // Оставляем, если используется где-то ещё, но не используем

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class CraftConfig {
    private static final String FILE_NAME = "craft_tiers.json";
    private static final Gson GSON = new Gson();
    public static final CraftConfig INSTANCE = new CraftConfig();

    public final Map<ResourceLocation, Integer> recipeTiers = new HashMap<>();
    private final Path configFile;

    private CraftConfig() {
        Path configDir = FMLPaths.CONFIGDIR.get().resolve("skilltree");
        try {
            Files.createDirectories(configDir);
        } catch (IOException ignored) {
        }
        this.configFile = configDir.resolve(FILE_NAME);
        this.load();
    }

    public void load() {
        recipeTiers.clear();
        if (!Files.exists(configFile)) {
            return;
        }
        try {
            String jsonContent = Files.readString(configFile);
            JsonElement json = JsonParser.parseString(jsonContent);
            if (json.isJsonObject()) {
                JsonObject obj = json.getAsJsonObject();
                obj.entrySet().forEach(e -> {
                    ResourceLocation recipeId = ResourceLocation.tryParse(e.getKey());
                    if (recipeId != null) {
                        int tier = e.getValue().getAsInt();
                        if (tier >= 0 && tier <= 4) {
                            recipeTiers.put(recipeId, tier);
                        }
                    }
                });
            }
        } catch (IOException | IllegalStateException ignored) {
        }
    }

    public int getTier(ResourceLocation recipeId) {
        return recipeTiers.getOrDefault(recipeId, 0);
    }

    public void save(Map<ResourceLocation, Integer> newTiers) {
        JsonObject obj = new JsonObject();
        newTiers.forEach((id, tier) -> obj.addProperty(id.toString(), tier));
        try {
            Files.writeString(configFile, GSON.toJson(obj));
        } catch (IOException ignored) {
        }
    }

    public static void reload() {
        INSTANCE.load();
    }
}