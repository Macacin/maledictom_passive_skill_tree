package daripher.skilltree.skill.bonus.event.wisdom;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.wisdom.PotionDurationBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.PotionItem;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class PotionDurationEvent {
    private static Field durationField;

    static {
        try {
            durationField = MobEffectInstance.class.getDeclaredField("duration");
            durationField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public static void onMobEffectAdded(MobEffectEvent.Added event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        MobEffectInstance instance = event.getEffectInstance();

        boolean isFromPotion = player.getUseItem().getItem() instanceof PotionItem;
        if (!isFromPotion) return;

        double bonus = PlayerSkillsProvider.get(player).getCachedBonus(PotionDurationBonus.class);
        if (bonus == 0) return;

        int originalDuration = instance.getDuration();
        int newDuration = (int) (originalDuration * (1 + bonus));

        if (newDuration <= 0) {
            player.removeEffect(instance.getEffect());
            return;
        }

        try {
            durationField.setInt(instance, newDuration);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}