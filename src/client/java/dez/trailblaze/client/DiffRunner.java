package dez.trailblaze.client;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DiffRunner {
    private static final ExecutorService DIFF_EXECUTOR = Executors.newCachedThreadPool();

    public static void runDiff(int radius, FabricClientCommandSource source) {
        Minecraft client = Minecraft.getInstance();
        IntegratedServer server = client.getSingleplayerServer();
        if (server == null) {
            source.sendFeedback(Component.literal("[Trailblaze] Not singleplayer, skipping"));
            return;
        }
        ClientLevel clientLevel = client.level;
        LocalPlayer player = client.player;
        if (clientLevel == null || player == null) {
            source.sendFeedback(Component.literal("[Trailblaze] No level or player found"));
            return;
        }
        ServerLevel serverLevel = server.getLevel(clientLevel.dimension());
        if (serverLevel == null) {
            source.sendFeedback(Component.literal("[Trailblaze] No matching server level found"));
            return;
        }

        ChunkPos centerPos = ChunkPos.containing(player.blockPosition());

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                ChunkPos targetPos = new ChunkPos(centerPos.x() + dx, centerPos.z() + dz);
                LevelChunk realChunk = clientLevel.getChunk(targetPos.x(), targetPos.z());

                CompletableFuture.supplyAsync(() ->
                                ReferenceChunkGenerator.generateAndFillReference(serverLevel, targetPos).join(),
                        DIFF_EXECUTOR
                ).thenAccept(referenceChunk -> {
                    if (referenceChunk == null) {
                        source.sendFeedback(Component.literal(
                                "[Trailblaze] Chunk " + targetPos + ": could not generate reference"));
                        return;
                    }
                    ChunkDiffer.DiffResult result = ChunkDiffer.diff(realChunk, referenceChunk, serverLevel);
                    DiffResultPresenter.present(source, result);
                }).exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
            }
        }
    }
}