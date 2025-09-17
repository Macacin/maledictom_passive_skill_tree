package daripher.skilltree.compat.jei;

import daripher.skilltree.SkillTreeMod;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class JeiCompatibility implements IModPlugin {
  @Override
  public @NotNull ResourceLocation getPluginUid() {
    return new ResourceLocation(SkillTreeMod.MOD_ID, "jei_plugin");
  }

  @Override
  public void registerGuiHandlers(@NotNull IGuiHandlerRegistration registration) {
    //		registration.
  }
}
