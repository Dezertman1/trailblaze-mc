package dez.trailblaze.client;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

public class DiffResultPresenter {
    public static void present(FabricClientCommandSource source, ChunkDiffer.DiffResult result) {
        source.sendFeedback(Component.literal(
                "[Trailblaze] Chunk " + result.chunkPos() + ": " + result.mismatchCount() + " mismatches"
        ));
    }
}