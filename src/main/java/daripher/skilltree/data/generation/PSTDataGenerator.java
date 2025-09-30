package daripher.skilltree.data.generation;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.data.generation.skills.PSTSkillTreesProvider;
import daripher.skilltree.data.generation.skills.PSTSkillsProvider;
import daripher.skilltree.data.generation.translation.PSTEnglishTranslationProvider;
import daripher.skilltree.data.generation.translation.PSTRussianTranslationProvider;
import daripher.skilltree.data.generation.WeaponTypesProvider;  // Добавь импорт, если создал этот класс

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = Bus.MOD)
public class PSTDataGenerator {
  @SubscribeEvent
  public static void onGatherData(GatherDataEvent event) {
    DataGenerator dataGenerator = event.getGenerator();
    ExistingFileHelper fileHelper = event.getExistingFileHelper();
    CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

    PSTBlockTagsProvider blockTagsProvider =
            new PSTBlockTagsProvider(dataGenerator, lookupProvider, fileHelper);
    dataGenerator.addProvider(event.includeServer(), blockTagsProvider);
    dataGenerator.addProvider(
            event.includeServer(),
            new PSTItemTagsProvider(dataGenerator, lookupProvider, blockTagsProvider, fileHelper));
    dataGenerator.addProvider(event.includeServer(), new PSTRecipesProvider(dataGenerator));

    dataGenerator.addProvider(
            event.includeClient(), new PSTEnglishTranslationProvider(dataGenerator));
    dataGenerator.addProvider(
            event.includeClient(), new PSTRussianTranslationProvider(dataGenerator));

    PSTSkillsProvider skillsProvider = new PSTSkillsProvider(dataGenerator);
    dataGenerator.addProvider(event.includeServer(), skillsProvider);
    dataGenerator.addProvider(
            event.includeServer(), new PSTSkillTreesProvider(dataGenerator, skillsProvider));

    dataGenerator.addProvider(event.includeServer(), new WeaponTypesProvider(dataGenerator.getPackOutput()));
  }
}