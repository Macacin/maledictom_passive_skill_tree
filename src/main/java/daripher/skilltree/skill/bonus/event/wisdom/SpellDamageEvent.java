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
        System.out.println("[SpellDamageEvent] Event triggered for entity: " + event.getEntity());

        boolean modLoaded = ModList.get().isLoaded("irons_spellbooks");
        System.out.println("[SpellDamageEvent] Is irons_spellbooks loaded? " + modLoaded);
        if (!modLoaded) return;

        DamageSource source = event.getSource();
        System.out.println("[SpellDamageEvent] Damage source: " + source + ", msgId: " + source.getMsgId());

        if (source.getEntity() == null) {
            System.out.println("[SpellDamageEvent] Failed: Source entity is null");
            return;
        }
        boolean isPlayer = source.getEntity() instanceof ServerPlayer;
        System.out.println("[SpellDamageEvent] Is source from player? " + isPlayer + " (entity: " + source.getEntity() + ")");
        if (!isPlayer) return;

        boolean containsMagic = source.getMsgId().contains("magic");
        System.out.println("[SpellDamageEvent] Does msgId contain 'magic'? " + containsMagic + " (msgId: " + source.getMsgId() + ")");
        if (!containsMagic) return;

        double bonus = PlayerSkillsProvider.get((ServerPlayer) source.getEntity()).getCachedBonus(SpellDamageBonus.class);
        System.out.println("[SpellDamageEvent] Cached bonus value: " + bonus);
        if (bonus == 0) return;

        float originalDamage = event.getAmount();
        float newDamage = (float) (originalDamage * (1 + bonus));
        event.setAmount(newDamage);

        System.out.println("[SpellDamageEvent] Original Damage: " + originalDamage);
        System.out.println("[SpellDamageEvent] Applied Bonus: " + bonus);
        System.out.println("[SpellDamageEvent] New Damage: " + newDamage);
    }
}