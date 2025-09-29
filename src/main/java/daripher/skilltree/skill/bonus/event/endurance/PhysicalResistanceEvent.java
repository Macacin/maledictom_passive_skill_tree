package daripher.skilltree.skill.bonus.event.endurance;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.endurance.PhysicalResistanceBonus;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PhysicalResistanceEvent {
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        DamageSource source = event.getSource();
        if (!player.isAlive()) return;

        double bonus = PlayerSkillsProvider.get(player).getCachedBonus(PhysicalResistanceBonus.class);
        if (bonus == 0) return;

        if ((source.is(DamageTypes.PLAYER_ATTACK) || source.is(DamageTypes.MOB_ATTACK))
                && source.getDirectEntity() instanceof LivingEntity) {

            float originalDamage = event.getAmount();
            float newDamage = originalDamage * (1f - (float) bonus);
            event.setAmount(newDamage);
        }
    }
}