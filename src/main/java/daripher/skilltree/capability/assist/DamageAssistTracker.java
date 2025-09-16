package daripher.skilltree.capability.assist;

import net.minecraft.nbt.CompoundTag;

import java.util.Map;
import java.util.UUID;

public interface DamageAssistTracker {
    Map<UUID, Boolean> getAssistedPlayers();

    void markAssisted(UUID playerUUID);

    boolean hasAssisted(UUID playerUUID);

    void clear();  // Clear after death

    CompoundTag serializeNBT();
    void deserializeNBT(CompoundTag tag);
}
