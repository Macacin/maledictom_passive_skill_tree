package daripher.skilltree.skill.bonus.event.endurance;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.endurance.RollRechargeBonus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class RollRechargeEvent {
    private static final UUID ROLL_RECHARGE_UUID = UUID.fromString("b4567890-cdef-0123-4567-890abcdef123");
    private static Attribute ROLL_RECHARGE_ATTR = null;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side.isClient()) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        if (!player.isAlive() || player.isDeadOrDying()) return;

        double bonus = PlayerSkillsProvider.get(player).getCachedBonus(RollRechargeBonus.class);

        if (bonus == 0) return;

        if (ROLL_RECHARGE_ATTR == null) {
            ROLL_RECHARGE_ATTR = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("combatroll", "recharge"));
        }

        assert ROLL_RECHARGE_ATTR != null;
        AttributeInstance attr = player.getAttribute(ROLL_RECHARGE_ATTR);
        if (attr == null) {
            return;
        }

        attr.removeModifier(ROLL_RECHARGE_UUID);

        attr.addTransientModifier(new AttributeModifier(
                ROLL_RECHARGE_UUID,
                "Endurance Roll Recharge Bonus",
                bonus,
                AttributeModifier.Operation.MULTIPLY_BASE
        ));
    }
}