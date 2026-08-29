package dez.trailblaze.client;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.ArrayList;
import java.util.List;

public class ChunkDiffer {
    public record  DiffResult(ChunkPos chunkPos, int mismatchCount, List<BlockPos> mismatches) {}

    public static DiffResult diff(LevelChunk realChunk, ChunkAccess referenceChunk, ServerLevel level) {
        ChunkPos pos = realChunk.getPos();
        List<BlockPos> mismatches = new ArrayList<>();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = level.getMinY(); y < level.getMaxY(); y++) {
                    BlockPos blockPos = new BlockPos(pos.getMinBlockX() + x, y, pos.getMinBlockZ() + z);
                    BlockState realState = realChunk.getBlockState(blockPos);
                    BlockState referenceState = referenceChunk.getBlockState(blockPos);

                    if (!realState.equals(referenceState)) {
                        mismatches.add(blockPos);
                    }
                }
            }
        }

        return new DiffResult(pos, mismatches.size(), mismatches);
    }
}
