package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.player.*;

import java.util.List;
import java.util.Objects;

import daripher.skilltree.skill.bonus.player.agility.*;
import daripher.skilltree.skill.bonus.player.constitution.*;
import daripher.skilltree.skill.bonus.player.endurance.*;
import daripher.skilltree.skill.bonus.player.strength.*;
import daripher.skilltree.skill.bonus.player.wisdom.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.*;

public class PSTSkillBonuses {
    public static final ResourceLocation REGISTRY_ID =
            new ResourceLocation(SkillTreeMod.MOD_ID, "skill_bonuses");
    public static final DeferredRegister<SkillBonus.Serializer> REGISTRY =
            DeferredRegister.create(REGISTRY_ID, SkillTreeMod.MOD_ID);

    public static final RegistryObject<SkillBonus.Serializer> ATTRIBUTE =
            REGISTRY.register("attribute", AttributeBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> COMMAND =
            REGISTRY.register("command", CommandBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> CRAFTED_ITEM_BONUS =
            REGISTRY.register("crafted_item_bonus", CraftedItemBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> PLAYER_SOCKETS =
            REGISTRY.register("player_sockets", PlayerSocketsBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> REPAIR_EFFICIENCY =
            REGISTRY.register("repair_efficiency", RepairEfficiencyBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> BLOCK_BREAK_SPEED =
            REGISTRY.register("block_break_speed", BlockBreakSpeedBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> RECIPE_UNLOCK =
            REGISTRY.register("recipe_unlock", RecipeUnlockBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> ENCHANTMENT_AMPLIFICATION =
            REGISTRY.register("enchantment_amplification", EnchantmentAmplificationBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> ENCHANTMENT_REQUIREMENT =
            REGISTRY.register("enchantment_requirement", EnchantmentRequirementBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> FREE_ENCHANTMENT =
            REGISTRY.register("free_enchantment", FreeEnchantmentBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> JUMP_HEIGHT =
            REGISTRY.register("jump_height", JumpHeightBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> INCOMING_HEALING =
            REGISTRY.register("incoming_healing", IncomingHealingBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> LOOT_DUPLICATION =
            REGISTRY.register("loot_duplication", LootDuplicationBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> GAINED_EXPERIENCE =
            REGISTRY.register("gained_experience", GainedExperienceBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> IGNITE =
            REGISTRY.register("ignite", IgniteBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> ARROW_RETRIEVAL =
            REGISTRY.register("arrow_retrieval", ArrowRetrievalBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> HEALTH_RESERVATION =
            REGISTRY.register("health_reservation", HealthReservationBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> ALL_ATTRIBUTES =
            REGISTRY.register("all_attributes", AllAttributesBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> MOB_EFFECT =
            REGISTRY.register("mob_effect", MobEffectBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> CANT_USE_ITEM =
            REGISTRY.register("cant_use_item", CantUseItemBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> HEALING =
            REGISTRY.register("healing", HealingBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> INFLICT_DAMAGE =
            REGISTRY.register("inflict_damage", InflictDamageBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> MOVEMENT_SPEED =
            REGISTRY.register("movement_speed", MovementSpeedBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> ATTACK_SPEED =
            REGISTRY.register("attack_speed", AttackSpeedBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> PROJECTILE_VELOCITY =
            REGISTRY.register("projectile_velocity", ProjectileVelocityBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> ATTACK_REACH =
            REGISTRY.register("attack_reach", AttackReachBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> SWIM_SPEED =
            REGISTRY.register("swim_speed", SwimSpeedBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> PROJECTILE_RESISTANCE =
            REGISTRY.register("projectile_resistance", ProjectileResistanceBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> SPRINT_DAMAGE =
            REGISTRY.register("sprint_damage", SprintDamageBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> AIRBORNE_DAMAGE =
            REGISTRY.register("airborne_damage", AirborneDamageBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> LIGHT_LOAD_MOVEMENT =
            REGISTRY.register("light_load_movement", LightLoadMovementBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> FALL_DAMAGE_RESISTANCE =
            REGISTRY.register("fall_damage_resistance", FallDamageResistanceBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> FULL_ARMOR_SET =
            REGISTRY.register("full_armor_set", FullArmorSetBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> REGENERATION_BONUS =
            REGISTRY.register("regeneration_bonus", RegenerationBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> DAMAGE_REFLECTION =
            REGISTRY.register("damage_reflection", DamageReflectionBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> SHIELD_REGENERATION =
            REGISTRY.register("shield_regeneration", ShieldRegenerationBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> KNOCKBACK_RESISTANCE =
            REGISTRY.register("knockback_resistance", KnockbackResistanceBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> NEGATIVE_EFFECT_REDUCTION =
            REGISTRY.register("negative_effect_reduction", NegativeEffectReductionBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> CARRY_CAPACITY =
            REGISTRY.register("carry_capacity", CarryCapacityBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> HEAVY_LOAD_SPEED =
            REGISTRY.register("heavy_load_speed", HeavyLoadSpeedBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> SHIELD_BLOCK =
            REGISTRY.register("shield_block", ShieldBlockBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> MAX_HEALTH =
            REGISTRY.register("max_health", MaxHealthBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> EVASION_PHYSICAL =
            REGISTRY.register("evasion_physical", EvasionBonusPhysical.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> EVASION_MAGIC =
            REGISTRY.register("evasion_magic", EvasionBonusMagic.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> EVASION_PROJECTILE =
            REGISTRY.register("evasion_projectile", EvasionBonusProjectile.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> ROLL_RECHARGE =
            REGISTRY.register("roll_recharge", RollRechargeBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> HUNGER_REDUCTION =
            REGISTRY.register("hunger_reduction", HungerReductionBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> MINING_SPEED =
            REGISTRY.register("mining_speed", MiningSpeedBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> FULL_HUNGER_DAMAGE =
            REGISTRY.register("full_hunger_damage", FullHungerDamageBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> PHYSICAL_RESISTANCE =
            REGISTRY.register("physical_resistance", PhysicalResistanceBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> MEDIUM_ARMOR_MOVEMENT =
            REGISTRY.register("medium_armor_movement", MediumArmorMovementBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> CRIT_CHANCE_STRENGTH =
            REGISTRY.register("crit_chance_strength", CritChanceBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> CRIT_DAMAGE_STRENGTH =
            REGISTRY.register("crit_damage_strength", CritDamageBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> PROJECTILE_DAMAGE =
            REGISTRY.register("projectile_damage", ProjectileDamageBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> PROJECTILE_CRIT_CHANCE =
            REGISTRY.register("projectile_crit_chance", ProjectileCritChanceBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> PROJECTILE_CRIT_DAMAGE =
            REGISTRY.register("projectile_crit_damage", ProjectileCritDamageBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> ARMOR_IGNORE =
            REGISTRY.register("armor_ignore", ArmorIgnoreBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> EXPLOSION_RESISTANCE =
            REGISTRY.register("explosion_resistance", ExplosionResistanceBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> NO_ARMOR_DAMAGE =
            REGISTRY.register("no_armor_damage", NoArmorDamageBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> SWORD_DAMAGE =
            REGISTRY.register("sword_damage", SwordDamageBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> AXE_DAMAGE =
            REGISTRY.register("axe_damage", AxeDamageBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> HAMMER_DAMAGE =
            REGISTRY.register("hammer_damage", HammerDamageBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> TRIDENT_DAMAGE =
            REGISTRY.register("trident_damage", TridentDamageBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> DAGGER_DAMAGE =
            REGISTRY.register("dagger_damage", DaggerDamageBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> SCYTHE_DAMAGE =
            REGISTRY.register("scythe_damage", ScytheDamageBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> CHAKRAM_DAMAGE =
            REGISTRY.register("chakram_damage", ChakramDamageBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> MAGIC_WEAPON_DAMAGE =
            REGISTRY.register("magic_weapon_damage", MagicWeaponDamageBonus.Serializer::new);
    public static final RegistryObject<SkillBonus.Serializer> SPELL_DAMAGE =
            REGISTRY.register("spell_damage", SpellDamageBonus.Serializer::new);

    @SuppressWarnings("rawtypes")
    public static List<SkillBonus> bonusList() {
        return PSTRegistries.SKILL_BONUSES.get().getValues().stream()
                .map(SkillBonus.Serializer::createDefaultInstance)
                .map(SkillBonus.class::cast)
                .toList();
    }

    public static String getName(SkillBonus<?> bonus) {
        ResourceLocation id = PSTRegistries.SKILL_BONUSES.get().getKey(bonus.getSerializer());
        return TooltipHelper.idToName(Objects.requireNonNull(id).getPath());
    }
}
