package daripher.skilltree.skill.bonus.event.wisdom;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.wisdom.MagicWeaponDamageBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class MagicWeaponDamageEvent {
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (!ModList.get().isLoaded("irons_spellbooks")) return;
        DamageSource source = event.getSource();
        if (source.getEntity() == null || !(source.getEntity() instanceof ServerPlayer player)) return;
        if (!source.is(DamageTypes.PLAYER_ATTACK)) return;

        var item = player.getMainHandItem();
        var tag = item.getTag();
        if (tag == null || !tag.contains("ISB_Spells")) return;

        double bonus = PlayerSkillsProvider.get(player).getCachedBonus(MagicWeaponDamageBonus.class);
        if (bonus == 0) return;

        float originalDamage = event.getAmount();
        float newDamage = (float) (originalDamage * (1 + bonus));
        event.setAmount(newDamage);
    }
}