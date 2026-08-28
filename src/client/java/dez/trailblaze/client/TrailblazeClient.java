package dez.trailblaze.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
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

public class TrailblazeClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
			if (!chunk.getPos().equals(new ChunkPos(0, 0))) return; // Only run for 0,0

			Minecraft client = Minecraft.getInstance();
			IntegratedServer server = client.getSingleplayerServer();
			if (server == null) return; // Not singleplayer
			ServerLevel serverLevel = server.getLevel(world.dimension());
			if (serverLevel == null) return;

			ProtoChunk protoChunk = new ProtoChunk(
					chunk.getPos(),
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
					BlockPos pos = new BlockPos(0, y, 0);  // Note this uses world coords, not chunk
					System.out.println("y=" + y + ": " + resultChunk.getBlockState(pos));
				}
			});

			System.out.println("Created empty ProtoChunk at " + protoChunk.getPos());
			System.out.println("serverLevel: " + serverLevel + " seed: " + serverLevel.getSeed());
		});




	}
}