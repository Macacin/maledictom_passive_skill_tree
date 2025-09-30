package daripher.skilltree.skill.bonus.event.strength;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.strength.ExplosionResistanceBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class ExplosionResistanceEvent {

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        DamageSource source = event.getSource();

        if (!source.is(DamageTypeTags.IS_EXPLOSION)) return;

        double totalResistance = PlayerSkillsProvider.get(player).getCachedBonus(ExplosionResistanceBonus.class);
        if (totalResistance <= 0) return;

        totalResistance = Math.min(1.0, totalResistance);

        float originalDamage = event.getAmount();
        event.setAmount((float) (originalDamage * (1 - totalResistance)));
    }
}