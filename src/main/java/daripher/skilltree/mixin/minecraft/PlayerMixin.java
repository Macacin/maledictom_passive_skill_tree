package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.entity.player.PlayerExtension;
//import daripher.skilltree.skill.bonus.SkillBonusHandler;
import daripher.skilltree.skill.bonus.player.endurance.HungerReductionBonus;
import daripher.skilltree.skill.bonus.player.endurance.MiningSpeedBonus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements PlayerExtension {
    @Shadow
    public abstract FoodData getFoodData();
    private int rainbowJewelInsertionSeed;

    @SuppressWarnings("DataFlowIssue")
    protected PlayerMixin() {
        super(null, null);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readRainbowJewelInsertionSeed(CompoundTag tag, CallbackInfo callbackInfo) {
        rainbowJewelInsertionSeed = tag.getInt("RainbowJewelInsertionSeed");
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void writeRainbowJewelInsertionSeed(CompoundTag tag, CallbackInfo callbackInfo) {
        tag.putInt("RainbowJewelInsertionSeed", rainbowJewelInsertionSeed);
    }

    @SuppressWarnings("DataFlowIssue")
//    @Inject(method = "onEnchantmentPerformed", at = @At("HEAD"))
//    private void restoreEnchantmentExperience(
//            ItemStack itemStack, int enchantmentCost, CallbackInfo callbackInfo) {
//        Player player = (Player) (Object) this;
//        float freeEnchantmentChance = SkillBonusHandler.getFreeEnchantmentChance(player);
//        if (player.getRandom().nextFloat() < freeEnchantmentChance) {
//            player.giveExperienceLevels(enchantmentCost);
//        }
//    }

    @Override
    public int getGemsRandomSeed() {
        return rainbowJewelInsertionSeed;
    }

    @Override
    public void updateGemsRandomSeed() {
        rainbowJewelInsertionSeed = random.nextInt();
    }

    @ModifyArg(method = "causeFoodExhaustion(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V"))
    private float modifyExhaustion(float exhaustion) {
        Player player = (Player) (Object) this;
        if (player.level().isClientSide) return exhaustion;
        double bonus = PlayerSkillsProvider.get(player).getCachedBonus(HungerReductionBonus.class);
        if (bonus > 0) {
            exhaustion *= (1 - (float) bonus);
        }
        return exhaustion;
    }
}
