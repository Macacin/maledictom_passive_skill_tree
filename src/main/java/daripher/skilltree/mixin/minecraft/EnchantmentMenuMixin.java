package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.container.menu.EnchantmentMenuExtension;
//import daripher.skilltree.skill.bonus.SkillBonusHandler;

import java.util.List;
import javax.annotation.Nonnull;

import daripher.skilltree.skill.bonus.event.intelligence.ContextHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentMenuMixin implements EnchantmentMenuExtension {
    private final int[] costsBeforeReduction = new int[3];
    public @Shadow
    @Final int[] costs;
    private @Shadow
    @Final DataSlot enchantmentSeed;
    @Shadow(remap = false) private Container enchantSlots;

//    @Redirect(
//            method = {"lambda$slotsChanged$0", "m_39483_"},
//            at =
//            @At(
//                    value = "INVOKE",
//                    target =
//                            "Lnet/minecraftforge/event/ForgeEventFactory;onEnchantmentLevelSet(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;IILnet/minecraft/world/item/ItemStack;I)I"))
//    private int reduceEnchantmentCost(
//            Level level, BlockPos pos, int slot, int power, ItemStack itemStack, int enchantmentLevel) {
//        int cost =
//                ForgeEventFactory.onEnchantmentLevelSet(level, pos, slot, power, itemStack, costs[slot]);
//        costsBeforeReduction[slot] = cost;
//        @SuppressWarnings("DataFlowIssue")
//        EnchantmentMenu menu = (EnchantmentMenu) (Object) this;
//        Player player = ContainerHelper.getViewingPlayer(menu);
//        if (player == null) return cost;
//        return SkillBonusHandler.adjustEnchantmentCost(cost, player);
//    }

//    @Redirect(
//            method = {"lambda$slotsChanged$0", "m_39483_"},
//            at =
//            @At(
//                    value = "INVOKE",
//                    target =
//                            "Lnet/minecraft/world/inventory/EnchantmentMenu;getEnchantmentList(Lnet/minecraft/world/item/ItemStack;II)Ljava/util/List;"))
//    private List<EnchantmentInstance> amplifyEnchantmentsVisually(
//            EnchantmentMenu menu, ItemStack itemStack, int slot, int cost) {
//        return amplifyEnchantments(itemStack, slot);
//    }

//    @Redirect(
//            method = {"lambda$clickMenuButton$1", "m_39475_"},
//            at =
//            @At(
//                    value = "INVOKE",
//                    target =
//                            "Lnet/minecraft/world/inventory/EnchantmentMenu;getEnchantmentList(Lnet/minecraft/world/item/ItemStack;II)Ljava/util/List;"))
//    private List<EnchantmentInstance> amplifyEnchantmentsOnButtonClick(
//            EnchantmentMenu menu, ItemStack itemStack, int slot, int cost) {
//        return amplifyEnchantments(itemStack, slot);
//    }
//
//    private List<EnchantmentInstance> amplifyEnchantments(ItemStack itemStack, int slot) {
//        List<EnchantmentInstance> enchantments =
//                getEnchantmentList(itemStack, slot, costsBeforeReduction[slot]);
//        RandomSource random = RandomSource.create(enchantmentSeed.get());
//        @SuppressWarnings("DataFlowIssue")
//        EnchantmentMenu menu = (EnchantmentMenu) (Object) this;
//        Player player = ContainerHelper.getViewingPlayer(menu);
//        if (player == null) return enchantments;
//        SkillBonusHandler.amplifyEnchantments(enchantments, random, player);
//        return enchantments;
//    }

    @SuppressWarnings({"unused", "DataFlowIssue"})
    @Shadow
    private @Nonnull List<EnchantmentInstance> getEnchantmentList(
            ItemStack stack, int slot, int cost) {
        return null;
    }

    @Override
    public int[] getCostsBeforeReduction() {
        return costsBeforeReduction;
    }

    @Unique
    private Player skilltree$player;

    protected EnchantmentMenuMixin(MenuType<?> pMenuType, int pId) {
        super();
    }

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V", at = @At("TAIL"))
    private void setPlayer(int pId, Inventory pPlayerInventory, ContainerLevelAccess pAccess, CallbackInfo ci) {
        this.skilltree$player = pPlayerInventory.player;
    }

    @Inject(method = "slotsChanged", at = @At("HEAD"))
    private void setCurrentEnchanter(Container pInventory, CallbackInfo ci) {
        if (pInventory == this.enchantSlots) {
            System.out.println("Setting enchanter for side: " + (this.skilltree$player.level().isClientSide ? "client" : "server") + " player: " + this.skilltree$player.getClass().getSimpleName());
            ContextHelper.CURRENT_ENCHANTER.set(this.skilltree$player);
        }
    }

    @Inject(method = "slotsChanged", at = @At("TAIL"))
    private void removeCurrentEnchanter(Container pInventory, CallbackInfo ci) {
        if (pInventory == this.enchantSlots) {
            System.out.println("Removing enchanter for side: " + (this.skilltree$player.level().isClientSide ? "client" : "server"));
            ContextHelper.CURRENT_ENCHANTER.remove();
        }
    }
}
