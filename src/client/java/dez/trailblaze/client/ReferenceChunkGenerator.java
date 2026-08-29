package dez.trailblaze.client;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.blending.Blender;
import java.util.concurrent.CompletableFuture;

public class ReferenceChunkGenerator {
    public static CompletableFuture<ChunkAccess> generateAndFillReference(ServerLevel serverLevel, ChunkPos chunkPos) {
        ProtoChunk protoChunk = new ProtoChunk(
                chunkPos, UpgradeData.EMPTY, serverLevel,
                PalettedContainerFactory.create(serverLevel.registryAccess()), null
        );

        System.out.println("Created empty ProtoChunk at " + protoChunk.getPos());
        System.out.println("serverLevel: " + serverLevel + " seed: " + serverLevel.getSeed());

        ChunkGenerator baseGenerator = serverLevel.getChunkSource().getGenerator();
        if (!(baseGenerator instanceof NoiseBasedChunkGenerator generator)) {
            return CompletableFuture.completedFuture(null);
        }

        return generator.fillFromNoise(
                Blender.empty(), serverLevel.getChunkSource().randomState(),
                serverLevel.structureManager(), protoChunk
        ).thenApply(resultChunk -> {
            WorldGenerationContext context = new WorldGenerationContext(generator, serverLevel);
            generator.buildSurface(
                    resultChunk, context, serverLevel.getChunkSource().randomState(),
                    serverLevel.structureManager(), serverLevel.getBiomeManager(), Blender.empty(), null
            );

            System.out.println("Surfaced chunk at " + resultChunk.getPos());
            for (int y = 60; y <= 70; y++) {
                BlockPos blockPos = new BlockPos(0, y, 0);  // Note this uses world coords, not chunk
                System.out.println("y=" + y + ": " + resultChunk.getBlockState(blockPos));
            }

            return resultChunk;
        });
    }
}