package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.capability.enchant.CraftingXPUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemCombinerMenu.class)
public abstract class ItemCombinerMixin {
    @Unique
    private int capturedAnvilCost = -1;
    @Unique
    private int capturedAnvilInput1Damage = -1;
    @Unique
    private boolean capturedAnvilInput2Empty = true;

    @Inject(method = "quickMoveStack(Lnet/minecraft/world/entity/player/Player;I)Lnet/minecraft/world/item/ItemStack;", at = @At("HEAD"))
    private void captureAnvilState(Player pPlayer, int pIndex, CallbackInfoReturnable<ItemStack> cir) {
        ItemCombinerMenu menu = (ItemCombinerMenu) (Object) this;
        if (!(menu instanceof AnvilMenu anvilMenu)) return;
        if (pIndex != 2) return;
        this.capturedAnvilCost = anvilMenu.getCost();
        Slot input1Slot = anvilMenu.getSlot(0);
        Slot input2Slot = anvilMenu.getSlot(1);
        this.capturedAnvilInput1Damage = input1Slot.hasItem() ? input1Slot.getItem().getDamageValue() : -1;
        this.capturedAnvilInput2Empty = !input2Slot.hasItem();
    }

    @Inject(method = "quickMoveStack(Lnet/minecraft/world/entity/player/Player;I)Lnet/minecraft/world/item/ItemStack;", at = @At("RETURN"))
    private void onQuickMove(Player pPlayer, int pIndex, CallbackInfoReturnable<ItemStack> cir) {
        ItemCombinerMenu menu = (ItemCombinerMenu) (Object) this;
        if (menu instanceof SmithingMenu) {
            if (!(pPlayer instanceof ServerPlayer serverPlayer)) return;
            if (pIndex != 3) return;
            ItemStack returned = cir.getReturnValue();
            if (returned.isEmpty()) return;
            int level = CraftingXPUtil.getPlayerLevel(serverPlayer);
            double xp = CraftingXPUtil.calculateXP(level);
            CraftingXPUtil.addXP(serverPlayer, xp);
        } else if (menu instanceof AnvilMenu) {
            if (!(pPlayer instanceof ServerPlayer serverPlayer)) return;
            if (pIndex != 2) return;
            ItemStack returned = cir.getReturnValue();
            if (returned.isEmpty()) return;
            if (this.capturedAnvilCost <= 0) return;
            boolean isCombine = !this.capturedAnvilInput2Empty;
            boolean isRepair = this.capturedAnvilInput1Damage > returned.getDamageValue();
            if (isCombine || isRepair) {
                int level = CraftingXPUtil.getPlayerLevel(serverPlayer);
                double xp = CraftingXPUtil.calculateXP(level);
                CraftingXPUtil.addXP(serverPlayer, xp);
            }
            this.capturedAnvilCost = -1;
            this.capturedAnvilInput1Damage = -1;
            this.capturedAnvilInput2Empty = true;
        }
    }
}