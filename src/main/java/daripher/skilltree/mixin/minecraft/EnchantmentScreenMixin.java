package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.capability.enchant.CraftingXPUtil;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.EnchantmentMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentMenu.class)
public class EnchantmentScreenMixin {
    @Shadow public int[] costs;

    @Unique
    private int capturedCost = -1;

    @Inject(method = "clickMenuButton(Lnet/minecraft/world/entity/player/Player;I)Z", at = @At("HEAD"))
    private void captureCost(Player player, int buttonId, CallbackInfoReturnable<Boolean> cir) {
        if (buttonId >= 0 && buttonId < 3) {
            this.capturedCost = this.costs[buttonId];
        }
    }

    @Inject(method = "clickMenuButton(Lnet/minecraft/world/entity/player/Player;I)Z", at = @At("RETURN"))
    private void onEnchantApplied(Player player, int buttonId, CallbackInfoReturnable<Boolean> cir) {
        System.out.println("Enchant clickMenuButton injected! Player: " + (player instanceof ServerPlayer ? player.getName().getString() : "client"));
        System.out.println("Button ID: " + buttonId);
        System.out.println("Return value: " + cir.getReturnValue());
        if (player instanceof ServerPlayer) {
            System.out.println("Is server player: yes");
        } else {
            System.out.println("Is server player: no");
        }
        if (buttonId >= 0 && buttonId < 3 && player instanceof ServerPlayer serverPlayer && cir.getReturnValue()) {
            System.out.println("Passed all checks");
            System.out.println("Captured Cost: " + this.capturedCost);
            if (this.capturedCost > 0) {
                int level = CraftingXPUtil.getPlayerLevel(serverPlayer);
                int xp = CraftingXPUtil.calculateXP(level);
                System.out.println("Enchanting XP trigger for " + serverPlayer.getName().getString() + ": level=" + level + ", xp=" + xp);
                CraftingXPUtil.addXP(serverPlayer, xp);
            }
            this.capturedCost = -1; // Reset after use
        }
    }
}
