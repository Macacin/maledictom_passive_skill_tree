package daripher.skilltree.capability.skill;

import daripher.skilltree.config.Config;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.skill.PassiveSkill;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.player.agility.*;
import daripher.skilltree.skill.bonus.player.constitution.*;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nonnull;

public class PlayerSkills implements IPlayerSkills {
    private static final UUID TREE_VERSION = UUID.fromString("fd21c2a9-7ab5-4a1e-b06d-ddb87b56047f");

    private static final int BASE_LEVEL_COST = 18;
    private static final double GROWTH_FACTOR = 1.2;
//    private static final int MAX_LEVEL = 10000;

    private long lastCraftingXPTime = 0;
    private int consecutiveCraftingActions = 0;

    private long lastMiningXPTime = 0;
    private int consecutiveMiningActions = 0;

    private final NonNullList<PassiveSkill> skills = NonNullList.create();
    private int skillPoints;
    private double skillExperience = 0;
    private int currentLevel = 0;
    private boolean treeReset;

    private double accuracy = Config.getBaseAccuracy();

    private int agility = 0;
    private int constitution = 0;

    private final Map<Class<?>, Double> cachedBonuses = new HashMap<>();

    public void addSkillExperience(double amount) {
        if (amount <= 0) return;
        this.skillExperience += amount;
        checkForLevelUp();
    }

    private void checkForLevelUp() {
        while (true) {
            int nextCost = getNextLevelCost();
            if (skillExperience < nextCost) break;
            skillExperience -= nextCost;
            currentLevel++;
            skillPoints++;
            if (currentLevel >= Integer.MAX_VALUE / 2) break;
        }
    }


    public void setSkillExperience(double exp) {
        this.skillExperience = Math.max(0, exp);
    }

