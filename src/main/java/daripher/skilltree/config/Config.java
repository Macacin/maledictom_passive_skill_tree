package daripher.skilltree.config;

import daripher.skilltree.SkillTreeMod;

import java.util.*;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = Bus.MOD)
public class Config {
    public static final ForgeConfigSpec SPEC;
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    //  private static final ConfigValue<Double> AMNESIA_SCROLL_PENALTY;
    private static final ConfigValue<Double> GRINDSTONE_EXP_MULTIPLIER;
    private static final ConfigValue<Double> MIXTURE_EFFECTS_DURATION;
    private static final ConfigValue<Double> MIXTURE_EFFECTS_STRENGTH;
    private static final ConfigValue<Boolean> SHOW_CHAT_MESSAGES;
    private static final ConfigValue<Boolean> ENABLE_EXP_EXCHANGE;

    public static final ForgeConfigSpec.DoubleValue BASE_ACCURACY;
    public static final ForgeConfigSpec.DoubleValue RANGED_DAMAGE_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue RANGED_VELOCITY_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue CURSE_CHANCE;
    public static final ForgeConfigSpec.DoubleValue LEVEL_SCALE_FACTOR;
    public static final Set<Enchantment> FORBIDDEN_ENCHANTMENTS = new HashSet<>();
    public static final Map<Enchantment, Integer> FORBIDDEN_LEVELS = new HashMap<>();
    public static final ForgeConfigSpec.DoubleValue SHIELD_DAMAGE_REDUCTION;
    public static final ForgeConfigSpec.DoubleValue SHIELD_SLOWDOWN;

    public static final ForgeConfigSpec.DoubleValue GRIND_MIN_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue ENCHANTMENT_COEFFICIENT;
    public static final ForgeConfigSpec.IntValue GRIND_STREAK_THRESHOLD;
    public static final ForgeConfigSpec.IntValue GRIND_MAX_STREAK_LENGTH;
    public static final ForgeConfigSpec.IntValue GRIND_TIME_WINDOW;
    public static final ForgeConfigSpec.IntValue GRIND_INITIAL_PENALTY_PERCENT;
    public static final ForgeConfigSpec.IntValue GRIND_MAX_PENALTY_PERCENT;
    public static final ForgeConfigSpec.IntValue CRAFTING_GRIND_TIME_WINDOW;
    public static final ForgeConfigSpec.IntValue CRAFTING_GRIND_PENALTY_STEP_PERCENT;
    public static final ForgeConfigSpec.DoubleValue CRAFTING_GRIND_MIN_MULTIPLIER;

    public static final ForgeConfigSpec.DoubleValue TIER1_MOB_B;
    public static final ForgeConfigSpec.DoubleValue TIER2_MOB_B;
    public static final ForgeConfigSpec.DoubleValue TIER3_MOB_B;
    public static final ForgeConfigSpec.DoubleValue TIER0_MOB_HP;
    public static final ForgeConfigSpec.DoubleValue TIER1_MOB_HP;
    public static final ForgeConfigSpec.DoubleValue TIER2_MOB_HP;

    public static final ForgeConfigSpec.DoubleValue TIER1_ORE_B;
    public static final ForgeConfigSpec.DoubleValue TIER2_ORE_B;
    public static final ForgeConfigSpec.DoubleValue TIER3_ORE_B;
    public static final ForgeConfigSpec.DoubleValue TIER4_ORE_B;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> TIER1_ORES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> TIER2_ORES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> TIER3_ORES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> TIER4_ORES;
    public static final ForgeConfigSpec.IntValue MINING_GRIND_TIME_WINDOW;
    public static final ForgeConfigSpec.IntValue MINING_GRIND_PENALTY_STEP_PERCENT;
    public static final ForgeConfigSpec.DoubleValue MINING_GRIND_MIN_MULTIPLIER;

