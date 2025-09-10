package daripher.skilltree.data.generation;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.PSTItems;
import daripher.skilltree.init.PSTTags;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class PSTRecipesProvider extends RecipeProvider {
  public PSTRecipesProvider(DataGenerator dataGenerator) {
    super(dataGenerator.getPackOutput());
  }

  @Override
  protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
    // resources
    packing(Items.COPPER_INGOT, PSTTags.NUGGETS_COPPER, consumer);
    unpacking(PSTItems.COPPER_NUGGET, Tags.Items.INGOTS_COPPER, consumer);
  }

  protected void quiver(
      RegistryObject<Item> result, Item material, Consumer<FinishedRecipe> consumer) {
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, result.get())
        .define('#', material)
        .define('l', Items.LEATHER)
        .define('s', Items.STRING)
        .pattern("#ls")
        .pattern("#ls")
        .pattern("#ls")
        .group(getItemName(result.get()))
        .unlockedBy(getHasName(material), has(material))
        .save(consumer, getRecipeId(result.get()));
  }

  protected void quiver(
      RegistryObject<Item> result, TagKey<Item> material, Consumer<FinishedRecipe> consumer) {
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, result.get())
        .define('#', material)
        .define('l', Items.LEATHER)
        .define('s', Items.STRING)
        .pattern("#ls")
        .pattern("#ls")
        .pattern("#ls")
        .group(getItemName(result.get()))
        .unlockedBy(getHasName(material), has(material))
        .save(consumer, getRecipeId(result.get()));
  }

  protected void quiver(RegistryObject<Item> result, Consumer<FinishedRecipe> consumer) {
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, result.get())
        .define('l', Items.LEATHER)
        .define('s', Items.STRING)
        .pattern("ls")
        .pattern("ls")
        .pattern("ls")
        .group(getItemName(result.get()))
        .unlockedBy(getHasName(Items.LEATHER), has(Items.LEATHER))
        .save(consumer, getRecipeId(result.get()));
  }

  protected void necklace(
      RegistryObject<Item> result, Item material, Consumer<FinishedRecipe> consumer) {
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result.get())
        .define('#', material)
        .define('n', Tags.Items.NUGGETS_GOLD)
        .pattern("nnn")
        .pattern("n n")
        .pattern("n#n")
        .group(getItemName(result.get()))
        .unlockedBy(getHasName(material), has(material))
        .save(consumer, getRecipeId(result.get()));
  }

  protected void necklace(RegistryObject<Item> result, Consumer<FinishedRecipe> consumer) {
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result.get())
        .define('n', Tags.Items.NUGGETS_GOLD)
        .pattern("nnn")
        .pattern("n n")
        .pattern("nnn")
        .group(getItemName(result.get()))
        .unlockedBy(getHasName(Tags.Items.NUGGETS_GOLD), has(Tags.Items.NUGGETS_GOLD))
        .save(consumer, getRecipeId(result.get()));
  }

  protected void ring(
      RegistryObject<Item> result, TagKey<Item> material, Consumer<FinishedRecipe> consumer) {
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result.get())
        .define('#', material)
        .pattern(" # ")
        .pattern("# #")
        .pattern(" # ")
        .group(getItemName(result.get()))
        .unlockedBy(getHasName(material), has(material))
        .save(consumer, getRecipeId(result.get()));
  }

  protected void packing(Item result, TagKey<Item> material, Consumer<FinishedRecipe> consumer) {
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result)
        .define('#', material)
        .pattern("###")
        .pattern("###")
        .pattern("###")
        .group(getItemName(result))
        .unlockedBy(getHasName(material), has(material))
        .save(consumer, getRecipeId(result));
  }

  protected void unpacking(
      RegistryObject<Item> result, TagKey<Item> material, Consumer<FinishedRecipe> consumer) {
    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, result.get(), 9)
        .requires(material)
        .group(getItemName(result.get()))
        .unlockedBy(getHasName(material), has(material))
        .save(consumer, getRecipeId(result.get()));
  }

  protected String getHasName(TagKey<Item> material) {
    return "has_" + material.location().getPath().replaceAll("/", "_");
  }

  private ResourceLocation getRecipeId(Item item) {
    ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
    return new ResourceLocation(SkillTreeMod.MOD_ID, Objects.requireNonNull(id).getPath());
  }
}
