package daripher.skilltree.skill.bonus.event.wisdom;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.wisdom.MagicCritChanceBonus;
import daripher.skilltree.skill.bonus.player.wisdom.MagicCritDamageBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class MagicCritEvent {
    private static final Random RANDOM = new Random();
    private static final float BASE_MAGIC_CRIT_MULTIPLIER = 1.8f;

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        System.out.println("[MagicCritEvent] Event triggered for entity: " + event.getEntity());

        boolean modLoaded = ModList.get().isLoaded("irons_spellbooks");
        System.out.println("[MagicCritEvent] Is irons_spellbooks loaded? " + modLoaded);
        if (!modLoaded) return;

        DamageSource source = event.getSource();
        System.out.println("[MagicCritEvent] Damage source: " + source + ", msgId: " + source.getMsgId());

        if (source.getEntity() == null) {
            System.out.println("[MagicCritEvent] Failed: Source entity is null");
            return;
        }
        boolean isPlayer = source.getEntity() instanceof ServerPlayer;
        System.out.println("[MagicCritEvent] Is source from player? " + isPlayer + " (entity: " + source.getEntity() + ")");
        if (!isPlayer) return;

        boolean containsMagic = source.getMsgId().contains("magic");
        System.out.println("[MagicCritEvent] Does msgId contain 'magic'? " + containsMagic + " (msgId: " + source.getMsgId() + ")");
        if (!containsMagic) return;

        double totalChance = PlayerSkillsProvider.get((ServerPlayer) source.getEntity()).getCachedBonus(MagicCritChanceBonus.class);
        System.out.println("[MagicCritEvent] Cached crit chance: " + totalChance);
        if (totalChance <= 0) return;

        boolean isCrit = RANDOM.nextDouble() < totalChance;
        System.out.println("[MagicCritEvent] Is crit? " + isCrit + " (random: " + RANDOM.nextDouble() + " < " + totalChance + ")");
        if (!isCrit) return;

        double totalCritDamage = PlayerSkillsProvider.get((ServerPlayer) source.getEntity()).getCachedBonus(MagicCritDamageBonus.class);
        System.out.println("[MagicCritEvent] Cached crit damage bonus: " + totalCritDamage);

        float multiplier = BASE_MAGIC_CRIT_MULTIPLIER + (float) totalCritDamage;
        System.out.println("[MagicCritEvent] Crit multiplier: " + multiplier);

        float originalDamage = event.getAmount();
        float newDamage = originalDamage * multiplier;
        event.setAmount(newDamage);

        System.out.println("[MagicCritEvent] Original Damage: " + originalDamage);
        System.out.println("[MagicCritEvent] New Damage (after crit): " + newDamage);
    }
}