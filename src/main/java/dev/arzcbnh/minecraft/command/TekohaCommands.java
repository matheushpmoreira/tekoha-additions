package dev.arzcbnh.minecraft.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import dev.arzcbnh.minecraft.TekohaAdditions;
import dev.arzcbnh.minecraft.auth.AuthService;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.server.commands.DialogCommand;

public class TekohaCommands {
    public void init(AuthService service) {
//        CommandRegistrationCallback.EVENT.register((dispatcher, build, selection) -> {
//            dispatcher.register(
//                    Commands.literal("tekoha")
//                            .then(Commands.literal("login")
//                                    .then(Commands.argument("password", StringArgumentType.greedyString())
//                                            .executes(context -> )))
//                            .then(Commands.literal("signup"))
//            );
//        });
    }
}