    public void setCurrentLevel(int lvl) {
        this.currentLevel = Math.max(0, lvl);
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getNextLevelCost() {
        int L = currentLevel + 1;
        double x;
        if (L >= 1 && L <= 30) {
            return (int) (15 * Math.pow(L, 1.05));
        } else if (L >= 31 && L <= 60) {
            x = 50 + (1.0 / 3) * (L - 30);
            return (int) (8.78 * Math.pow(x, 1.20) * Math.exp(0.02 * (x - 50)));
        } else if (L >= 61 && L <= 90) {
            x = 100 + (1.0 / 3) * (L - 60);
            return (int) (3.786 * Math.pow(x, 1.60) * Math.exp(0.005 * (x - 100)));
        } else {
            x = 150 + (1.0 / 3) * (L - 90);
            return (int) (0.2404 * Math.pow(x, 2.20) * Math.exp(0.0185 * (x - 150)));
        }
    }

    public double getSkillExperience() {
        return skillExperience;
    }

    @Override
    public boolean learnSkill(ServerPlayer player, @Nonnull PassiveSkill passiveSkill) {
        if (skillPoints <= 0 || skills.contains(passiveSkill)) return false;
        skillPoints--;
        boolean added = skills.add(passiveSkill);
        if (added) {
            passiveSkill.learn(player, true);
            if (passiveSkill.getTags().contains("Agility")) {
                agility++;
            }
            if (passiveSkill.getTags().contains("Constitution")) {
                constitution++;
            }
        }
        recalculateAllCachedBonuses();
        return added;
    }

    @Override
    public boolean isTreeReset() {
        return treeReset;
    }

    @Override
    public void setTreeReset(boolean reset) {
        treeReset = reset;
    }

    @Override
    public void resetTree(ServerPlayer player) {
        int refunded = getPlayerSkills().size();
        getPlayerSkills().forEach(skill -> skill.remove(player));
        getPlayerSkills().clear();
        skillPoints += refunded;
        agility = 0;
        constitution = 0;
        cachedBonuses.clear();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("TreeVersion", TREE_VERSION);
        tag.putInt("Points", skillPoints);
        tag.putDouble("Experience", skillExperience);
        tag.putInt("Level", currentLevel);
        tag.putBoolean("TreeReset", treeReset);
        tag.putLong("LastCraftingXPTime", lastCraftingXPTime);
        tag.putInt("ConsecutiveCraftingActions", consecutiveCraftingActions);
        tag.putLong("LastMiningXPTime", lastMiningXPTime);
        tag.putInt("ConsecutiveMiningActions", consecutiveMiningActions);
        tag.putDouble("Accuracy", accuracy);
        tag.putInt("Agility", agility);
        tag.putInt("Constitution", constitution);
        ListTag skillsTag = new ListTag();
        skills.forEach(skill -> skillsTag.add(StringTag.valueOf(skill.getId().toString())));
        tag.put("Skills", skillsTag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        skills.clear();
        skillPoints = tag.getInt("Points");
        skillExperience = tag.contains("Experience") ? tag.getDouble("Experience") : 0;
        currentLevel = tag.contains("Level") ? tag.getInt("Level") : 0;
        lastCraftingXPTime = tag.getLong("LastCraftingXPTime");
        consecutiveCraftingActions = tag.getInt("ConsecutiveCraftingActions");
        lastMiningXPTime = tag.getLong("LastMiningXPTime");
        consecutiveMiningActions = tag.getInt("ConsecutiveMiningActions");
        agility = tag.getInt("Agility");
        constitution = tag.getInt("Constitution");
        if (tag.contains("Accuracy")) {
            accuracy = tag.getDouble("Accuracy");
        } else {
            accuracy = Config.getBaseAccuracy();
        }
        ListTag skillsTag = tag.getList("Skills", Tag.TAG_STRING);
        System.out.println("Deserializing skills: points=" + skillPoints + ", exp=" + skillExperience + ", level=" + currentLevel);
        UUID treeVersion = tag.hasUUID("TreeVersion") ? tag.getUUID("TreeVersion") : null;
        for (Tag skillTag : skillsTag) {
            ResourceLocation skillId = new ResourceLocation(skillTag.getAsString());
            PassiveSkill passiveSkill = SkillsReloader.getSkillById(skillId);
            if (passiveSkill == null || passiveSkill.isInvalid()) {
                skills.clear();
                treeReset = true;
                skillPoints += skillsTag.size();
                return;
            }
            skills.add(passiveSkill);
        }
        recalculateAllCachedBonuses();
        if (!tag.hasUUID("TreeVersion")) {
            tag.putUUID("TreeVersion", TREE_VERSION);
        }
    }

    private void recalculateAllCachedBonuses() {
        cachedBonuses.clear();
        getPlayerSkills().forEach(skill ->
                skill.getBonuses().forEach(bonus -> {
                    double value = getBonusValue(bonus);
                    cachedBonuses.merge(bonus.getClass(), value, Double::sum);
                })
        );
    }

    private double getBonusValue(SkillBonus<?> bonus) {
        if (bonus instanceof MovementSpeedBonus msb) return msb.getSpeedBonus(null);
        if (bonus instanceof JumpHeightBonus jhb) return jhb.getJumpHeightMultiplier(null);
        if (bonus instanceof AttackSpeedBonus asb) return asb.getAttackSpeedBonus(null);
        if (bonus instanceof AttackReachBonus arb) return arb.getReachBonus(null);
        if (bonus instanceof AirborneDamageBonus adb) return adb.getDamageBonus(null);
        if (bonus instanceof LightLoadMovementBonus llmb) return llmb.getSpeedBonus(null);
        if (bonus instanceof ProjectileResistanceBonus prb) return prb.getResistanceBonus(null);
        if (bonus instanceof ProjectileVelocityBonus pvb) return pvb.getVelocityBonus(null);
        if (bonus instanceof SprintDamageBonus sdb) return sdb.getDamageBonus(null);
        if (bonus instanceof SwimSpeedBonus ssb) return ssb.getSpeedBonus(null);
        if (bonus instanceof FallDamageResistanceBonus fdrb) return fdrb.getResistanceBonus(null);
        if (bonus instanceof FullArmorSetBonus fasb) return fasb.getBonusMultiplier();
        if (bonus instanceof RegenerationBonus rb) return rb.getBonusMultiplier();
        if (bonus instanceof DamageReflectionBonus drb) return drb.getChance();
        if (bonus instanceof ShieldRegenerationBonus srb) return srb.getRegenAmount();
        if (bonus instanceof KnockbackResistanceBonus krb) return krb.getResistanceBonus(null);
        if (bonus instanceof NegativeEffectReductionBonus nerb) return nerb.getReduction();
        if (bonus instanceof CarryCapacityBonus ccb) return ccb.getCapacityBonus(null);
        return 0.0;
    }

    @Override
    public double getCachedBonus(Class<?> bonusClass) {
        return cachedBonuses.getOrDefault(bonusClass, 0.0);
    }

    @Override
    public long getLastCraftingXPTime() {
        return lastCraftingXPTime;
    }

    @Override
    public void setLastCraftingXPTime(long time) {
        this.lastCraftingXPTime = time;
    }

    @Override
    public int getConsecutiveCraftingActions() {
        return consecutiveCraftingActions;
    }

    @Override
    public void setConsecutiveCraftingActions(int count) {
        this.consecutiveCraftingActions = count;
    }

    @Override
    public long getLastMiningXPTime() {
        return lastMiningXPTime;
    }

    @Override
    public void setLastMiningXPTime(long time) {
        this.lastMiningXPTime = time;
    }

    @Override
    public int getConsecutiveMiningActions() {
        return consecutiveMiningActions;
    }

    @Override
    public void setConsecutiveMiningActions(int count) {
        this.consecutiveMiningActions = count;
    }

    @Override
    public double getAccuracy() {
        return accuracy;
    }

    @Override
    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    @Override
    public NonNullList<PassiveSkill> getPlayerSkills() {
        return skills;
    }

    @Override
    public int getSkillPoints() {
        return skillPoints;
    }

    @Override
    public void setSkillPoints(int skillPoints) {
        this.skillPoints = Math.max(0, skillPoints);  // Cap
    }

    @Override
    public void grantSkillPoints(int skillPoints) {
        this.skillPoints = Math.max(0, this.skillPoints + skillPoints);  // Cap
    }

    @Override
    public int getAgility() {
        return agility;
    }

    @Override
    public int getConstitution() {
        return constitution;
    }
}