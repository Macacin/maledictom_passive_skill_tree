package daripher.skilltree.skill.bonus.event.wisdom;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.wisdom.MagicResistanceBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class MagicResistanceEvent {
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        DamageSource source = event.getSource();

        boolean isMagic = source.is(DamageTypes.MAGIC) || source.getMsgId().contains("magic") || source.is(DamageTypes.INDIRECT_MAGIC);
        if (!isMagic) return;

        double bonus = PlayerSkillsProvider.get(player).getCachedBonus(MagicResistanceBonus.class);
        if (bonus == 0) return;

        float originalDamage = event.getAmount();
        float newDamage = (float) (originalDamage * (1 - bonus));
        event.setAmount(newDamage);
    }
}