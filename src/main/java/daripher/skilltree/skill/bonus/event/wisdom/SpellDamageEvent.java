package daripher.skilltree.skill.bonus.event.wisdom;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.wisdom.SpellDamageBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class SpellDamageEvent {
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        boolean modLoaded = ModList.get().isLoaded("irons_spellbooks");
        if (!modLoaded) return;

        DamageSource source = event.getSource();

        if (source.getEntity() == null) return;
        boolean isPlayer = source.getEntity() instanceof ServerPlayer;
        if (!isPlayer) return;

        boolean containsMagic = source.getMsgId().contains("magic");
        if (!containsMagic) return;

        double bonus = PlayerSkillsProvider.get((ServerPlayer) source.getEntity()).getCachedBonus(SpellDamageBonus.class);
        if (bonus == 0) return;

        float originalDamage = event.getAmount();
        float newDamage = (float) (originalDamage * (1 + bonus));
        event.setAmount(newDamage);
    }
}