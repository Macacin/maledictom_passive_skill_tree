package daripher.skilltree.skill.bonus.event.agility;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.player.agility.JumpHeightBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class JumpHeightEvent {
    private static final double BLOCKS_PER_REDUCTION = 0.05;

    public static <T extends SkillBonus<T>> List<T> getSkillBonuses(Player player, Class<T> bonusClass) {
        return PlayerSkillsProvider.get(player).getPlayerSkills().stream()
                .flatMap(skill -> skill.getBonuses().stream())
                .filter(bonusClass::isInstance)
                .map(bonusClass::cast)
                .collect(Collectors.toList());
    }

    public static float getJumpHeightMultiplier(Player player) {
        float multiplier = 1f;
        List<JumpHeightBonus> bonuses = getSkillBonuses(player, JumpHeightBonus.class);
        for (JumpHeightBonus bonus : bonuses) {
            multiplier += bonus.getJumpHeightMultiplier(player);
        }
        return multiplier;
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        float totalMultiplier = getJumpHeightMultiplier(player);
        double extraHeight = approximateDeltaHeight(totalMultiplier - 1);

        double reduction = extraHeight * BLOCKS_PER_REDUCTION;
        reduction = Math.min(reduction, 0.6);

        event.setDamageMultiplier((float) (1.0 - reduction));
    }

    private static double approximateDeltaHeight(float extraAmount) {
        return 2.4 * extraAmount;
    }
}