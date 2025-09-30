package daripher.skilltree.skill.bonus.event.strength;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.strength.CritChanceBonus;
import daripher.skilltree.skill.bonus.player.strength.CritDamageBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class CritChanceEvent {
    private static final Random RANDOM = new Random();
    private static final float BASE_CUSTOM_CRIT_MULTIPLIER = 1.8f;

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof ServerPlayer player)) return;

        if (!source.is(DamageTypes.PLAYER_ATTACK)) return;
        if (source.is(DamageTypeTags.IS_PROJECTILE)) return;
        if (source.is(DamageTypeTags.IS_EXPLOSION)) return;
        if (source.is(DamageTypes.MAGIC) || source.is(DamageTypes.INDIRECT_MAGIC)) return;
        if (source.is(DamageTypeTags.IS_FIRE)) return;

        Vec3 velocity = player.getDeltaMovement();
        boolean isFalling = !player.onGround() && velocity.y < 0;
        double totalCritDamage = PlayerSkillsProvider.get(player).getCachedBonus(CritDamageBonus.class);

        if (isFalling) {
            if (!player.isSprinting()) {
                if (totalCritDamage > 0) {
                    event.setAmount(event.getAmount() * (float) (1 + totalCritDamage));
                }
            }
            return;
        }

        double totalChance = PlayerSkillsProvider.get(player).getCachedBonus(CritChanceBonus.class);
        if (totalChance <= 0) return;

        if (RANDOM.nextDouble() < totalChance) {
            float multiplier = BASE_CUSTOM_CRIT_MULTIPLIER;
            if (totalCritDamage > 0) {
                multiplier += (float) (1 + totalCritDamage);
            }
            event.setAmount(event.getAmount() * multiplier);
        }
    }
}