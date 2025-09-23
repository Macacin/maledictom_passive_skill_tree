package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.agility.ProjectileVelocityBonus;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public class AbstractArrowMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void applyVelocityBonus(CallbackInfo info) {
        AbstractArrow arrow = (AbstractArrow) (Object) this;
        if (!(arrow.getOwner() instanceof Player player)) return;
        if (arrow.tickCount > 1) return; // Apply only on first tick

        float totalBonus = (float) PlayerSkillsProvider.get(player).getPlayerSkills().stream()
                .flatMap(skill -> skill.getBonuses().stream())
                .filter(bonus -> bonus instanceof ProjectileVelocityBonus)
                .mapToDouble(bonus -> ((ProjectileVelocityBonus) bonus).getVelocityBonus(player))
                .sum();

        arrow.setDeltaMovement(arrow.getDeltaMovement().scale(1 + totalBonus));
    }
}