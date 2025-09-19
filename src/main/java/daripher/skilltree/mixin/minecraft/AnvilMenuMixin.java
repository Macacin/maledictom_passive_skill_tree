package daripher.skilltree.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import daripher.skilltree.capability.enchant.CraftingXPUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {
    @SuppressWarnings("DataFlowIssue")
    public AnvilMenuMixin() {
        super(null, 0, null, null);
    }

    @ModifyExpressionValue(
            method = "createResult",
            at =
            @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I"),
            require = 0)
    private int uncapEnchantmentLevel(int original) {
        ItemStack base = inputSlots.getItem(0);
        if (base.getItem() == Items.ENCHANTED_BOOK) return original;
        ItemStack addition = inputSlots.getItem(1);
        if (base.getAllEnchantments().isEmpty() && addition.getItem() == Items.ENCHANTED_BOOK) {
            return Integer.MAX_VALUE;
        }
        return original;
    }

    @Inject(method = "onTake", at = @At("HEAD"))
    private void onAnvilTake(Player pPlayer, ItemStack pStack, CallbackInfo ci) {
        System.out.println("Anvil onTake injected! Player: " + (pPlayer instanceof ServerPlayer ? pPlayer.getName().getString() : "client"));
        if (!(pPlayer instanceof ServerPlayer serverPlayer)) return;

        if (pStack.isEmpty()) return;

        AnvilMenu menu = (AnvilMenu) (Object) this;
        int cost = menu.getCost();
        if (cost <= 0) return;

        Slot input1Slot = menu.getSlot(0);
        Slot input2Slot = menu.getSlot(1);
        ItemStack input1 = input1Slot.getItem();
        ItemStack input2 = input2Slot.getItem();

        boolean isCombine = !input2.isEmpty();
        boolean isRepair = !input1.isEmpty() && input1.getDamageValue() > pStack.getDamageValue();

        if (isCombine || isRepair) {
            int level = CraftingXPUtil.getPlayerLevel(serverPlayer);
            int xp = CraftingXPUtil.calculateXP(level);
            System.out.println("Anvil XP trigger for " + serverPlayer.getName().getString() + ": level=" + level + ", xp=" + xp + ", type=" + (isCombine ? "combine" : "repair"));
            CraftingXPUtil.addXP(serverPlayer, xp);
        }
    }
}
