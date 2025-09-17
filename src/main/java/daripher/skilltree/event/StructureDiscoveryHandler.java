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
        if (event.phase == TickEvent.Phase.END && event.side.isServer() && event.player instanceof ServerPlayer player) {  // Server end tick
            if (player.tickCount % 20 != 0) return;  // Every 1s
            ServerLevel serverLevel = (ServerLevel) event.player.level();
            BlockPos playerPos = player.blockPosition();  // Player pos, safe
            System.out.println("[DEBUG Discovery] Player tick check at " + playerPos);  // Debug

            for (String typeStr : getAllStructureTypes()) {
                ResourceLocation type = new ResourceLocation(typeStr);
                Structure structure = serverLevel.registryAccess().registryOrThrow(Registries.STRUCTURE).get(type);
                System.out.println("[DEBUG Discovery] Type=" + typeStr + ", structure=" + (structure != null ? "OK" : "NULL"));  // Debug

                if (structure == null) continue;

                // Get the Optional<StructureStart> for the structure at player position
                StructureManager structureManager = serverLevel.structureManager();
                Optional<StructureStart> structureStartOpt = Optional.of(structureManager.getStructureAt(playerPos, structure));
                System.out.println("[DEBUG Discovery] Has " + typeStr + " near player? " + structureStartOpt.isPresent());  // Debug: check presence first

                if (structureStartOpt.isPresent()) {
                    StructureStart start = structureStartOpt.get();  // Safe: isPresent() true
                    if (start.isValid()) {  // Additional check on StructureStart validity (bbox not empty)
                        // Create unique key based on structure type and its bounding box (minX, minZ for simplicity; assumes no overlaps)
                        // For more robustness, you could use full bbox: minX_minY_minZ_maxX_maxY_maxZ
                        String instanceKey = typeStr + "_" + start.getBoundingBox().minX() + "_" + start.getBoundingBox().minZ();
                        DiscoveredStructures discovered = DiscoveredStructuresProvider.get(player);
                        boolean isNew = !discovered.isDiscovered(instanceKey);
                        System.out.println("[DEBUG Discovery] InstanceKey=" + instanceKey + ", isNew=" + isNew);  // Debug

                        if (isNew) {
                            String tier = getTierForType(typeStr);
                            int B = Config.getTierB(tier);
                            int L = PlayerSkillsProvider.get(player).getCurrentLevel();
                            double xp = B * (1 + Math.pow(2.5, L - 1) / 119);
                            int amount = (int) xp;
                            System.out.println("[DEBUG Discovery] Tier=" + tier + ", B=" + B + ", L=" + L + ", XP=" + amount);  // Debug

                            if (amount > 0) {
                                PlayerSkillsProvider.get(player).addSkillExperience(amount);
                                discovered.addDiscoveredChunk(instanceKey);  // Reuse method name, but now it's per-instance
                                System.out.println("[DEBUG Discovery] Gave " + amount + " XP for new " + typeStr + " instance to " + player.getName().getString());
                                NetworkDispatcher.network_channel.send(PacketDistributor.PLAYER.with(() -> player), new SyncPlayerSkillsMessage(player));
                            } else {
                                System.out.println("[DEBUG Discovery] Amount=0 — skip");
                            }
                        }
                    } else {
                        System.out.println("[DEBUG Discovery] StructureStart invalid (empty bbox) for " + typeStr);  // Debug
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