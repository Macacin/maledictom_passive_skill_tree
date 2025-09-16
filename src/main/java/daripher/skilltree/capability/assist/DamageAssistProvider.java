package daripher.skilltree.capability.assist;

import daripher.skilltree.SkillTreeMod;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class DamageAssistProvider implements ICapabilitySerializable<CompoundTag> {
    private static final ResourceLocation ID = new ResourceLocation(SkillTreeMod.MOD_ID, "damage_assist_tracker");
    private static final Capability<DamageAssistTracker> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    private final LazyOptional<DamageAssistTracker> instance = LazyOptional.of(DamageAssistTrackerImpl::new);

    @SubscribeEvent
    public static void attach(AttachCapabilitiesEvent<Entity> event) {
        Entity obj = event.getObject();
        if (!(obj instanceof LivingEntity) || obj instanceof Player) return;
        event.addCapability(ID, new DamageAssistProvider());
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return cap == CAPABILITY ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return instance.orElseThrow(NullPointerException::new).serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        instance.orElseThrow(NullPointerException::new).deserializeNBT(tag);
    }

    public static DamageAssistTracker get(LivingEntity entity) {
        if (entity instanceof Player) return new DamageAssistTrackerImpl();  // Fallback for players (though not attached)
        return entity.getCapability(CAPABILITY).orElseGet(DamageAssistTrackerImpl::new);
    }
}