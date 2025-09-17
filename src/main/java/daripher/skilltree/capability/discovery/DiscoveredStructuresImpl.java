package daripher.skilltree.capability.discovery;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.HashSet;
import java.util.Set;

public class DiscoveredStructuresImpl implements DiscoveredStructures {
    private final Set<String> discoveredChunks = new HashSet<>();

    @Override
    public Set<String> getDiscoveredChunks() {
        return new HashSet<>(discoveredChunks);
    }

    @Override
    public void addDiscoveredChunk(String chunkKey) {
        discoveredChunks.add(chunkKey);
    }

    @Override
    public boolean isDiscovered(String chunkKey) {
        return discoveredChunks.contains(chunkKey);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        discoveredChunks.forEach(key -> list.add(StringTag.valueOf(key)));  // StringTag for keys
        tag.put("DiscoveredChunks", list);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        discoveredChunks.clear();
        ListTag list = tag.getList("DiscoveredChunks", Tag.TAG_STRING);  // TAG_STRING for strings
        for (Tag t : list) {
            discoveredChunks.add(t.getAsString());
        }
    }
}