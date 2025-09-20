package daripher.skilltree;

import daripher.skilltree.config.ClientConfig;
import daripher.skilltree.config.Config;
import daripher.skilltree.init.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

@Mod(SkillTreeMod.MOD_ID)
public class SkillTreeMod {
    public static final String MOD_ID = "skilltree";
    public static final Logger LOGGER = LogManager.getLogger(SkillTreeMod.MOD_ID);
    public static final UUID SHIELD_SLOWDOWN_UUID = UUID.fromString("1b9d6bcd-bbfd-4b2d-9b5d-ab8dfbbd4bed");

    public SkillTreeMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        PSTItems.REGISTRY.register(modEventBus);
        PSTAttributes.REGISTRY.register(modEventBus);
        PSTRecipeSerializers.REGISTRY.register(modEventBus);
        PSTEffects.REGISTRY.register(modEventBus);
        PSTSkillBonuses.REGISTRY.register(modEventBus);
        PSTLivingConditions.REGISTRY.register(modEventBus);
        PSTLivingMultipliers.REGISTRY.register(modEventBus);
        PSTDamageConditions.REGISTRY.register(modEventBus);
        PSTItemBonuses.REGISTRY.register(modEventBus);
        PSTItemConditions.REGISTRY.register(modEventBus);
        PSTEnchantmentConditions.REGISTRY.register(modEventBus);
        PSTLootPoolEntries.REGISTRY.register(modEventBus);
        PSTEventListeners.REGISTRY.register(modEventBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }

    public static AttributeModifier getShieldSlowdownModifier() {
        return new AttributeModifier(SHIELD_SLOWDOWN_UUID, "Shield Slowdown", -Config.SHIELD_SLOWDOWN.get(), AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}
