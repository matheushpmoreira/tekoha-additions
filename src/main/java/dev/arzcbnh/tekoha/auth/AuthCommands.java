package dev.arzcbnh.tekoha.auth;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.arzcbnh.tekoha.TekohaAdditions;
import dev.arzcbnh.tekoha.data.PlayerData;
import io.netty.buffer.Unpooled;
import java.util.Objects;
import java.util.UUID;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;

public class AuthCommands {
    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, build, selection) -> {
            dispatcher.register(Commands.literal("login")
                    .then(Commands.argument("password", StringArgumentType.greedyString())
                            .requires(CommandSourceStack::isPlayer)
                            .executes(AuthCommands::login)));

            dispatcher.register(Commands.literal("tekoha")
                    .then(Commands.literal("auth")
                            .then(Commands.literal("login")
                                    .then(Commands.argument("password", StringArgumentType.greedyString())
                                            .requires(CommandSourceStack::isPlayer)
                                            .executes(AuthCommands::login)))
                            .then(Commands.literal("update")
                                    .then(Commands.argument("old-password", StringArgumentType.string())
                                            .then(Commands.argument("new-password", StringArgumentType.string())
                                                    .requires(CommandSourceStack::isPlayer)
                                                    .executes(AuthCommands::update))))
                            .then(Commands.literal("delete")
                                    .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                    .then(Commands.argument("player", EntityArgument.player())
                                            .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                            .executes(AuthCommands::delete)))
                            .then(Commands.literal("allow")
                                    .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                    .then(Commands.argument("player", EntityArgument.player())
                                            .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                            .executes(AuthCommands::allow)))
                            .then(Commands.literal("forbid")
                                    .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                    .then(Commands.argument("player", EntityArgument.player())
                                            .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                            .executes(AuthCommands::forbid)))));
        });
    }

    private static int login(CommandContext<CommandSourceStack> context) {
        final var player = Objects.requireNonNull(context.getSource().getPlayer());
        final var password = context.getArgument("password", String.class);
        handleLoginRequest(player, password);
        return 1;
    }

    private static int update(CommandContext<CommandSourceStack> context) {
        final var player = Objects.requireNonNull(context.getSource().getPlayer());
        final var oldPassword = context.getArgument("old-password", String.class);
        final var newPassword = context.getArgument("new-password", String.class);
        handleUpdateRequest(player, oldPassword, newPassword);
        return 1;
    }

    private static int delete(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var player = EntityArgument.getPlayer(context, "player");
        PlayerData.of(player).setPassword(null);
        context.getSource()
                .sendSystemMessage(Component.literal("Deleted password for ").append(player.getName()));
        return 1;
    }

    private static int allow(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var player = EntityArgument.getPlayer(context, "player");
        enablePlayerInteraction(player);
        context.getSource()
                .sendSystemMessage(Component.literal("Manually allowed ").append(player.getName()));
        return 1;
    }

    private static int forbid(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var player = EntityArgument.getPlayer(context, "player");
        disablePlayerInteraction(player);
        context.getSource()
                .sendSystemMessage(Component.literal("Manually forbade ").append(player.getName()));
        return 1;
    }

    private static void handleLoginRequest(ServerPlayer player, String password) {
        final var data = PlayerData.of(player);
        final var entry = data.getPassword();
        final var service = ChatAuthService.getInstance();

        if (data.isAuthenticated()) {
            return;
        }

        if (entry.isEmpty()) {
            if (isPasswordInvalid(password)) {
                service.handleUnprocessable(player);
            } else {
                service.handleSuccess(player);
                data.setPassword(password);
                enablePlayerInteraction(player);
            }
        } else if (entry.get().matches(password)) {
            service.handleSuccess(player);
            enablePlayerInteraction(player);
        } else {
            service.handleUnauthorized(player);
        }
    }

    private static void handleUpdateRequest(ServerPlayer player, String oldPassword, String newPassword) {
        final var data = PlayerData.of(player);
        final var entry = data.getPassword();
        final var service = ChatAuthService.getInstance();

        if (entry.isEmpty()) {
            service.handleNotFound(player);
        } else if (!entry.get().matches(oldPassword)) {
            service.handleUnauthorized(player);
        } else if (isPasswordInvalid(newPassword)) {
            service.handleUnprocessable(player);
        } else {
            data.setPassword(newPassword);
            service.handleUpdate(player);
        }
    }

    public static void enablePlayerInteraction(ServerPlayer player) {
        final var data = PlayerData.of(player);
        final var service = ChatAuthService.getInstance();

        data.getDefaultGameType().ifPresent(player::setGameMode);
        data.setDefaultGameType(null);

        player.connection.send(new ClientboundRemoveEntitiesPacket(-1));
        service.handleAllow(player);
    }

    // FIXME: Not enough to prevent a player from interacting with the server.
    public static void disablePlayerInteraction(ServerPlayer player) {
        final var data = PlayerData.of(player);
        final var service = ChatAuthService.getInstance();

        if (data.getDefaultGameType().isEmpty()) {
            data.setDefaultGameType(player.gameMode());
        }

        player.setGameMode(GameType.SPECTATOR);

        player.connection.send(new ClientboundAddEntityPacket(
                -1,
                UUID.randomUUID(),
                player.getX(),
                player.getY(),
                player.getZ(),
                player.getXRot(),
                player.getYRot(),
                EntityType.CHICKEN,
                0,
                Vec3.ZERO,
                player.getYHeadRot()));

        player.connection.send(
                ClientboundSetPassengersPacket.STREAM_CODEC.decode(new FriendlyByteBuf(Unpooled.buffer(12))
                        .writeVarInt(-1)
                        .writeVarInt(1)
                        .writeVarInt(player.getId())));

        service.handleForbid(player);
    }

    public static boolean isPasswordInvalid(String password) {
        final var min = TekohaAdditions.CONFIG.passwordMinLength;
        final var max = TekohaAdditions.CONFIG.passwordMaxLength;
        final var len = password.length();
        return min > len || len > max;
    }
}
