package daripher.skilltree.data.generation.skills;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.player.agility.MovementSpeedBonus;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import org.jetbrains.annotations.NotNull;

public class PSTSkillsProvider implements DataProvider {
    private final Map<ResourceLocation, PassiveSkill> skills = new HashMap<>();
    private final PackOutput packOutput;

    public PSTSkillsProvider(DataGenerator dataGenerator) {
        this.packOutput = dataGenerator.getPackOutput();
    }

    private void addSkills() {
        addSkill("agility_starting", "starting_1", 24);  // Starting point, class size, icon whatever
        addSkillBranch("agility", "agility_1", 16, 1, 4);
    }

    private void shapeSkillTree() {
        setSkillPosition(null, 0, 0, "agility_starting");  // Center, no previous
        setSkillBranchPosition("agility_starting", 10, "agility", 0, 90, 1, 4);  // From starting, down
    }

    private void setSkillsAttributeModifiers() {
        addSkillBonus("agility_starting", new MovementSpeedBonus(0.1f, Operation.MULTIPLY_BASE));  // +10% для теста
        addSkillBranchBonuses("agility", new MovementSpeedBonus(0.3f, Operation.MULTIPLY_BASE), 1, 4);
    }

    private void addSkillBranchBonuses(String branchName, SkillBonus<?> bonus, int from, int to) {
        for (int node = from; node <= to; node++) {
            addSkillBonus(branchName + "_" + node, bonus);
        }
    }

    private void addSkillBonus(String skillName, SkillBonus<?> bonus) {
        getSkill(skillName).addSkillBonus(bonus);
    }

    public void addSkillBranch(String branchName, String iconName, int nodeSize, int from, int to) {
        for (int node = from; node <= to; node++) {
            addSkill(branchName + "_" + node, iconName, nodeSize);
        }
    }

    private void setSkillBranchPosition(
            @Nullable String nodeName,
            int distance,
            String branchName,
            float rotation,
            float rotationPerNode,
            int from,
            int to) {
        String branchNode = nodeName;
        for (int node = from; node <= to; node++) {
            setSkillPosition(
                    branchNode,
                    distance,
                    rotation + (node - from) * rotationPerNode,
                    branchName + "_" + node);
            branchNode = branchName + "_" + node;
        }
    }

    private void setSkillPosition(
            @Nullable String previousSkillName,
            float distance,
            float angle,
            String skillName) {
        angle *= Mth.PI / 180F;
        PassiveSkill previous = previousSkillName == null ? null : getSkill(previousSkillName);
        PassiveSkill skill = getSkill(skillName);
        float centerX = 0F;
        float centerY = 0F;
        int buttonSize = skill.getSkillSize();
        distance += buttonSize / 2F;
        if (previous != null) {
            int previousButtonRadius = previous.getSkillSize() / 2;
            distance += previousButtonRadius;
            centerX = previous.getPositionX();
            centerY = previous.getPositionY();
        }
        float skillX = centerX + Mth.sin(angle) * distance;
        float skillY = centerY + Mth.cos(angle) * distance;
        skill.setPosition(skillX, skillY);
        if (previous != null) previous.connect(skill);
    }

    private PassiveSkill getSkill(String skillName) {
        return getSkills().get(getSkillId(skillName));
    }

    private void connectSkills(String skillName1, String skillName2) {
        getSkill(skillName1).connect(getSkill(skillName2));
    }

    private ResourceLocation getSkillId(String skillName) {
        return new ResourceLocation(SkillTreeMod.MOD_ID, skillName);
    }

    private void addSkill(String name, String icon, int size) {
        ResourceLocation skillId = new ResourceLocation(SkillTreeMod.MOD_ID, name);
        String background = size == 32 ? "keystone" : size == 20 ? "notable" : "lesser";
        if (name.contains("starting")) background = "class";
        ResourceLocation backgroundTexture =
                new ResourceLocation(
                        SkillTreeMod.MOD_ID, "textures/icons/background/" + background + ".png");
        ResourceLocation iconTexture =
                new ResourceLocation(SkillTreeMod.MOD_ID, "textures/icons/" + icon + ".png");
        String border = size == 32 ? "keystone" : size == 20 ? "notable" : "lesser";
        ResourceLocation borderTexture =
                new ResourceLocation(SkillTreeMod.MOD_ID, "textures/tooltip/" + border + ".png");
        boolean isStarting = name.contains("starting");
        PassiveSkill skill = new PassiveSkill(skillId, size, backgroundTexture, iconTexture, borderTexture, isStarting);
        if (name.startsWith("agility_")) {
            skill.getTags().add("Agility");
        }
        skills.put(skillId, skill);
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput output) {
        ImmutableList.Builder<CompletableFuture<?>> futuresBuilder = new ImmutableList.Builder<>();
        addSkills();
        shapeSkillTree();
        setSkillsAttributeModifiers();
        skills.values().forEach(skill -> futuresBuilder.add(save(output, skill)));
        return CompletableFuture.allOf(futuresBuilder.build().toArray(CompletableFuture[]::new));
    }

    private CompletableFuture<?> save(CachedOutput output, PassiveSkill skill) {
        Path path = packOutput.getOutputFolder().resolve(getPath(skill));
        JsonElement json = SkillsReloader.GSON.toJsonTree(skill);
        return DataProvider.saveStable(output, json, path);
    }

    public String getPath(PassiveSkill skill) {
        ResourceLocation id = skill.getId();
        return "data/%s/skills/%s.json".formatted(id.getNamespace(), id.getPath());
    }

    public Map<ResourceLocation, PassiveSkill> getSkills() {
        return skills;
    }

    @Override
    public @NotNull String getName() {
        return "Skills Provider";
    }
}