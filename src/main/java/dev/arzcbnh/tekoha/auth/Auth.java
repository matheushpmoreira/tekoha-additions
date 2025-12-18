package dev.arzcbnh.tekoha.auth;

import com.mojang.brigadier.arguments.StringArgumentType;
import dev.arzcbnh.tekoha.TekohaAdditions;
import dev.arzcbnh.tekoha.util.ModConfig;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;

public class Auth {
    private final AuthCommands commands;

    public Auth(ModConfig config) {
        this.commands = new AuthCommands(config);
    }

    public Auth() {
        this(TekohaAdditions.CONFIG);
    }

    public void init() {
        ServerLifecycleEvents.SERVER_STARTED.register(
                server -> ServerPlayerEvents.JOIN.register(this.commands::forbidPlayer));

        CommandRegistrationCallback.EVENT.register((dispatcher, build, selection) -> {
            dispatcher.register(Commands.literal("tekoha")
                    .then(Commands.literal("login")
                            .then(Commands.argument("password", StringArgumentType.greedyString())
                                    .requires(CommandSourceStack::isPlayer)
                                    .executes(this.commands::handleLogin)))
                    .then(Commands.literal("signup")
                            .then(Commands.argument("password", StringArgumentType.greedyString())
                                    .requires(CommandSourceStack::isPlayer)
                                    .executes(this.commands::handleSignup)))
                    .then(Commands.literal("auth")
                            .then(Commands.literal("login")
                                    .then(Commands.argument("password", StringArgumentType.greedyString())
                                            .requires(CommandSourceStack::isPlayer)
                                            .executes(this.commands::handleLogin)))
                            .then(Commands.literal("signup")
                                    .then(Commands.argument("password", StringArgumentType.greedyString())
                                            .requires(CommandSourceStack::isPlayer)
                                            .executes(this.commands::handleSignup)))
                            .then(Commands.literal("update")
                                    .then(Commands.argument("old-password", StringArgumentType.string())
                                            .then(Commands.argument("new-password", StringArgumentType.string())
                                                    .requires(CommandSourceStack::isPlayer)
                                                    .executes(this.commands::handleUpdate))))
                            .then(Commands.literal("delete")
                                    .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                    .then(Commands.argument("player", EntityArgument.player())
                                            .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                            .executes(this.commands::handleDelete)))));
        });
    }
}
