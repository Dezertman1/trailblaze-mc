package dez.trailblaze.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

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

			ReferenceChunkGenerator.generateReference(serverLevel, chunk.getPos());
		});
	}
}