package dez.trailblaze.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;

public class TrailblazeClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
			System.out.println("Chunk loaded: " + chunk.getPos().x() + ", " + chunk.getPos().z());
		});
	}
}