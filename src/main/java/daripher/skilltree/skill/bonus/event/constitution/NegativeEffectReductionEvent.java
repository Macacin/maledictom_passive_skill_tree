package daripher.skilltree.skill.bonus.event.constitution;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.constitution.NegativeEffectReductionBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class NegativeEffectReductionEvent {
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
        if (instance.getEffect().isBeneficial()) return;
        double reduction = PlayerSkillsProvider.get(player).getCachedBonus(NegativeEffectReductionBonus.class);
        if (reduction == 0) return;
        int newDuration = (int) (instance.getDuration() * (1 - reduction));
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