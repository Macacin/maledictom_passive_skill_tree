package daripher.skilltree.skill.bonus.event.constitution;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.constitution.DamageReflectionBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class DamageReflectionEvent {
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) return;
        double chance = PlayerSkillsProvider.get(player).getCachedBonus(DamageReflectionBonus.class);
        if (chance == 0 || Math.random() > chance) return;
        float reflectedDamage = event.getAmount() * 0.7f;
        attacker.hurt(player.level().damageSources().thorns(player), reflectedDamage);
    }
}