    public static final ForgeConfigSpec.DoubleValue TIER1_STRUCTURE_B;
    public static final ForgeConfigSpec.DoubleValue TIER2_STRUCTURE_B;
    public static final ForgeConfigSpec.DoubleValue TIER3_STRUCTURE_B;
    public static final ForgeConfigSpec.DoubleValue TIER4_STRUCTURE_B;
    public static final ForgeConfigSpec.DoubleValue TIER5_STRUCTURE_B;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> COMMON_STRUCTURES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> UNCOMMON_STRUCTURES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> RARE_STRUCTURES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> EPIC_STRUCTURES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> LEGENDARY_STRUCTURES;
    //  private static final ConfigValue<Boolean> DRAGON_DROPS_AMNESIA_SCROLL;
    public static final int DEFAULT_MAX_SKILLS = 85;
    public static int max_skill_points;
    public static int first_skill_cost;
    public static int last_skill_cost;
    public static int default_helmet_sockets;
    public static int default_chestplate_sockets;
    public static int default_leggings_sockets;
    public static int default_boots_sockets;
    public static int default_weapon_sockets;
    public static int default_shield_sockets;
    public static int default_necklace_sockets;
    public static int default_ring_sockets;
    public static double amnesia_scroll_penalty;
    public static double grindstone_exp_multiplier;
    public static double mixture_effects_duration;
    public static double mixture_effects_strength;
    public static boolean show_chat_messages;
    public static boolean use_skill_points_array;
    public static boolean enable_exp_exchange;
    public static boolean dragon_drops_amnesia_scroll;
    public static List<? extends Integer> skill_points_costs;
    public static List<? extends String> socket_blacklist;


