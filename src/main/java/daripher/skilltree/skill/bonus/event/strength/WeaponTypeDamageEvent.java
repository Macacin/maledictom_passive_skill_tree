package daripher.skilltree.skill.bonus.event.strength;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.skill.bonus.player.strength.*;
import daripher.skilltree.data.reloader.WeaponTypesReloader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class WeaponTypeDamageEvent {

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof ServerPlayer player)) return;
        if (!source.is(DamageTypes.PLAYER_ATTACK)) return;

        ItemStack weapon = player.getMainHandItem();
        if (weapon.isEmpty()) {
            return;
        }

        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(weapon.getItem());
        if (itemId == null) {
            return;
        }

        double totalBonus = 0;
        String type = null;

        if (WeaponTypesReloader.getWeaponsForType("sword").contains(itemId)) {
            type = "sword";
            totalBonus = PlayerSkillsProvider.get(player).getCachedBonus(SwordDamageBonus.class);
        } else if (WeaponTypesReloader.getWeaponsForType("axe").contains(itemId)) {
            type = "axe";
            totalBonus = PlayerSkillsProvider.get(player).getCachedBonus(AxeDamageBonus.class);
        } else if (WeaponTypesReloader.getWeaponsForType("hammer").contains(itemId)) {
            type = "hammer";
            totalBonus = PlayerSkillsProvider.get(player).getCachedBonus(HammerDamageBonus.class);
        } else if (WeaponTypesReloader.getWeaponsForType("trident").contains(itemId)) {
            type = "trident";
            totalBonus = PlayerSkillsProvider.get(player).getCachedBonus(TridentDamageBonus.class);
        } else if (WeaponTypesReloader.getWeaponsForType("dagger").contains(itemId)) {
            type = "dagger";
            totalBonus = PlayerSkillsProvider.get(player).getCachedBonus(DaggerDamageBonus.class);
        } else if (WeaponTypesReloader.getWeaponsForType("scythe").contains(itemId)) {
            type = "scythe";
            totalBonus = PlayerSkillsProvider.get(player).getCachedBonus(ScytheDamageBonus.class);
        } else if (WeaponTypesReloader.getWeaponsForType("chakram").contains(itemId)) {
            type = "chakram";
            totalBonus = PlayerSkillsProvider.get(player).getCachedBonus(ChakramDamageBonus.class);
        }

        if (type == null) {
            return;
        }

        if (totalBonus <= 0) {
            return;
        }

        float originalDamage = event.getAmount();
        event.setAmount((float) (originalDamage * (1 + totalBonus)));
    }
}