package daripher.skilltree.skill.bonus.event.intelligence;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.intelligence.DurabilityBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class DurabilityBonusEvent {
    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.level().isClientSide) return;

        double bonus = PlayerSkillsProvider.get(player).getCachedBonus(DurabilityBonus.class);
        if (bonus == 0) return;

        ItemStack crafted = event.getCrafting();
        if (crafted.isEmpty() || !isDurableItem(crafted)) return;

        int baseDurability = crafted.getMaxDamage();

        int newDurability = (int) Math.round(baseDurability * (1 + bonus));
        crafted.getOrCreateTag().putInt("CustomMaxDamage", newDurability);

        int afterDurability = crafted.getMaxDamage();
    }

    private static boolean isDurableItem(ItemStack stack) {
        return stack.isDamageableItem();
    }
}