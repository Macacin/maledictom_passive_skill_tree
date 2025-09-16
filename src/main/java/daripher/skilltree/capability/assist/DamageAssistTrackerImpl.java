package daripher.skilltree.capability.assist;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DamageAssistTrackerImpl implements DamageAssistTracker {
    private final Map<UUID, Boolean> assistedPlayers = new HashMap<>();

    @Override
    public Map<UUID, Boolean> getAssistedPlayers() {
        return new HashMap<>(assistedPlayers);  // Copy for safety
    }

    @Override
    public void markAssisted(UUID playerUUID) {
        assistedPlayers.put(playerUUID, true);
    }

    @Override
    public boolean hasAssisted(UUID playerUUID) {
        return assistedPlayers.getOrDefault(playerUUID, false);
    }

    @Override
    public void clear() {
        assistedPlayers.clear();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        assistedPlayers.forEach((uuid, bool) -> {
            CompoundTag entry = new CompoundTag();
            entry.putString("UUID", uuid.toString());
            entry.putBoolean("Assisted", bool);
            list.add(entry);
        });
        tag.put("Assisted", list);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        assistedPlayers.clear();
        ListTag list = tag.getList("Assisted", Tag.TAG_COMPOUND);
        for (Tag t : list) {
            CompoundTag entry = (CompoundTag) t;
            UUID uuid = UUID.fromString(entry.getString("UUID"));
            boolean assisted = entry.getBoolean("Assisted");
            assistedPlayers.put(uuid, assisted);
        }
    }
}