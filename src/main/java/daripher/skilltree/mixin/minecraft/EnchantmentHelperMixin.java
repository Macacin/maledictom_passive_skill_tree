package daripher.skilltree.mixin.minecraft;  // Подставь свой пакет mixin.minecraft

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import daripher.skilltree.config.Config;  // Или твой конфиг
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @ModifyVariable(
            method = "getEnchantmentCost(Lnet/minecraft/util/RandomSource;IILnet/minecraft/world/item/ItemStack;)I",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 1
    )
    private static int scaleBookshelfPower(int pPower) {
        pPower = Math.min(pPower, 15);
        return (int) (pPower * Config.LEVEL_SCALE_FACTOR.get());
    }

    @ModifyReturnValue(
            method = "selectEnchantment(Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/item/ItemStack;IZ)Ljava/util/List;",
            at = @At("RETURN")
    )
    private static List<EnchantmentInstance> addCurseWithChance(List<EnchantmentInstance> original, RandomSource pRandom, ItemStack pStack, int pLevel, boolean pAllowTreasure) {
        if (pAllowTreasure) return original;
        if (pRandom.nextDouble() < Config.getCurseChance()) {
            List<Enchantment> curses = BuiltInRegistries.ENCHANTMENT.stream()
                    .filter(Enchantment::isCurse)
                    .filter(e -> e.canEnchant(pStack))
                    .toList();
            if (!curses.isEmpty()) {
                Enchantment curse = curses.get(pRandom.nextInt(curses.size()));
                original.add(new EnchantmentInstance(curse, 1));
            }
        }
        return original;
    }
}