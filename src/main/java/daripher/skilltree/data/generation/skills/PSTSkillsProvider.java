package daripher.skilltree.data.generation.skills;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.player.agility.*;

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
        addSkill("agility_starting", "starting_1", 24); // Starting skill
        addSkillBranch("agility_movement_speed", "agility_1", 16, 1, 5); // Movement speed line
        addSkillBranch("agility_attack_speed", "agility_1", 16, 1, 5); // Attack speed line
        addSkillBranch("agility_jump_height", "agility_1", 16, 1, 5); // Новая линия: 5 нод для jump height
        addSkillBranch("agility_projectile_velocity", "agility_1", 16, 1, 5); // Новая линия: 5 нод для projectile velocity
        addSkillBranch("agility_attack_reach", "agility_1", 16, 1, 5); // 5 нод, как другие
        addSkillBranch("agility_swim_speed", "agility_1", 16, 1, 5);
        addSkillBranch("agility_projectile_resistance", "agility_1", 16, 1, 5);
        addSkillBranch("agility_sprint_damage", "agility_1", 16, 1, 5);
        addSkillBranch("agility_airborne_damage", "agility_1", 16, 1, 5);
    }

    private void shapeSkillTree() {
        setSkillPosition(null, 0, 0, "agility_starting"); // Center, no previous

        // Movement speed branch: downward (rotation starting at 90 degrees)
        setSkillBranchPosition("agility_starting", 10, "agility_movement_speed", 90, 30, 1, 5);

        // Attack speed branch: upward (rotation starting at -90 degrees)
        setSkillBranchPosition("agility_starting", 10, "agility_attack_speed", -90, 30, 1, 5);

        // Jump height branch: leftward (rotation starting at 180 degrees)
        setSkillBranchPosition("agility_starting", 10, "agility_jump_height", 180, 30, 1, 5);

        // Projectile velocity branch: rightward (rotation starting at 0 degrees)
        setSkillBranchPosition("agility_starting", 10, "agility_projectile_velocity", 0, 30, 1, 5);

        // Attack reach branch: northeast (45 degrees)
        setSkillBranchPosition("agility_starting", 10, "agility_attack_reach", 45, 30, 1, 5);

        // Swim speed branch: southeast (135 degrees)
        setSkillBranchPosition("agility_starting", 10, "agility_swim_speed", 135, 30, 1, 5);

        // Projectile resistance branch: southwest (-135 degrees)
        setSkillBranchPosition("agility_starting", 10, "agility_projectile_resistance", -135, 30, 1, 5);

        // Sprint damage branch: westward (270 degrees)
        setSkillBranchPosition("agility_starting", 10, "agility_sprint_damage", 15, 30, 1, 5);
        setSkillBranchPosition("agility_starting", 10, "agility_airborne_damage", 120, 30, 1, 5);
    }

    private void setSkillsAttributeModifiers() {
        // Optional small bonuses on starting skill for testing
        addSkillBonus("agility_starting", new MovementSpeedBonus(0.05f, Operation.MULTIPLY_BASE));
        addSkillBonus("agility_starting", new AttackSpeedBonus(0.05f, Operation.MULTIPLY_BASE));

        // Bonuses for movement speed branch
        addSkillBranchBonuses("agility_movement_speed", new MovementSpeedBonus(0.1f, Operation.MULTIPLY_BASE), 1, 5);

        // Bonuses for attack speed branch
        addSkillBranchBonuses("agility_attack_speed", new AttackSpeedBonus(0.1f, Operation.MULTIPLY_BASE), 1, 5);

        addSkillBranchBonuses("agility_jump_height", new JumpHeightBonus(0.08f, Operation.ADDITION), 1, 5);
        // Bonuses for projectile velocity branch
        addSkillBranchBonuses("agility_projectile_velocity", new ProjectileVelocityBonus(1.0f, Operation.MULTIPLY_BASE), 1, 5); // +10% velocity per node
        addSkillBranchBonuses("agility_attack_reach", new AttackReachBonus(0.5f, Operation.ADDITION), 1, 5);
        addSkillBonus("agility_starting", new SwimSpeedBonus(0.1f, AttributeModifier.Operation.MULTIPLY_BASE)); // +10%
        addSkillBranchBonuses("agility_swim_speed", new SwimSpeedBonus(0.2f, AttributeModifier.Operation.MULTIPLY_BASE), 1, 5); // +20% per node
        addSkillBranchBonuses("agility_projectile_resistance", new ProjectileResistanceBonus(0.2f, AttributeModifier.Operation.MULTIPLY_BASE), 1, 5); // +10% per node
        addSkillBranchBonuses("agility_sprint_damage", new SprintDamageBonus(0.2f, AttributeModifier.Operation.MULTIPLY_BASE), 1, 5); // +10% per node
        addSkillBranchBonuses("agility_airborne_damage", new AirborneDamageBonus(0.2f, AttributeModifier.Operation.MULTIPLY_BASE), 1, 5); // +10% per node
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