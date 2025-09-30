package daripher.skilltree.skill.bonus.event.strength;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.strength.NoArmorDamageBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class NoArmorDamageEvent {

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof ServerPlayer player)) return;
        if (source.getDirectEntity() == null) return;
        boolean noArmor = true;
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack item = player.getItemBySlot(slot);
            if (!item.isEmpty()) {
                noArmor = false;
                break;
            }
        }

        if (!noArmor) return;

        double totalBonus = PlayerSkillsProvider.get(player).getCachedBonus(NoArmorDamageBonus.class);
        if (totalBonus <= 0) return;

        float originalDamage = event.getAmount();
        event.setAmount((float) (originalDamage * (1 + totalBonus)));
    }
}