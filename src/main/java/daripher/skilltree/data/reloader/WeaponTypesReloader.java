package daripher.skilltree.data.reloader;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.SkillTreeMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class WeaponTypesReloader extends SimpleJsonResourceReloadListener {
    private static final ResourceLocation ID = new ResourceLocation(SkillTreeMod.MOD_ID, "types");
    private static Map<String, List<ResourceLocation>> WEAPON_TYPES = new HashMap<>();

    public WeaponTypesReloader() {
        super(new GsonBuilder().setLenient().create(), "weapon_types");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler) {
        JsonElement json = map.get(ID);
        if (json == null) return;
        try {
            JsonObject obj = json.getAsJsonObject();
            WEAPON_TYPES = new HashMap<>();
            obj.entrySet().forEach(entry -> {
                String type = entry.getKey();
                List<ResourceLocation> items = entry.getValue().getAsJsonArray().asList().stream()
                        .map(JsonElement::getAsString)
                        .map(ResourceLocation::new)
                        .collect(Collectors.toList());
                WEAPON_TYPES.put(type, items);
            });
        } catch (JsonParseException e) {
            SkillTreeMod.LOGGER.error("Error parsing weapon_types/types.json", e);
        }
    }

    public static List<ResourceLocation> getWeaponsForType(String type) {
        return WEAPON_TYPES.getOrDefault(type, List.of());
    }

    @SubscribeEvent
    public static void addReloadListener(AddReloadListenerEvent event) {
        event.addListener(new WeaponTypesReloader());
    }
}