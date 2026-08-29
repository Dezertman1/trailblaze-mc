package dez.trailblaze.client;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;

public class TrailblazeCommands {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext) -> {
            dispatcher.register(ClientCommands.literal("trailblaze")
                    .then(ClientCommands.literal("diff")
                            .executes(context -> {
                                DiffRunner.runDiff(0, context.getSource());
                                return 1;
                            })
                            .then(ClientCommands.argument("radius", IntegerArgumentType.integer(0, 5))
                                    .executes(context -> {
                                        int radius = IntegerArgumentType.getInteger(context, "radius");
                                        DiffRunner.runDiff(radius, context.getSource());
                                        return 1;
                                    })
                            )
                    )
            );
        });
    }
}