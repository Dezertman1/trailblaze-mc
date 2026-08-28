package dez.trailblaze.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.PalettedContainerFactory;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
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

			ChunkGenerator generator = serverLevel.getChunkSource().getGenerator();
			generator.fillFromNoise(
					Blender.empty(),
					serverLevel.getChunkSource().randomState(),
					serverLevel.structureManager(),
					protoChunk
			).thenAccept(resultChunk -> {
				System.out.println("Filled chunk at " + resultChunk.getPos());

				// Sample a column of the chunk
				for (int y = 60; y <= 70; y++) {
					BlockPos pos = new BlockPos(0, y, 0); // Note this uses world coords, not chunk
					BlockState state = resultChunk.getBlockState(pos);
					System.out.println("y=" + y + ": " + state);
				}
			});

			System.out.println("Created empty ProtoChunk at " + protoChunk.getPos());
			System.out.println("serverLevel: " + serverLevel + " seed: " + serverLevel.getSeed());
		});




	}
}