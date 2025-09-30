package daripher.skilltree.skill.bonus.event.strength;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.strength.ProjectileCritChanceBonus;
import daripher.skilltree.skill.bonus.player.strength.ProjectileCritDamageBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class ProjectileCritEvent {
    private static final Random RANDOM = new Random();
    private static final float BASE_CRIT_MULTIPLIER = 1.8f;

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof ServerPlayer player)) return;

        if (!source.is(DamageTypeTags.IS_PROJECTILE)) return;
        if (source.is(DamageTypes.MAGIC) || source.is(DamageTypes.INDIRECT_MAGIC)) return;
        if (source.is(DamageTypeTags.IS_FIRE)) return;
        if (source.is(DamageTypeTags.IS_EXPLOSION)) return;

        double totalChance = PlayerSkillsProvider.get(player).getCachedBonus(ProjectileCritChanceBonus.class);
        if (totalChance <= 0) return;

        double roll = RANDOM.nextDouble();

        if (roll < totalChance) {
            double totalDamageBonus = PlayerSkillsProvider.get(player).getCachedBonus(ProjectileCritDamageBonus.class);
            float multiplier = BASE_CRIT_MULTIPLIER + (float) totalDamageBonus;

            float originalDamage = event.getAmount();
            event.setAmount(originalDamage * multiplier);
        }
    }
}