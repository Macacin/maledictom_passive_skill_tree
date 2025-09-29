package daripher.skilltree.skill.bonus.event.strength;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.strength.CritChanceBonus;
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
        if (isFalling) return;

        double totalChance = PlayerSkillsProvider.get(player).getCachedBonus(CritChanceBonus.class);
        if (totalChance <= 0) return;

        double roll = RANDOM.nextDouble();

        if (roll < totalChance) {
            float originalDamage = event.getAmount();
            event.setAmount(originalDamage * 2f);
        }
    }
}