package daripher.skilltree.skill.bonus.event.intelligence;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.intelligence.VoidResistanceBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class VoidResistanceEvent {
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.level().isClientSide) return;

        DamageSource source = event.getSource();
        if (!source.is(DamageTypes.FELL_OUT_OF_WORLD)) return;

        double bonus = PlayerSkillsProvider.get(player).getCachedBonus(VoidResistanceBonus.class);
        if (bonus == 0) return;

        float originalDamage = event.getAmount();
        float newDamage = (float) (originalDamage * (1 - bonus));
        event.setAmount(newDamage);
    }
}