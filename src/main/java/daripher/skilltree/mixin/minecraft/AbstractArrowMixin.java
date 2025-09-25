package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.capability.skill.IPlayerSkills;
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
        if (arrow.tickCount > 1) return;

        IPlayerSkills skills = PlayerSkillsProvider.get(player);
        float totalBonus = (float) skills.getCachedBonus(ProjectileVelocityBonus.class);
        if (totalBonus == 0) return;

        arrow.setDeltaMovement(arrow.getDeltaMovement().scale(1 + totalBonus));
    }
}