    static {
        BUILDER.push("Skill Points");
        BUILDER.comment("Disabling this will remove chat messages when you gain a skill point.");
        SHOW_CHAT_MESSAGES = BUILDER.define("Show chat messages", true);
        BUILDER.comment(
                "Warning: If you disable this make sure you make alternative way of getting skill points.");
        ENABLE_EXP_EXCHANGE = BUILDER.define("Enable exprerience exchange for skill points", false);
        BUILDER.pop();

//    BUILDER.push("Amnesia Scroll");
//    BUILDER.comment("How much levels (percentage) player lose using amnesia scroll");
//    AMNESIA_SCROLL_PENALTY = BUILDER.defineInRange("Amnesia scroll penalty", 0.2D, 0D, 1D);
//    DRAGON_DROPS_AMNESIA_SCROLL =
//        BUILDER.define("Drop amnesia scrolls from the Ender Dragon", true);
//    BUILDER.pop();

        BUILDER.push("Experience");
        GRINDSTONE_EXP_MULTIPLIER =
                BUILDER.defineInRange("Grindstone experience multiplier", 0.1D, 0D, 1D);
        BUILDER.pop();

        BUILDER.push("Mixtures");
        MIXTURE_EFFECTS_DURATION = BUILDER.defineInRange("Effects duration multiplier", 1D, 0D, 2D);
        MIXTURE_EFFECTS_STRENGTH = BUILDER.defineInRange("Effects strength multiplier", 1D, 0D, 2D);
        BUILDER.pop();

        BUILDER.push("Base mechanics");
        BASE_ACCURACY = BUILDER.defineInRange("base_accuracy", 70.0, 0.0, 100.0);
        RANGED_DAMAGE_MULTIPLIER = BUILDER.defineInRange("rangedDamageMultiplier", 0.6, 0.0, 1.0);
        RANGED_VELOCITY_MULTIPLIER = BUILDER.defineInRange("rangedVelocityMultiplier", 0.5, 0.0, 1.0);
        CURSE_CHANCE = BUILDER.defineInRange("curseChance", 0.7, 0.0, 1.0);
        LEVEL_SCALE_FACTOR = BUILDER.defineInRange("levelScaleFactor", 2.0 / 3.0, 0.0, 1.0);
        SHIELD_DAMAGE_REDUCTION = BUILDER.defineInRange("shieldDamageReduction", 0.4, 0.0, 1.0);
        SHIELD_SLOWDOWN = BUILDER.defineInRange("shieldSlowdown", 0.3, 0.0, 1.0);
        BUILDER.pop();

        BUILDER.push("Penalties for grind for killing & assisting mobs");
        GRIND_TIME_WINDOW = BUILDER.defineInRange("grind_time_window", 5, 1, 60);
        GRIND_STREAK_THRESHOLD = BUILDER.defineInRange("grind_streak_threshold", 3, 1, 10);
        GRIND_INITIAL_PENALTY_PERCENT = BUILDER.defineInRange("grind_initial_penalty_percent", 20, 0, 50);
        GRIND_MAX_PENALTY_PERCENT = BUILDER.defineInRange("grind_max_penalty_percent", 60, 20, 100);
        GRIND_MAX_STREAK_LENGTH = BUILDER.defineInRange("grind_max_streak_length", 20, 5, 50);
        GRIND_MIN_MULTIPLIER = BUILDER.defineInRange("grind_min_multiplier", 0.4, 0.1, 1.0);
        BUILDER.pop();

        BUILDER.push("Killing mobs tweaks");
        TIER1_MOB_B = BUILDER.defineInRange("tier_1_mob_b", 1.2, 0.0, 100.0);
        TIER2_MOB_B = BUILDER.defineInRange("tier_2_mob_b", 1.8, 0.0, 100.0);
        TIER3_MOB_B = BUILDER.defineInRange("tier_3_mob_b", 3.0, 0.0, 100.0);
        TIER0_MOB_HP = BUILDER.defineInRange("tier_1_mob_hp", 12.0, 0.0, 100000.0);
        TIER1_MOB_HP = BUILDER.defineInRange("tier_2_mob_hp", 300.0, 0.0, 100000.0);
        TIER2_MOB_HP = BUILDER.defineInRange("tier_3_mob_hp", 1500.0, 0.0, 100000.0);
        BUILDER.pop();

        BUILDER.push("Structure discovery tiers");
        COMMON_STRUCTURES = BUILDER.defineList("common_structures", List.of(), o -> o instanceof String);
        UNCOMMON_STRUCTURES = BUILDER.defineList("uncommon_structures", List.of(), o -> o instanceof String);
        RARE_STRUCTURES = BUILDER.defineList("rare_structures", List.of(), o -> o instanceof String);
        EPIC_STRUCTURES = BUILDER.defineList("epic_structures", List.of(), o -> o instanceof String);
        LEGENDARY_STRUCTURES = BUILDER.defineList("legendary_structures", List.of(), o -> o instanceof String);
        TIER1_STRUCTURE_B = BUILDER.defineInRange("tier1_structure_b", 15.0, 0.1, 1000.0);
        TIER2_STRUCTURE_B = BUILDER.defineInRange("tier2_structure_b", 30.0, 0.1, 1000.0);
        TIER3_STRUCTURE_B = BUILDER.defineInRange("tier3_structure_b", 80.0, 0.1, 1000.0);
        TIER4_STRUCTURE_B = BUILDER.defineInRange("tier4_structure_b", 130.0, 0.1, 1000.0);
        TIER5_STRUCTURE_B = BUILDER.defineInRange("tier5_structure_b", 200.0, 0.1, 1000.0);
        BUILDER.pop();

        BUILDER.push("Enchantment, smithing, anvil exp tweaks");
        ENCHANTMENT_COEFFICIENT = BUILDER.defineInRange("formula_coef", 3.0, 1.0, 100.0);
        CRAFTING_GRIND_TIME_WINDOW = BUILDER.defineInRange("crafting_grind_time_window", 60, 1, 300);
        CRAFTING_GRIND_PENALTY_STEP_PERCENT = BUILDER.defineInRange("crafting_grind_penalty_step_percent", 20, 0, 50);
        CRAFTING_GRIND_MIN_MULTIPLIER = BUILDER.defineInRange("crafting_grind_min_multiplier", 0.2, 0.1, 1.0);
        BUILDER.pop();

        BUILDER.push("Mining ores exp tweaks");
        TIER1_ORE_B = BUILDER.defineInRange("tier1_ore_b", 0.25, 0.1, 100.0);
        TIER2_ORE_B = BUILDER.defineInRange("tier2_ore_b", 2.0, 0.1, 100.0);
        TIER3_ORE_B = BUILDER.defineInRange("tier3_ore_b", 8.0, 0.1, 100.0);
        TIER4_ORE_B = BUILDER.defineInRange("tier4_ore_b", 25.0, 0.1, 100.0);
        TIER1_ORES = BUILDER.defineList("tier1_ores", List.of("minecraft:iron_ore"), o -> o instanceof String);
        TIER2_ORES = BUILDER.defineList("tier2_ores", List.of("minecraft:diamond_ore"), o -> o instanceof String);
        TIER3_ORES = BUILDER.defineList("tier3_ores", List.of("example_mod:ruby_ore", "example_mod:nephrite_ore"), o -> o instanceof String);
        TIER4_ORES = BUILDER.defineList("tier4_ores", List.of("minecraft:ancient_debris"), o -> o instanceof String);
        MINING_GRIND_TIME_WINDOW = BUILDER.defineInRange("mining_grind_time_window", 15, 1, 300);
        MINING_GRIND_PENALTY_STEP_PERCENT = BUILDER.defineInRange("mining_grind_penalty_step_percent", 4, 0, 50);
        MINING_GRIND_MIN_MULTIPLIER = BUILDER.defineInRange("mining_grind_min_multiplier", 0.6, 0.1, 1.0);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    static {
        FORBIDDEN_ENCHANTMENTS.add(Enchantments.MENDING);
        FORBIDDEN_ENCHANTMENTS.add(Enchantments.FLAMING_ARROWS);
        FORBIDDEN_ENCHANTMENTS.add(Enchantments.INFINITY_ARROWS);
        FORBIDDEN_ENCHANTMENTS.add(Enchantments.FIRE_ASPECT);
        FORBIDDEN_ENCHANTMENTS.add(Enchantments.KNOCKBACK);

        FORBIDDEN_LEVELS.put(Enchantments.ALL_DAMAGE_PROTECTION, 2);
        FORBIDDEN_LEVELS.put(Enchantments.FIRE_PROTECTION, 3);
        FORBIDDEN_LEVELS.put(Enchantments.FALL_PROTECTION, 2);
        FORBIDDEN_LEVELS.put(Enchantments.BLAST_PROTECTION, 3);
        FORBIDDEN_LEVELS.put(Enchantments.PROJECTILE_PROTECTION, 3);
        FORBIDDEN_LEVELS.put(Enchantments.THORNS, 2);
        FORBIDDEN_LEVELS.put(Enchantments.SHARPNESS, 2);
        FORBIDDEN_LEVELS.put(Enchantments.SMITE, 4);
        FORBIDDEN_LEVELS.put(Enchantments.MOB_LOOTING, 1);
        FORBIDDEN_LEVELS.put(Enchantments.SWEEPING_EDGE, 1);
        FORBIDDEN_LEVELS.put(Enchantments.BLOCK_EFFICIENCY, 3);
        FORBIDDEN_LEVELS.put(Enchantments.UNBREAKING, 1);
        FORBIDDEN_LEVELS.put(Enchantments.BLOCK_FORTUNE, 1);
        FORBIDDEN_LEVELS.put(Enchantments.POWER_ARROWS, 2);
        FORBIDDEN_LEVELS.put(Enchantments.PUNCH_ARROWS, 1);
    }

    static List<Integer> generateDefaultPointsCosts() {
        List<Integer> costs = new ArrayList<>();
        costs.add(15);
        for (int i = 1; i < DEFAULT_MAX_SKILLS; i++) {
            int previousCost = costs.get(costs.size() - 1);
            int cost = previousCost + 3 + i;
            costs.add(cost);
        }
        return costs;
    }

    static boolean validateItemName(Object object) {
        return object instanceof String name
                && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(name));
    }

