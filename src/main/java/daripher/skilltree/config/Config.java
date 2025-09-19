package daripher.skilltree.config;

import daripher.skilltree.SkillTreeMod;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
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
    public static double gem_drop_chance;
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

        BUILDER.push("Penalties for grind for killing & assisting mobs");
        GRIND_TIME_WINDOW = BUILDER.defineInRange("grind_time_window", 5, 1, 60);  // Seconds
        GRIND_STREAK_THRESHOLD = BUILDER.defineInRange("grind_streak_threshold", 3, 1, 10);  // Actions to start penalty
        GRIND_INITIAL_PENALTY_PERCENT = BUILDER.defineInRange("grind_initial_penalty_percent", 20, 0, 50);  // Start %
        GRIND_MAX_PENALTY_PERCENT = BUILDER.defineInRange("grind_max_penalty_percent", 60, 20, 100);  // Max %
        GRIND_MAX_STREAK_LENGTH = BUILDER.defineInRange("grind_max_streak_length", 20, 5, 50);  // Kills for full max
        GRIND_MIN_MULTIPLIER = BUILDER.defineInRange("grind_min_multiplier", 0.4, 0.1, 1.0);  // Min XP % (0.4 = 40%)
        BUILDER.pop();

        BUILDER.push("Structure discovery tiers");
        COMMON_STRUCTURES = BUILDER.defineList("common_structures", List.of("minecraft:village_plains", "minecraft:village_desert", "minecraft:village_savanna", "minecraft:village_snowy", "minecraft:village_taiga", "minecraft:ruined_portal", "minecraft:shipwreck", "minecraft:ocean_monument", "minecraft:ancient_city"), o -> o instanceof String);
        UNCOMMON_STRUCTURES = BUILDER.defineList("uncommon_structures", List.of("minecraft:desert_pyramid", "minecraft:jungle_pyramid", "minecraft:igloo"), o -> o instanceof String);
        RARE_STRUCTURES = BUILDER.defineList("rare_structures", List.of("minecraft:stronghold", "minecraft:end_city", "minecraft:woodland_mansion"), o -> o instanceof String);
        EPIC_STRUCTURES = BUILDER.defineList("epic_structures", List.of("minecraft:bastion_remnant", "minecraft:ancient_city"), o -> o instanceof String);  // Example
        LEGENDARY_STRUCTURES = BUILDER.defineList("legendary_structures", List.of("minecraft:nether_fortress"), o -> o instanceof String);  // Example
        BUILDER.pop();

        BUILDER.push("Enchantment, smithing, anvil exp tweaks");
        ENCHANTMENT_COEFFICIENT = BUILDER.defineInRange("formula_coef", 3.0, 1.0, 100.0);
        CRAFTING_GRIND_TIME_WINDOW = BUILDER.defineInRange("crafting_grind_time_window", 60, 1, 300);  // Seconds
        CRAFTING_GRIND_PENALTY_STEP_PERCENT = BUILDER.defineInRange("crafting_grind_penalty_step_percent", 20, 0, 50);  // % per streak
        CRAFTING_GRIND_MIN_MULTIPLIER = BUILDER.defineInRange("crafting_grind_min_multiplier", 0.2, 0.1, 1.0);
        BUILDER.pop();

        SPEC = BUILDER.build();
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

    public static int getTierB(String tier) {
        switch (tier) {
            case "common":
                return 25;
            case "uncommon":
                return 50;
            case "rare":
                return 100;
            case "epic":
                return 150;
            case "legendary":
                return 200;
            default:
                return 25;
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
}
