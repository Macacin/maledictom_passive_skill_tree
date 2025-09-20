package daripher.skilltree.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import daripher.skilltree.config.Config;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(targets = "net.minecraft.world.entity.npc.VillagerTrades$EnchantBookForEmeralds")
public abstract class EnchantBookForEmeraldsMixin {

    @Shadow private int villagerXp;

    @ModifyReturnValue(
            method = "getOffer(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/util/RandomSource;)Lnet/minecraft/world/item/trading/MerchantOffer;",
            at = @At("RETURN")
    )
    private MerchantOffer filterForbiddenEnchants(MerchantOffer original, Entity trader, RandomSource random) {
        if (original == null) return null;

        for (int attempts = 0; attempts < 100; attempts++) {
            List<Enchantment> list = BuiltInRegistries.ENCHANTMENT.stream()
                    .filter(Enchantment::isTradeable)
                    .collect(Collectors.toList());
            if (list.isEmpty()) continue;

            Enchantment ench = list.get(random.nextInt(list.size()));
            int level = Mth.nextInt(random, ench.getMinLevel(), ench.getMaxLevel());

            if (Config.FORBIDDEN_ENCHANTMENTS.contains(ench)) continue;
            Integer maxAllowed = Config.FORBIDDEN_LEVELS.get(ench);
            if (maxAllowed != null && level > maxAllowed) continue;

            ItemStack book = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(ench, level));
            int cost = 2 + random.nextInt(5 + level * 10) + 3 * level;
            if (ench.isTreasureOnly()) cost *= 2;
            if (cost > 64) cost = 64;

            return new MerchantOffer(new ItemStack(Items.EMERALD, cost), new ItemStack(Items.BOOK), book, 12, this.villagerXp, 0.2F);
        }

        return null;
    }
}