    @SubscribeEvent
    static void load(ModConfigEvent event) {
        if (event.getConfig().getSpec() != SPEC) return;
//    amnesia_scroll_penalty = AMNESIA_SCROLL_PENALTY.get();
        grindstone_exp_multiplier = GRINDSTONE_EXP_MULTIPLIER.get();
        show_chat_messages = SHOW_CHAT_MESSAGES.get();
        enable_exp_exchange = ENABLE_EXP_EXCHANGE.get();
//    dragon_drops_amnesia_scroll = DRAGON_DROPS_AMNESIA_SCROLL.get();
        mixture_effects_duration = MIXTURE_EFFECTS_DURATION.get();
        mixture_effects_strength = MIXTURE_EFFECTS_STRENGTH.get();
    }

    public static int getSkillPointCost(int level) {
        if (use_skill_points_array) {
            if (level >= skill_points_costs.size()) {
                return skill_points_costs.get(skill_points_costs.size() - 1);
            }
            return skill_points_costs.get(level);
        }
        return first_skill_cost + (last_skill_cost - first_skill_cost) * level / max_skill_points;
    }

    public static int getGrindTimeWindow() {
        return GRIND_TIME_WINDOW.get();
    }

    public static int getGrindStreakThreshold() {
        return GRIND_STREAK_THRESHOLD.get();
    }

