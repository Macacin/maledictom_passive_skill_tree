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

        boolean modLoaded = ModList.get().isLoaded("irons_spellbooks");
        if (!modLoaded) return;

        DamageSource source = event.getSource();

        if (source.getEntity() == null) return;
        boolean isPlayer = source.getEntity() instanceof ServerPlayer;
        if (!isPlayer) return;

        boolean containsMagic = source.getMsgId().contains("magic");
        if (!containsMagic) return;

        double totalChance = PlayerSkillsProvider.get((ServerPlayer) source.getEntity()).getCachedBonus(MagicCritChanceBonus.class);
        if (totalChance <= 0) return;

        boolean isCrit = RANDOM.nextDouble() < totalChance;
        if (!isCrit) return;

        double totalCritDamage = PlayerSkillsProvider.get((ServerPlayer) source.getEntity()).getCachedBonus(MagicCritDamageBonus.class);

        float multiplier = BASE_MAGIC_CRIT_MULTIPLIER + (float) totalCritDamage;

        float originalDamage = event.getAmount();
        float newDamage = originalDamage * multiplier;
        event.setAmount(newDamage);
    }
}