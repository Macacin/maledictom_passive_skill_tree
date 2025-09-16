package daripher.skilltree.capability.grind;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import java.util.HashMap;
import java.util.Map;

public interface GrindTracker {
    Map<String, Long> getLastActionTimes();

    void updateLastTime(Player player, String mobType);

    boolean isGrind(Player player, String mobType);

    void clearGrind(String mobType);

    CompoundTag serializeNBT();

    void deserializeNBT(CompoundTag tag);

    int getStreakCount(String type);


    double getPenaltyMultiplier(String type);

    double getLastXP(String type);

    void setLastXP(String type, double xp);

}
