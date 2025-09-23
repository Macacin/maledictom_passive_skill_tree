package daripher.skilltree.capability.skill;

import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;

@AutoRegisterCapability
public interface IPlayerSkills extends INBTSerializable<CompoundTag> {
    NonNullList<PassiveSkill> getPlayerSkills();

    int getSkillPoints();

    void setSkillPoints(int skillPoints);

    void grantSkillPoints(int skillPoints);

    boolean learnSkill(ServerPlayer player, @Nonnull PassiveSkill passiveSkill);

    boolean isTreeReset();

    void setTreeReset(boolean reset);

    void resetTree(ServerPlayer player);

    double getSkillExperience();

    void addSkillExperience(double amount);

    int getCurrentLevel();

    int getNextLevelCost();

    void setSkillExperience(double exp);

    void setCurrentLevel(int lvl);

    long getLastCraftingXPTime();

    void setLastCraftingXPTime(long time);

    int getConsecutiveCraftingActions();

    void setConsecutiveCraftingActions(int count);

    long getLastMiningXPTime();

    void setLastMiningXPTime(long time);

    int getConsecutiveMiningActions();

    void setConsecutiveMiningActions(int count);

    void setAccuracy(double accuracy);

    double getAccuracy();

    int getAgility();
}
