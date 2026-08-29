package dez.trailblaze.client;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.PalettedContainerFactory;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.blending.Blender;

public class ReferenceChunkGenerator {
    public static void generateReference(ServerLevel serverLevel, ChunkPos chunkPos) {
        ProtoChunk protoChunk = new ProtoChunk(
                chunkPos,
                UpgradeData.EMPTY,
                serverLevel,
                PalettedContainerFactory.create(serverLevel.registryAccess()),
                null
        );

        ChunkGenerator baseGenerator = serverLevel.getChunkSource().getGenerator();

        if (!(baseGenerator instanceof NoiseBasedChunkGenerator generator)) {
            System.out.println("Not NoiseBasedChunkGenerator, skipping surface stage");
            return;
        }

        generator.fillFromNoise(
                Blender.empty(),
                serverLevel.getChunkSource().randomState(),
                serverLevel.structureManager(),
                protoChunk
        ).thenAccept(resultChunk -> {
            System.out.println("Filled chunk at " + resultChunk.getPos());
            WorldGenerationContext context = new WorldGenerationContext(generator, serverLevel);
            generator.buildSurface(
                    resultChunk,
                    context,
                    serverLevel.getChunkSource().randomState(),
                    serverLevel.structureManager(),
                    serverLevel.getBiomeManager(),
                    Blender.empty(),
                    null
            );

            // Sample a column of the chunk
            System.out.println("Surfaced chunk at " + resultChunk.getPos());
            for (int y = 60; y <= 70; y++) {
                BlockPos blockPos = new BlockPos(0, y, 0);  // Note this uses world coords, not chunk
                System.out.println("y=" + y + ": " + resultChunk.getBlockState(blockPos));
            }
        });

        System.out.println("Created empty ProtoChunk at " + protoChunk.getPos());
        System.out.println("serverLevel: " + serverLevel + " seed: " + serverLevel.getSeed());
    }
}