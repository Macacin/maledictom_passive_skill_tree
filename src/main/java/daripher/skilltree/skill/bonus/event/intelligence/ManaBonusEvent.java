package daripher.skilltree.skill.bonus.event.intelligence;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.intelligence.ManaBonus;
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
public class ManaBonusEvent {
    private static final UUID MANA_UUID = UUID.fromString("d456389a-e123-f012-3456-789a04bcdef0");

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side.isClient()) return;

        boolean modLoaded = ModList.get().isLoaded("irons_spellbooks");
        if (!modLoaded) return;

        if (!(event.player instanceof ServerPlayer player)) return;
        if (!player.isAlive()) return;

        AttributeInstance attr = player.getAttribute(AttributeRegistry.MAX_MANA.get());
        if (attr == null) return;

        attr.removeModifier(MANA_UUID);

        double bonus = PlayerSkillsProvider.get(player).getCachedBonus(ManaBonus.class);
        if (bonus == 0) return;

        attr.addTransientModifier(new AttributeModifier(
                MANA_UUID,
                "Intelligence Mana Bonus",
                bonus,
                AttributeModifier.Operation.ADDITION
        ));
    }
}