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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

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

        List<ChunkPos> targets = new ArrayList<>();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                targets.add(new ChunkPos(centerPos.x() + dx, centerPos.z() + dz));
            }
        }

        int total = targets.size();
        AtomicInteger completed = new AtomicInteger(0);

        CompletableFuture<Void> chain = CompletableFuture.completedFuture(null);
        for (ChunkPos targetPos : targets) {
            chain = chain.thenCompose(ignored -> {
                LevelChunk realChunk = clientLevel.getChunk(targetPos.x(), targetPos.z());
                return CompletableFuture.supplyAsync(() ->
                                ReferenceChunkGenerator.generateAndFillReference(serverLevel, targetPos).join(),
                        DIFF_EXECUTOR
                ).thenAccept(referenceChunk -> {
                    int done = completed.incrementAndGet();
                    client.execute(() -> client.gui.hud.setOverlayMessage(
                            Component.literal("[Trailblaze] Diffing (" + targetPos.x() + ", " + targetPos.z() + ")... " + done + "/" + total),
                            false
                    ));

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
            });
        }

        chain.thenRun(() -> {
            client.execute(() -> client.gui.hud.setOverlayMessage(
                    Component.literal("[Trailblaze] Diff complete!"), false));
        });
    }
}