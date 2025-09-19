package daripher.skilltree.capability.grind;

import daripher.skilltree.config.Config;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public class GrindTrackerImpl implements GrindTracker {
    private final Map<String, Long> lastTicks = new HashMap<>();
    private final Map<String, Integer> streakCounts = new HashMap<>();
    private final Map<String, Double> lastXP = new HashMap<>();

    @Override
    public Map<String, Long> getLastActionTimes() {
        return new HashMap<>(lastTicks);
    }

    @Override
    public void updateLastTime(Player player, String mobType) {
        long nowTicks = player.level().getGameTime();
        long lastTicksValue = lastTicks.getOrDefault(mobType, 0L);
        long diffTicks = nowTicks - lastTicksValue;
        int windowTicks = Config.getGrindTimeWindow() * 20;
        boolean inWindow = diffTicks < windowTicks;

        if (!inWindow) {
            int newStreak = 1;
            streakCounts.put(mobType, newStreak);
            lastTicks.put(mobType, nowTicks);
            lastXP.put(mobType, 0.0);
            return;
        }

        int current = getStreakCount(mobType);
        int newStreak = Math.min(current + 1, 20);
        streakCounts.put(mobType, newStreak);
        lastTicks.put(mobType, nowTicks);
    }

    @Override
    public boolean isGrind(Player player, String mobType) {
        long nowTicks = player.level().getGameTime();
        long lastTicksValue = lastTicks.getOrDefault(mobType, 0L);
        long diffTicks = nowTicks - lastTicksValue;
        int windowTicks = Config.getGrindTimeWindow() * 20;
        return diffTicks < windowTicks;
    }

    @Override
    public int getStreakCount(String type) {
        return streakCounts.getOrDefault(type, 0);
    }

    @Override
    public double getPenaltyMultiplier(String type) {
        int streak = getStreakCount(type);
        int threshold = Config.getGrindStreakThreshold();
        if (streak < threshold) return 1.0;

        double initialPenalty = Config.getGrindInitialPenalty();
        double maxPenalty = Config.getGrindMaxPenalty();
        int maxStreakLength = Config.getGrindMaxStreakLength();
        double progress = Math.min((streak - threshold) / (double) maxStreakLength, 1.0);
        double penalty = initialPenalty + (maxPenalty - initialPenalty) * progress;
        double multiplier = 1.0 - penalty;
        return Math.max(multiplier, Config.getGrindMinMultiplier());
    }

    @Override
    public void clearGrind(String mobType) {
        lastTicks.remove(mobType);
        streakCounts.remove(mobType);
        lastXP.remove(mobType);
    }

    @Override
    public double getLastXP(String type) {
        return lastXP.getOrDefault(type, 0.0);
    }

    @Override
    public void setLastXP(String type, double xp) {
        lastXP.put(type, xp);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag lastTicksList = new ListTag();
        lastTicks.forEach((type, ticks) -> {
            CompoundTag entry = new CompoundTag();
            entry.putString("Type", type);
            entry.putLong("Ticks", ticks);
            lastTicksList.add(entry);
        });
        tag.put("LastTicks", lastTicksList);

        ListTag streakList = new ListTag();
        streakCounts.forEach((type, count) -> {
            CompoundTag entry = new CompoundTag();
            entry.putString("Type", type);
            entry.putInt("Count", count);
            streakList.add(entry);
        });
        tag.put("StreakCounts", streakList);

        ListTag xpList = new ListTag();
        lastXP.forEach((type, xp) -> {
            CompoundTag entry = new CompoundTag();
            entry.putString("Type", type);
            entry.putDouble("LastXP", xp);
            xpList.add(entry);
        });
        tag.put("LastXP", xpList);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        lastTicks.clear();
        streakCounts.clear();
        lastXP.clear();

        ListTag lastTicksList = tag.getList("LastTicks", Tag.TAG_COMPOUND);
        for (Tag t : lastTicksList) {
            CompoundTag entry = (CompoundTag) t;
            String type = entry.getString("Type");
            long ticks = entry.getLong("Ticks");
            lastTicks.put(type, ticks);
        }

        ListTag streakList = tag.getList("StreakCounts", Tag.TAG_COMPOUND);
        for (Tag t : streakList) {
            CompoundTag entry = (CompoundTag) t;
            String type = entry.getString("Type");
            int count = entry.getInt("Count");
            streakCounts.put(type, count);
        }

        ListTag xpList = tag.getList("LastXP", Tag.TAG_COMPOUND);
        for (Tag t : xpList) {
            CompoundTag entry = (CompoundTag) t;
            String type = entry.getString("Type");
            double xp = entry.getDouble("LastXP");
            lastXP.put(type, xp);
        }
    }
}