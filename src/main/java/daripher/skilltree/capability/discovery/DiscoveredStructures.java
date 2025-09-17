package daripher.skilltree.capability.discovery;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Set;

public interface DiscoveredStructures {
    Set<String> getDiscoveredChunks();

    void addDiscoveredChunk(String chunkKey);

    boolean isDiscovered(String chunkKey);

    CompoundTag serializeNBT();
    void deserializeNBT(CompoundTag tag);
}