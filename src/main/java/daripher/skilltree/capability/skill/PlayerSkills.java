package daripher.skilltree.capability.skill;

import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.skill.PassiveSkill;

import java.util.UUID;
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
  private static final int MAX_LEVEL = 10000;

  private final NonNullList<PassiveSkill> skills = NonNullList.create();
  private int skillPoints;
  private int skillExperience = 0;
  private int currentLevel = 0;
  private boolean treeReset;

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
    this.skillPoints = Math.max(0, Math.min(MAX_LEVEL, skillPoints));  // Cap
  }

  @Override
  public void grantSkillPoints(int skillPoints) {
    this.skillPoints = Math.max(0, Math.min(MAX_LEVEL, this.skillPoints + skillPoints));  // Cap
  }

  public void addSkillExperience(int amount) {
    if (amount <= 0) return;
    this.skillExperience += amount;
    checkForLevelUp();
  }

  private void checkForLevelUp() {
    while (currentLevel < MAX_LEVEL) {
      int nextCost = getNextLevelCost();
      if (skillExperience < nextCost) break;
      skillExperience -= nextCost;
      currentLevel++;
      skillPoints++;
    }
  }

  public void setSkillExperience(int exp) {
    this.skillExperience = Math.max(0, exp);
  }
  public void setCurrentLevel(int lvl) {
    this.currentLevel = Math.max(0, Math.min(MAX_LEVEL, lvl));
  }

  public int getCurrentLevel() {
    return currentLevel;
  }

  public int getNextLevelCost() {
    if (currentLevel >= MAX_LEVEL - 1) return Integer.MAX_VALUE;
    int L = currentLevel + 1;
    double x;
    if (L >= 1 && L <= 30) {
      return (int) (15 * Math.pow(L, 1.05));  // Блок 1: 15 L^{1.05}
    } else if (L >= 31 && L <= 60) {
      x = 50 + (1.0 / 3) * (L - 30);  // x = 50 + 1/3 (L - 30)
      return (int) (8.78 * Math.pow(x, 1.20) * Math.exp(0.02 * (x - 50)));  // Блок 2
    } else if (L >= 61 && L <= 90) {
      x = 100 + (1.0 / 3) * (L - 60);  // x = 100 + 1/3 (L - 60)
      return (int) (3.786 * Math.pow(x, 1.60) * Math.exp(0.005 * (x - 100)));  // Блок 3
    } else {  // L >= 91
      x = 150 + (1.0 / 3) * (L - 90);  // x = 150 + 1/3 (L - 90)
      return (int) (0.2404 * Math.pow(x, 2.20) * Math.exp(0.0185 * (x - 150)));  // Блок 4
    }
  }

  // Новый: Текущий модовый XP (для progress bar в GUI)
  public int getSkillExperience() {
    return skillExperience;
  }

  @Override
  public boolean learnSkill(@Nonnull PassiveSkill passiveSkill) {
    if (skillPoints <= 0 || skills.contains(passiveSkill)) return false;
    skillPoints--;  // Трата point (1:1)
    return skills.add(passiveSkill);
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
    skillPoints += refunded;  // Refund points
    skillPoints = Math.min(MAX_LEVEL, skillPoints);  // Cap
    // Опционально: Refund XP? Пока нет, но если нужно — добавь skillExperience += totalSpentXP;
    // Sync: Если на сервере, отправь network update
  }

  @Override
  public CompoundTag serializeNBT() {
    CompoundTag tag = new CompoundTag();
    tag.putUUID("TreeVersion", TREE_VERSION);
    tag.putInt("Points", skillPoints);
    tag.putInt("Experience", skillExperience);  // Новое: Сохраняем XP
    tag.putInt("Level", currentLevel);  // Новое: Сохраняем уровень
    tag.putBoolean("TreeReset", treeReset);
    ListTag skillsTag = new ListTag();
    skills.forEach(skill -> skillsTag.add(StringTag.valueOf(skill.getId().toString())));
    tag.put("Skills", skillsTag);
    return tag;
  }

  @Override
  public void deserializeNBT(CompoundTag tag) {
    skills.clear();
    UUID treeVersion = tag.hasUUID("TreeVersion") ? tag.getUUID("TreeVersion") : null;
    skillPoints = tag.getInt("Points");
    skillExperience = tag.contains("Experience") ? tag.getInt("Experience") : 0;  // NEW: Fallback 0 if absent
    currentLevel = tag.contains("Level") ? tag.getInt("Level") : 0;
    ListTag skillsTag = tag.getList("Skills", Tag.TAG_STRING);
    if (!TREE_VERSION.equals(treeVersion)) {
      skillPoints += skillsTag.size();
      treeReset = true;
      skillPoints = Math.min(MAX_LEVEL, skillPoints);  // Cap
      return;
    }
    for (Tag skillTag : skillsTag) {
      ResourceLocation skillId = new ResourceLocation(skillTag.getAsString());
      PassiveSkill passiveSkill = SkillsReloader.getSkillById(skillId);
      if (passiveSkill == null || passiveSkill.isInvalid()) {
        skills.clear();
        treeReset = true;
        skillPoints += skillsTag.size();
        skillPoints = Math.min(MAX_LEVEL, skillPoints);  // Cap
        return;
      }
      skills.add(passiveSkill);
    }
    skillPoints = Math.min(MAX_LEVEL, skillPoints);  // Финальный cap
  }
}