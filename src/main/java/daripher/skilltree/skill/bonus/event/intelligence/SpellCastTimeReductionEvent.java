package daripher.skilltree.skill.bonus.event.intelligence;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.intelligence.SpellCastTimeReductionBonus;
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
public class SpellCastTimeReductionEvent {
    private static final UUID SPELL_CAST_TIME_UUID = UUID.fromString("c3456789-d012-ef01-2345-672903abcdef");

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side.isClient()) return;

        boolean modLoaded = ModList.get().isLoaded("irons_spellbooks");
        if (!modLoaded) return;

        if (!(event.player instanceof ServerPlayer player)) return;
        if (!player.isAlive()) return;

        AttributeInstance attr = player.getAttribute(AttributeRegistry.CAST_TIME_REDUCTION.get());
        if (attr == null) return;

        attr.removeModifier(SPELL_CAST_TIME_UUID);

        double bonus = PlayerSkillsProvider.get(player).getCachedBonus(SpellCastTimeReductionBonus.class);
        if (bonus == 0) return;

        attr.addTransientModifier(new AttributeModifier(
                SPELL_CAST_TIME_UUID,
                "Intelligence Spell Cast Time Reduction",
                bonus,
                AttributeModifier.Operation.ADDITION
        ));
    }
}