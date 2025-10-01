package daripher.skilltree.skill.bonus.event.wisdom;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.wisdom.DoubleLootChanceBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Random;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class DoubleLootEvent {
    private static final Random RANDOM = new Random();

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;

        double chance = PlayerSkillsProvider.get(player).getCachedBonus(DoubleLootChanceBonus.class);
        if (chance <= 0) return;

        boolean isDouble = RANDOM.nextDouble() < chance;
        if (!isDouble) return;

        var originalDrops = event.getDrops();
        var newDrops = new ArrayList<ItemEntity>();
        for (ItemEntity drop : originalDrops) {
            ItemStack stack = drop.getItem().copy();
            ItemEntity newDrop = new ItemEntity(drop.level(), drop.getX(), drop.getY(), drop.getZ(), stack);
            newDrops.add(newDrop);
        }
        originalDrops.addAll(newDrops);
    }
}