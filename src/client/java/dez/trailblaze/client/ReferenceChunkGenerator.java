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
        return generateAndFillReference(serverLevel, chunkPos, DiffConfig.INSTANCE.includeFeatures);
    }

    public static CompletableFuture<ChunkAccess> generateAndFillReference(ServerLevel serverLevel, ChunkPos chunkPos, boolean includeFeatures) {
        ProtoChunk protoChunk = new ProtoChunk(
                chunkPos, UpgradeData.EMPTY, serverLevel,
                PalettedContainerFactory.create(serverLevel.registryAccess()), null
        );
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
            if (includeFeatures) {
                ReferenceWorldGenLevel sandboxLevel = new ReferenceWorldGenLevel(serverLevel, (ProtoChunk) resultChunk);
                generator.applyBiomeDecoration(sandboxLevel, resultChunk, serverLevel.structureManager());
            }
            return resultChunk;
        });
    }
}