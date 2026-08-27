package dez.trailblaze.client;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.PalettedContainerFactory;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;

public class TrailblazeClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
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

			System.out.println("Created empty ProtoChunk at " + protoChunk.getPos());
			System.out.println("serverLevel: " + serverLevel + "seed: " + serverLevel.getSeed());
			// System.out.println("Chunk loaded: " + chunk.getPos().x() + ", " + chunk.getPos().z());
		});




	}
}