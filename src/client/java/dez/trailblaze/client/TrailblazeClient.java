package dez.trailblaze.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.level.ServerLevel;

public class TrailblazeClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		TrailblazeCommands.register();
		ClientChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
			Minecraft client = Minecraft.getInstance();
			IntegratedServer server = client.getSingleplayerServer();
			if (server == null) return; // Not singleplayer
			ServerLevel serverLevel = server.getLevel(world.dimension());
			if (serverLevel == null) return;
			ReferenceChunkGenerator.generateAndFillReference(serverLevel, chunk.getPos());
		});
	}
}