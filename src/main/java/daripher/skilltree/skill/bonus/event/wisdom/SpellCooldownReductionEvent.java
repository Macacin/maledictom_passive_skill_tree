package daripher.skilltree.skill.bonus.event.wisdom;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.wisdom.SpellCooldownReductionBonus;
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
public class SpellCooldownReductionEvent {
    private static final UUID SPELL_COOLDOWN_UUID = UUID.fromString("b2345678-c901-def0-1234-567892abcdef");

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        System.out.println("[SpellCooldownReductionEvent] Tick for player: " + event.player);

        if (event.phase != TickEvent.Phase.END || event.side.isClient()) return;

        boolean modLoaded = ModList.get().isLoaded("irons_spellbooks");
        System.out.println("[SpellCooldownReductionEvent] Is irons_spellbooks loaded? " + modLoaded);
        if (!modLoaded) return;

        if (!(event.player instanceof ServerPlayer player)) return;
        if (!player.isAlive()) return;

        AttributeInstance attr = player.getAttribute(AttributeRegistry.COOLDOWN_REDUCTION.get());
        System.out.println("[SpellCooldownReductionEvent] Cooldown Reduction attribute found? " + (attr != null));
        if (attr == null) return;

        attr.removeModifier(SPELL_COOLDOWN_UUID);

        double bonus = PlayerSkillsProvider.get(player).getCachedBonus(SpellCooldownReductionBonus.class);
        System.out.println("[SpellCooldownReductionEvent] Cached bonus: " + bonus);
        if (bonus == 0) return;

        attr.addTransientModifier(new AttributeModifier(
                SPELL_COOLDOWN_UUID,
                "Wisdom Spell Cooldown Reduction",
                bonus,
                AttributeModifier.Operation.ADDITION
        ));
        System.out.println("[SpellCooldownReductionEvent] Modifier applied successfully");
    }
}