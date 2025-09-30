package daripher.skilltree.skill.bonus.event.strength;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.strength.ProjectileDamageBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class ProjectileDamageEvent {

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof ServerPlayer player)) return;

        if (!source.is(DamageTypeTags.IS_PROJECTILE)) return;
        if (source.is(DamageTypes.MAGIC) || source.is(DamageTypes.INDIRECT_MAGIC)) return;
        if (source.is(DamageTypeTags.IS_FIRE)) return;
        if (source.is(DamageTypeTags.IS_EXPLOSION)) return;

        double totalBonus = PlayerSkillsProvider.get(player).getCachedBonus(ProjectileDamageBonus.class);
        if (totalBonus <= 0) return;

        float originalDamage = event.getAmount();
        event.setAmount((float) (originalDamage * (1 + totalBonus)));
    }
}