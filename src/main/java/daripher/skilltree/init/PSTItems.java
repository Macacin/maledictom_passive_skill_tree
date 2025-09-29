package daripher.skilltree.init;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.item.AmnesiaScrollItem;
import daripher.skilltree.item.ResourceItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class PSTItems {
  public static final DeferredRegister<Item> REGISTRY =
      DeferredRegister.create(ForgeRegistries.ITEMS, SkillTreeMod.MOD_ID);
//  public static final RegistryObject<Item> WISDOM_SCROLL =
//      REGISTRY.register("wisdom_scroll", WisdomScrollItem::new);
//  public static final RegistryObject<Item> AMNESIA_SCROLL =
//      REGISTRY.register("amnesia_scroll", AmnesiaScrollItem::new);
  // resources
  public static final RegistryObject<Item> COPPER_NUGGET =
      REGISTRY.register("copper_nugget", ResourceItem::new);
}
