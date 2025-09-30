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

import daripher.skilltree.skill.bonus.player.constitution.*;
import daripher.skilltree.skill.bonus.player.endurance.*;
import daripher.skilltree.skill.bonus.player.strength.*;
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
        addSkill("agility_starting", "icon_starting_agility", 24); // Starting skill
        addSkillBranch("agility_movement_speed", "icon_movement_speed", 16, 1, 5); // Movement speed line
        addSkillBranch("agility_attack_speed", "icon_attack_speed", 16, 1, 5); // Attack speed line
        addSkillBranch("agility_jump_height", "icon_jump_height", 16, 1, 5); // Новая линия: 5 нод для jump height
        addSkillBranch("agility_projectile_velocity", "icon_projectile_velocilty", 16, 1, 5); // Новая линия: 5 нод для projectile velocity
        addSkillBranch("agility_attack_reach", "icon_attack_reach", 16, 1, 5); // 5 нод, как другие
        addSkillBranch("agility_swim_speed", "icon_swim_speed", 16, 1, 5);
        addSkillBranch("agility_projectile_resistance", "icon_projectile_resistance", 16, 1, 5);
        addSkillBranch("agility_sprint_damage", "icon_sprint_damage", 16, 1, 5);
        addSkillBranch("agility_airborne_damage", "icon_airborne_damage", 16, 1, 5);
        addSkillBranch("agility_light_load_movement", "icon_light_load_movement", 16, 1, 5);
        addSkill("constitution_starting", "icon_starting_constitution", 24); // Starting skill for Constitution
        addSkillBranch("constitution_fall_damage_resistance", "icon_fall_damage_resistance", 16, 1, 5); // Fall damage resistance line
        addSkillBranch("constitution_full_armor_set", "icon_full_armor_set", 16, 1, 5); // Full armor set line
        addSkillBranch("constitution_regeneration", "icon_regeneration", 16, 1, 5); // Regeneration bonus line
        addSkillBranch("constitution_damage_reflection", "icon_reflection", 16, 1, 5); // Damage reflection line
        addSkillBranch("constitution_shield_regeneration", "icon_shield_regeneration", 16, 1, 5); // Shield regeneration line
        addSkillBranch("constitution_knockback_resistance", "icon_knockback_resistance", 16, 1, 5); // Knockback resistance line
        addSkillBranch("constitution_negative_effect_reduction", "icon_negative_effect_reduction", 16, 1, 5); // Negative effect reduction line
        addSkillBranch("constitution_carry_capacity", "icon_carry_capacity", 16, 1, 5); // Carry capacity line
        addSkillBranch("constitution_heavy_load_speed", "icon_heavy_load_speed", 16, 1, 5); // Heavy load speed line
        addSkillBranch("constitution_shield_block", "icon_shield_block", 16, 1, 5); // Shield block line
        addSkill("endurance_starting", "icon_starting_endurance", 24); // Starting skill for Endurance
        addSkillBranch("endurance_max_health", "icon_max_health", 16, 1, 5); // Max health line (reuse icon for now)
        addSkillBranch("endurance_evasion_physical", "icon_evasion_physical", 16, 1, 5); // Reuse icon for now
        addSkillBranch("endurance_evasion_magic", "icon_evasion_magic", 16, 1, 5);
        addSkillBranch("endurance_evasion_projectile", "icon_evasion_projectile", 16, 1, 5);
        addSkillBranch("endurance_roll_recharge", "icon_roll_recharge", 16, 1, 5); // Reuse icon
        addSkillBranch("endurance_hunger_reduction", "icon_hunger_reduction", 16, 1, 5); // Reuse icon
        addSkillBranch("endurance_mining_speed", "icon_mining_speed", 16, 1, 5); // Reuse icon
        addSkillBranch("endurance_full_hunger_damage", "icon_full_hunger_damage", 16, 1, 5);
        addSkillBranch("endurance_physical_resistance", "icon_physical_resistance", 16, 1, 5);
        addSkillBranch("endurance_medium_armor_movement", "icon_medium_armor_movement", 16, 1, 5);

        addSkill("strength_starting", "icon_starting_strength", 24); // Starting skill for Strength
        addSkillBranch("strength_crit_chance", "icon_crit_chance", 16, 1, 5); // Crit chance line
        addSkillBranch("strength_crit_damage", "icon_crit_damage", 16, 1, 5); // Crit damage line
        addSkillBranch("strength_projectile_damage", "icon_projectile_damage", 16, 1, 5); // Projectile damage line
        addSkillBranch("strength_projectile_crit_chance", "icon_projectile_crit_chance", 16, 1, 5); // Projectile crit chance line
        addSkillBranch("strength_projectile_crit_damage", "icon_projectile_crit_damage", 16, 1, 5); // Projectile crit damage line
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
        setSkillBranchPosition("agility_starting", 10, "agility_light_load_movement", 160, 30, 1, 5);

        setSkillPosition(null, 200, 0, "constitution_starting");

        setSkillBranchPosition("constitution_starting", 10, "constitution_fall_damage_resistance", 90, 30, 1, 5);

        // Full armor set branch: rightward (rotation starting at 0 degrees)
        setSkillBranchPosition("constitution_starting", 10, "constitution_full_armor_set", 0, 30, 1, 5);
        setSkillBranchPosition("constitution_starting", 10, "constitution_regeneration", 180, 30, 1, 5);
        // Damage reflection branch: upward (rotation starting at -90 degrees)
        setSkillBranchPosition("constitution_starting", 10, "constitution_damage_reflection", -90, 30, 1, 5);

        // Shield regeneration branch: southeast (rotation starting at 45 degrees)
        setSkillBranchPosition("constitution_starting", 10, "constitution_shield_regeneration", 45, 30, 1, 5);
        // Knockback resistance branch: southwest (rotation starting at 135 degrees)
        setSkillBranchPosition("constitution_starting", 10, "constitution_knockback_resistance", 135, 30, 1, 5);
        // Negative effect reduction branch: northeast (rotation starting at -45 degrees)
        setSkillBranchPosition("constitution_starting", 10, "constitution_negative_effect_reduction", -45, 30, 1, 5);
        // Carry capacity branch: southwest (rotation starting at -135 degrees)
        setSkillBranchPosition("constitution_starting", 10, "constitution_carry_capacity", -135, 30, 1, 5);
        // Heavy load speed branch: westward (rotation starting at 180 degrees)
        setSkillBranchPosition("constitution_starting", 10, "constitution_heavy_load_speed", 25, 30, 1, 5);
        // Shield block branch: eastward (rotation starting at 0 degrees)
        setSkillBranchPosition("constitution_starting", 10, "constitution_shield_block", 115, 30, 1, 5);
        // Endurance: Starting far right for horizontal expansion
        setSkillPosition(null, 200, 90, "endurance_starting"); // Справа от Constitution (200,0), уменьшил с 400 для плотности

        setSkillBranchPosition("endurance_starting", 10, "endurance_max_health", 0, 15, 1, 5);

        setSkillBranchPosition("endurance_starting", 10, "endurance_evasion_physical", 90, 15, 1, 5); // Slightly up-right
        setSkillBranchPosition("endurance_starting", 10, "endurance_evasion_magic", 60, 15, 1, 5); // Straight right
        setSkillBranchPosition("endurance_starting", 10, "endurance_evasion_projectile", 30, 15, 1, 5); // Slightly down-right
        setSkillBranchPosition("endurance_starting", 10, "endurance_roll_recharge", 120, 15, 1, 5); // Down-right spread
        setSkillBranchPosition("endurance_starting", 10, "endurance_hunger_reduction", 150, 15, 1, 5); // Up-right spread
        setSkillBranchPosition("endurance_starting", 10, "endurance_mining_speed", 180, 15, 1, 5); // Down spread
        setSkillBranchPosition("endurance_starting", 10, "endurance_full_hunger_damage", 210, 15, 1, 5);
        setSkillBranchPosition("endurance_starting", 10, "endurance_physical_resistance", -60, 15, 1, 5);
        setSkillBranchPosition("endurance_starting", 10, "endurance_medium_armor_movement", -90, 15, 1, 5);

        setSkillPosition(null, 200, 270, "strength_starting");
        setSkillBranchPosition("strength_starting", 10, "strength_crit_chance", 0, 30, 1, 5);
        setSkillBranchPosition("strength_starting", 10, "strength_crit_damage", 30, 30, 1, 5); // Upward, как attack_speed в agility
        setSkillBranchPosition("strength_starting", 10, "strength_projectile_damage", 60, 30, 1, 5); // Rightward
        setSkillBranchPosition("strength_starting", 10, "strength_projectile_crit_chance", 90, 30, 1, 5); // Southeast
        setSkillBranchPosition("strength_starting", 10, "strength_projectile_crit_damage", 120, 30, 1, 5); // Northeast (для симметрии)
    }

    private void setSkillsAttributeModifiers() {
        // Optional small bonuses on starting skill for testing
        addSkillBonus("agility_starting", new MovementSpeedBonus(0.05f, Operation.MULTIPLY_BASE));
        addSkillBonus("agility_starting", new AttackSpeedBonus(0.05f, Operation.MULTIPLY_BASE));
        addSkillBranchBonuses("agility_movement_speed", new MovementSpeedBonus(0.1f, Operation.MULTIPLY_BASE), 1, 5);
        addSkillBranchBonuses("agility_attack_speed", new AttackSpeedBonus(0.1f, Operation.MULTIPLY_BASE), 1, 5);
        addSkillBranchBonuses("agility_jump_height", new JumpHeightBonus(0.08f, Operation.ADDITION), 1, 5);
        addSkillBranchBonuses("agility_projectile_velocity", new ProjectileVelocityBonus(1.0f, Operation.MULTIPLY_BASE), 1, 5); // +10% velocity per node
        addSkillBranchBonuses("agility_attack_reach", new AttackReachBonus(0.5f, Operation.ADDITION), 1, 5);
        addSkillBonus("agility_starting", new SwimSpeedBonus(0.1f, AttributeModifier.Operation.MULTIPLY_BASE)); // +10%
        addSkillBranchBonuses("agility_swim_speed", new SwimSpeedBonus(0.2f, AttributeModifier.Operation.MULTIPLY_BASE), 1, 5); // +20% per node
        addSkillBranchBonuses("agility_projectile_resistance", new ProjectileResistanceBonus(0.2f, AttributeModifier.Operation.MULTIPLY_BASE), 1, 5); // +10% per node
        addSkillBranchBonuses("agility_sprint_damage", new SprintDamageBonus(0.2f, AttributeModifier.Operation.MULTIPLY_BASE), 1, 5); // +20% per node
        addSkillBranchBonuses("agility_airborne_damage", new AirborneDamageBonus(0.2f, AttributeModifier.Operation.MULTIPLY_BASE), 1, 5); // +20% per node
        addSkillBranchBonuses("agility_light_load_movement", new LightLoadMovementBonus(0.1f, Operation.MULTIPLY_BASE), 1, 5);

        addSkillBonus("constitution_starting", new FallDamageResistanceBonus(0.05f, Operation.MULTIPLY_TOTAL));
        addSkillBranchBonuses("constitution_fall_damage_resistance", new FallDamageResistanceBonus(0.1f, Operation.MULTIPLY_TOTAL), 1, 5);
        addSkillBonus("constitution_starting", new FullArmorSetBonus(0.05f, Operation.MULTIPLY_BASE));
        addSkillBranchBonuses("constitution_full_armor_set", new FullArmorSetBonus(0.1f, Operation.MULTIPLY_BASE), 1, 5);
        addSkillBranchBonuses("constitution_regeneration", new RegenerationBonus(0.5f, Operation.MULTIPLY_BASE), 1, 5);
        addSkillBranchBonuses("constitution_damage_reflection", new DamageReflectionBonus(0.5f, Operation.ADDITION), 1, 5);
        addSkillBranchBonuses("constitution_shield_regeneration", new ShieldRegenerationBonus(0.1f, Operation.MULTIPLY_BASE), 1, 5);
        addSkillBranchBonuses("constitution_knockback_resistance", new KnockbackResistanceBonus(0.2f, Operation.MULTIPLY_BASE), 1, 5);
        addSkillBranchBonuses("constitution_negative_effect_reduction", new NegativeEffectReductionBonus(0.1f, Operation.ADDITION), 1, 5);
        addSkillBranchBonuses("constitution_carry_capacity", new CarryCapacityBonus(10f, Operation.ADDITION), 1, 5);
        addSkillBranchBonuses("constitution_heavy_load_speed", new HeavyLoadSpeedBonus(0.2f, Operation.MULTIPLY_BASE), 1, 5);
        addSkillBranchBonuses("constitution_shield_block", new ShieldBlockBonus(0.2f, Operation.MULTIPLY_BASE), 1, 5);

        addSkillBonus("endurance_starting", new MaxHealthBonus(1f, Operation.ADDITION)); // +2 HP per node
        addSkillBranchBonuses("endurance_max_health", new MaxHealthBonus(2f, Operation.ADDITION), 1, 5); // +2 HP per node
        addSkillBranchBonuses("endurance_evasion_physical", new EvasionBonusPhysical(0.2f, Operation.ADDITION), 1, 5); // +20% per node
        addSkillBranchBonuses("endurance_evasion_magic", new EvasionBonusMagic(0.2f, Operation.ADDITION), 1, 5);
        addSkillBranchBonuses("endurance_evasion_projectile", new EvasionBonusProjectile(0.2f, Operation.ADDITION), 1, 5);
        addSkillBranchBonuses("endurance_roll_recharge", new RollRechargeBonus(0.3f, Operation.MULTIPLY_BASE), 1, 5); // +10% per node
        addSkillBranchBonuses("endurance_hunger_reduction", new HungerReductionBonus(0.2f, Operation.MULTIPLY_BASE), 1, 5); // -10% per node
        addSkillBranchBonuses("endurance_mining_speed", new MiningSpeedBonus(0.2f, Operation.MULTIPLY_BASE), 1, 5); // +20% per node НО ОЧЕНЬ СЛАБО РАБОТАЕТ, НАДО БУДЕТ ИЗМЕНИТЬ В ТУЛТИПЕ
        addSkillBranchBonuses("endurance_full_hunger_damage", new FullHungerDamageBonus(0.2f, Operation.MULTIPLY_BASE), 1, 5);
        addSkillBranchBonuses("endurance_physical_resistance", new PhysicalResistanceBonus(0.1f, Operation.MULTIPLY_BASE), 1, 5);
        addSkillBranchBonuses("endurance_medium_armor_movement", new MediumArmorMovementBonus(0.1f, Operation.MULTIPLY_BASE), 1, 5);

        addSkillBonus("strength_starting", new CritChanceBonus(0.05f, Operation.ADDITION)); // +5% base
        addSkillBranchBonuses("strength_crit_chance", new CritChanceBonus(0.1f, Operation.ADDITION), 1, 5);
        addSkillBonus("strength_starting", new CritDamageBonus(0.05f, Operation.ADDITION)); // +5% base
        addSkillBranchBonuses("strength_crit_damage", new CritDamageBonus(0.1f, Operation.ADDITION), 1, 5); // +10% per node
        addSkillBranchBonuses("strength_projectile_damage", new ProjectileDamageBonus(0.2f, Operation.MULTIPLY_BASE), 1, 5); // +10% per node
        addSkillBranchBonuses("strength_projectile_crit_chance", new ProjectileCritChanceBonus(0.2f, Operation.ADDITION), 1, 5); // +10% chance per node
        addSkillBranchBonuses("strength_projectile_crit_damage", new ProjectileCritDamageBonus(0.2f, Operation.ADDITION), 1, 5); // +10% damage per node
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
        if (name.startsWith("constitution_")) {
            skill.getTags().add("Constitution");
        }
        if (name.startsWith("endurance_")) {
            skill.getTags().add("Endurance");
        }
        if (name.startsWith("strength_")) {
            skill.getTags().add("Strength");
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