    public static double getGrindInitialPenalty() {
        return GRIND_INITIAL_PENALTY_PERCENT.get() / 100.0;
    }

    public static double getGrindMaxPenalty() {
        return GRIND_MAX_PENALTY_PERCENT.get() / 100.0;
    }

    public static int getGrindMaxStreakLength() {
        return GRIND_MAX_STREAK_LENGTH.get();
    }

    public static double getGrindMinMultiplier() {
        return GRIND_MIN_MULTIPLIER.get();
    }

    public static List<? extends String> getCommonStructures() {
        return COMMON_STRUCTURES.get();
    }

    public static List<? extends String> getUncommonStructures() {
        return UNCOMMON_STRUCTURES.get();
    }

    public static List<? extends String> getRareStructures() {
        return RARE_STRUCTURES.get();
    }

    public static List<? extends String> getEpicStructures() {
        return EPIC_STRUCTURES.get();
    }

    public static List<? extends String> getLegendaryStructures() {
        return LEGENDARY_STRUCTURES.get();
    }

    public static double getTierB(String tier) {
        switch (tier) {
            case "common":
                return TIER1_STRUCTURE_B.get();
            case "uncommon":
                return TIER2_STRUCTURE_B.get();
            case "rare":
                return TIER3_STRUCTURE_B.get();
            case "epic":
                return TIER4_STRUCTURE_B.get();
            case "legendary":
                return TIER5_STRUCTURE_B.get();
            default:
                return TIER1_STRUCTURE_B.get();
        }
    }

    public static int getCraftingGrindTimeWindow() {
        return CRAFTING_GRIND_TIME_WINDOW.get();
    }

    public static double getCraftingGrindPenaltyStep() {
        return CRAFTING_GRIND_PENALTY_STEP_PERCENT.get() / 100.0;
    }

    public static double getCraftingGrindMinMultiplier() {
        return CRAFTING_GRIND_MIN_MULTIPLIER.get();
    }

    public static double getTier1OreB() {
        return TIER1_ORE_B.get();
    }

    public static double getTier2OreB() {
        return TIER2_ORE_B.get();
    }

    public static double getTier3OreB() {
        return TIER3_ORE_B.get();
    }

    public static double getTier4OreB() {
        return TIER4_ORE_B.get();
    }

    public static List<? extends String> getTier1Ores() {
        return TIER1_ORES.get();
    }

    public static List<? extends String> getTier2Ores() {
        return TIER2_ORES.get();
    }

    public static List<? extends String> getTier3Ores() {
        return TIER3_ORES.get();
    }

    public static List<? extends String> getTier4Ores() {
        return TIER4_ORES.get();
    }

    public static int getMiningGrindTimeWindow() {
        return MINING_GRIND_TIME_WINDOW.get();
    }

    public static double getMiningGrindPenaltyStep() {
        return MINING_GRIND_PENALTY_STEP_PERCENT.get() / 100.0;
    }

    public static double getMiningGrindMinMultiplier() {
        return MINING_GRIND_MIN_MULTIPLIER.get();
    }

    public static double getBaseAccuracy() {
        return BASE_ACCURACY.get();
    }

    public static double getCurseChance() {
        return CURSE_CHANCE.get();
    }

    public static double getScaleFactor() {
        return LEVEL_SCALE_FACTOR.get();
    }

    public static double getShieldReduction() {
        return SHIELD_DAMAGE_REDUCTION.get();
    }
}
