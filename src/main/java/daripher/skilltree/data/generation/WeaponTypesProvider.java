package daripher.skilltree.data.generation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import daripher.skilltree.SkillTreeMod;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class WeaponTypesProvider implements DataProvider {
    private final PackOutput packOutput;

    public WeaponTypesProvider(PackOutput packOutput) {
        this.packOutput = packOutput;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        JsonObject json = new JsonObject();
        json.add("sword", new JsonArray());
        json.add("axe", new JsonArray());
        json.add("hammer", new JsonArray());
        json.add("trident", new JsonArray());
        json.add("dagger", new JsonArray());
        json.add("scythe", new JsonArray());
        json.add("chakram", new JsonArray());

        Path path = packOutput.getOutputFolder().resolve("data/" + SkillTreeMod.MOD_ID + "/weapon_types/types.json");  // Фикс: save as types.json inside weapon_types folder
        return DataProvider.saveStable(cache, json, path);
    }

    @Override
    public @NotNull String getName() {
        return "Weapon Types Provider";
    }
}