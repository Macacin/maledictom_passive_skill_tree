package daripher.skilltree.network.message;

import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.client.screen.SkillTreeScreen;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.skill.PassiveSkill;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

public class SyncPlayerSkillsMessage {
    private List<ResourceLocation> learnedSkills = new ArrayList<>();
    private int skillPoints;
    private double skillExperience;
    private int currentLevel;
    private long lastCraftingXPTime;
    private int consecutiveCraftingActions;
    private long lastMiningXPTime;
    private int consecutiveMiningActions;


    private SyncPlayerSkillsMessage() {
    }

    public SyncPlayerSkillsMessage(Player player) {
        IPlayerSkills skillsCapability = PlayerSkillsProvider.get(player);
        if (skillsCapability != null) {  // Guard на capability attach
            learnedSkills = skillsCapability.getPlayerSkills().stream().map(PassiveSkill::getId).toList();
            skillPoints = skillsCapability.getSkillPoints();
            skillExperience = skillsCapability.getSkillExperience();
            currentLevel = skillsCapability.getCurrentLevel();
            lastCraftingXPTime = skillsCapability.getLastCraftingXPTime();
            consecutiveCraftingActions = skillsCapability.getConsecutiveCraftingActions();
            lastMiningXPTime = skillsCapability.getLastMiningXPTime();
            consecutiveMiningActions = skillsCapability.getConsecutiveMiningActions();
        }
    }

    public static SyncPlayerSkillsMessage decode(FriendlyByteBuf buf) {
        SyncPlayerSkillsMessage result = new SyncPlayerSkillsMessage();
        int learnedSkillsCount = buf.readInt();
        for (int i = 0; i < learnedSkillsCount; i++) {
            result.learnedSkills.add(new ResourceLocation(buf.readUtf()));
        }
        result.skillPoints = buf.readInt();
        result.skillExperience = buf.readDouble();
        result.currentLevel = buf.readInt();
        result.lastCraftingXPTime = buf.readLong();
        result.consecutiveCraftingActions = buf.readInt();
        result.lastMiningXPTime = buf.readLong();
        result.consecutiveMiningActions = buf.readInt();
        return result;
    }

    public static void receive(SyncPlayerSkillsMessage message, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        if (ctx.getDirection().getReceptionSide() != LogicalSide.CLIENT) {
            return;
        }
        ctx.setPacketHandled(true);
        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handlePacket(message, ctx)));
    }

    @OnlyIn(value = Dist.CLIENT)
    private static void handlePacket(SyncPlayerSkillsMessage message, NetworkEvent.Context ctx) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        IPlayerSkills capability = PlayerSkillsProvider.get(minecraft.player);
        if (capability == null) {
            return;
        }

        ctx.setPacketHandled(true);

        capability.getPlayerSkills().clear();
        message.learnedSkills.stream()
                .map(SkillsReloader::getSkillById)
                .filter(Objects::nonNull)
                .forEach(capability.getPlayerSkills()::add);
        capability.setSkillPoints(message.skillPoints);
        capability.setSkillExperience(message.skillExperience);
        capability.setCurrentLevel(message.currentLevel);

        capability.setLastCraftingXPTime(message.lastCraftingXPTime);
        capability.setConsecutiveCraftingActions(message.consecutiveCraftingActions);
        capability.setLastMiningXPTime(message.lastMiningXPTime);
        capability.setConsecutiveMiningActions(message.consecutiveMiningActions);


        if (minecraft.screen instanceof SkillTreeScreen screen) {
            screen.skillPoints = capability.getSkillPoints() - screen.newlyLearnedSkills.size();
            screen.updateProgressDisplay();
            screen.init();
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(learnedSkills.size());
        learnedSkills.stream().map(ResourceLocation::toString).forEach(buf::writeUtf);
        buf.writeInt(skillPoints);
        buf.writeDouble(skillExperience);
        buf.writeInt(currentLevel);
        buf.writeLong(lastCraftingXPTime);
        buf.writeInt(consecutiveCraftingActions);
        buf.writeLong(lastMiningXPTime);
        buf.writeInt(consecutiveMiningActions);
    }
}
