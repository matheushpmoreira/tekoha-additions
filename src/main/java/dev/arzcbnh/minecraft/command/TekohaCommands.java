package dev.arzcbnh.minecraft.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.arzcbnh.minecraft.auth.ChatAuthService;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;

public class TekohaCommands {
    public static void init(ChatAuthService service) {
        CommandRegistrationCallback.EVENT.register((dispatcher, build, selection) -> {
            dispatcher.register(Commands.literal("tekoha")
                    .then(Commands.literal("login")
                            .then(Commands.argument("password", StringArgumentType.greedyString())
                                    .executes(context -> {
                                        service.handleLoginRequest(context.getSource().getPlayer(), context.getArgument("password", String.class));
                                        return Command.SINGLE_SUCCESS;
                                    }).requires(CommandSourceStack::isPlayer)))
                    .then(Commands.literal("signup")
                            .then(Commands.argument("password", StringArgumentType.greedyString())
                                    .executes(context -> {
                                        service.handleSignupRequest(context.getSource().getPlayer(), context.getArgument("password", String.class));
                                        return Command.SINGLE_SUCCESS;
                                    }).requires(CommandSourceStack::isPlayer)))
                    .then(Commands.literal("auth")
                            .then(Commands.literal("login")
                                    .then(Commands.argument("password", StringArgumentType.greedyString())
                                            .executes(context -> {
                                                service.handleLoginRequest(context.getSource().getPlayer(), context.getArgument("password", String.class));
                                                return Command.SINGLE_SUCCESS;
                                            }).requires(CommandSourceStack::isPlayer)))
                            .then(Commands.literal("signup")
                                    .then(Commands.argument("password", StringArgumentType.greedyString())
                                            .executes(context -> {
                                                service.handleSignupRequest(context.getSource().getPlayer(), context.getArgument("password", String.class));
                                                return Command.SINGLE_SUCCESS;
                                            }).requires(CommandSourceStack::isPlayer)))
                            .then(Commands.literal("change")
                                    .then(Commands.argument("old_password", StringArgumentType.string())
                                            .then(Commands.argument("new_password", StringArgumentType.string())
                                                    .executes(context -> {
                                                        service.handlePasswordChangeRequest(context.getSource().getPlayer(), context.getArgument("old_password", String.class), context.getArgument("new_password", String.class));
                                                        return Command.SINGLE_SUCCESS;
                                                    }).requires(CommandSourceStack::isPlayer)))
                            .then(Commands.literal("delete")
                                    .then(Commands.argument("password", StringArgumentType.greedyString())
                                            .executes(context -> {
                                                service.handleDeleteRequest(context.getSource().getPlayer(), context.getArgument("password", String.class));
                                                return Command.SINGLE_SUCCESS;
                                            }).requires(CommandSourceStack::isPlayer)))
                                    .then(Commands.argument("player", EntityArgument.player())
                                            .executes(context -> {
                                                service.deletePlayerPassword(EntityArgument.getPlayer(context, "player"));
                                                return Command.SINGLE_SUCCESS;
                                            }).requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))))
                            .then(Commands.literal("enter")
                                    .then(Commands.argument("player", EntityArgument.player())
                                            .executes(context -> {
                                                service.freezePlayer(EntityArgument.getPlayer(context, "player"));
                                                return Command.SINGLE_SUCCESS;
                                            }).requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))))
                            .then(Commands.literal("exit")
                                    .then(Commands.argument("player", EntityArgument.player())
                                            .executes(context -> {
                                                service.unfreezePlayer(EntityArgument.getPlayer(context, "player"));
                                                return Command.SINGLE_SUCCESS;
                                            }).requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS)))))
            );
        });
    }
}
