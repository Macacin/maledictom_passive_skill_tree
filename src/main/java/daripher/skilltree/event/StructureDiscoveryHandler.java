package daripher.skilltree.event;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.capability.discovery.DiscoveredStructures;
import daripher.skilltree.capability.discovery.DiscoveredStructuresProvider;
import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.config.Config;
import daripher.skilltree.network.NetworkDispatcher;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(modid = SkillTreeMod.MOD_ID)
public class StructureDiscoveryHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.side.isServer() && event.player instanceof ServerPlayer player) {
            if (player.tickCount % 60 != 0) return;
            ServerLevel serverLevel = (ServerLevel) event.player.level();
            BlockPos playerPos = player.blockPosition();

            for (String typeStr : getAllStructureTypes()) {
                ResourceLocation type = new ResourceLocation(typeStr);
                Structure structure = serverLevel.registryAccess().registryOrThrow(Registries.STRUCTURE).get(type);

                if (structure == null) continue;

                StructureManager structureManager = serverLevel.structureManager();
                Optional<StructureStart> structureStartOpt = Optional.of(structureManager.getStructureAt(playerPos, structure));

                if (structureStartOpt.isPresent()) {
                    StructureStart start = structureStartOpt.get();
                    if (start.isValid()) {
                        String instanceKey = typeStr + "_" + start.getBoundingBox().minX() + "_" + start.getBoundingBox().minZ();
                        DiscoveredStructures discovered = DiscoveredStructuresProvider.get(player);
                        boolean isNew = !discovered.isDiscovered(instanceKey);

                        if (isNew) {
                            String tier = getTierForType(typeStr);
                            double B = Config.getTierB(tier);
                            int L = PlayerSkillsProvider.get(player).getCurrentLevel();
                            double xp = B * (1 + Math.pow(2.5, L - 1) / 119);

                            if (xp > 0) {
                                PlayerSkillsProvider.get(player).addSkillExperience(xp);
                                discovered.addDiscoveredChunk(instanceKey);
                                NetworkDispatcher.network_channel.send(PacketDistributor.PLAYER.with(() -> player), new SyncPlayerSkillsMessage(player));
                            }
                        }
                    } else {
                    }
                }
            }
        }
    }

    private static List<String> getAllStructureTypes() {
        return Stream.of(
                Config.getCommonStructures(),
                Config.getUncommonStructures(),
                Config.getRareStructures(),
                Config.getEpicStructures(),
                Config.getLegendaryStructures()
        ).flatMap(List::stream).collect(Collectors.toList());
    }

    private static String getTierForType(String typeStr) {
        if (Config.getCommonStructures().contains(typeStr)) return "common";
        if (Config.getUncommonStructures().contains(typeStr)) return "uncommon";
        if (Config.getRareStructures().contains(typeStr)) return "rare";
        if (Config.getEpicStructures().contains(typeStr)) return "epic";
        if (Config.getLegendaryStructures().contains(typeStr)) return "legendary";
        return "common";
    }
}