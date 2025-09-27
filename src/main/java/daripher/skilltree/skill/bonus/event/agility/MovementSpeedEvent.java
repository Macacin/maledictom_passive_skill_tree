package daripher.skilltree.skill.bonus.event.agility;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.agility.LightLoadMovementBonus;
import daripher.skilltree.skill.bonus.player.agility.MovementSpeedBonus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class MovementSpeedEvent {
    private static final UUID LIGHT_LOAD_ROLL_UUID = UUID.fromString("d3456777-e01f-f012-3456-777abcdef123");
    private static Attribute ROLL_DISTANCE_ATTR = null;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side.isClient()) return;
        ServerPlayer player = (ServerPlayer) event.player;

        double cachedMovementBonus = PlayerSkillsProvider.get(player).getCachedBonus(MovementSpeedBonus.class);

        double lightLoadBonus = 0;
        if (PlayerSkillsProvider.get(player).getPlayerSkills().stream()
                .flatMap(skill -> skill.getBonuses().stream())
                .anyMatch(bonus -> bonus instanceof LightLoadMovementBonus)) {
            lightLoadBonus = PlayerSkillsProvider.get(player).getPlayerSkills().stream()
                    .flatMap(skill -> skill.getBonuses().stream())
                    .filter(bonus -> bonus instanceof LightLoadMovementBonus)
                    .mapToDouble(bonus -> ((LightLoadMovementBonus) bonus).getSpeedBonus(player))
                    .sum();
        }

        double totalBonus = cachedMovementBonus + lightLoadBonus;
        if (totalBonus == 0) return;

        double originalBase = 0.1D;
        double newValue = originalBase * (1 + totalBonus);
        player.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(newValue);

        // Добавление бонуса к roll distance при light load
        if (ModList.get().isLoaded("combatroll")) {
            if (ROLL_DISTANCE_ATTR == null) {
                ROLL_DISTANCE_ATTR = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("combatroll", "distance"));
            }
            if (ROLL_DISTANCE_ATTR != null) {
                AttributeInstance rollAttr = player.getAttribute(ROLL_DISTANCE_ATTR);
                if (rollAttr != null) {
                    rollAttr.removeModifier(LIGHT_LOAD_ROLL_UUID);
                    if (lightLoadBonus > 0) { // Поскольку lightLoadBonus уже 0 если не light load
                        rollAttr.addTransientModifier(new AttributeModifier(
                                LIGHT_LOAD_ROLL_UUID,
                                "Light Load Roll Bonus",
                                lightLoadBonus,
                                AttributeModifier.Operation.MULTIPLY_BASE
                        ));
                    }
                }
            }
        }
    }
}