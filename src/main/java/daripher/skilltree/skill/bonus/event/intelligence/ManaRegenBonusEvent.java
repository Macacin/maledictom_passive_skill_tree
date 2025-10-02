package daripher.skilltree.skill.bonus.event.intelligence;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.intelligence.ManaRegenBonus;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class ManaRegenBonusEvent {
    private static final UUID MANA_REGEN_UUID = UUID.fromString("e96789ab-f234-01ef-4567-89ab05cdef03");

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side.isClient()) return;

        boolean modLoaded = ModList.get().isLoaded("irons_spellbooks");
        if (!modLoaded) return;

        if (!(event.player instanceof ServerPlayer player)) return;
        if (!player.isAlive()) return;

        AttributeInstance attr = player.getAttribute(AttributeRegistry.MANA_REGEN.get());
        if (attr == null) return;

        attr.removeModifier(MANA_REGEN_UUID);

        double bonus = PlayerSkillsProvider.get(player).getCachedBonus(ManaRegenBonus.class);
        if (bonus == 0) return;

        attr.addTransientModifier(new AttributeModifier(
                MANA_REGEN_UUID,
                "Intelligence Mana Regen Bonus",
                bonus,
                AttributeModifier.Operation.ADDITION
        ));
    }
}