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
            dispatcher.register(Commands.literal("tekoha")
                    .then(Commands.literal("login")
                            .then(Commands.argument("password", StringArgumentType.greedyString())
                                    .requires(CommandSourceStack::isPlayer)
                                    .executes(AuthCommands::handleLogin)))
                    .then(Commands.literal("auth")
                            .then(Commands.literal("login")
                                    .then(Commands.argument("password", StringArgumentType.greedyString())
                                            .requires(CommandSourceStack::isPlayer)
                                            .executes(AuthCommands::handleLogin)))
                            .then(Commands.literal("update")
                                    .then(Commands.argument("old-password", StringArgumentType.string())
                                            .then(Commands.argument("new-password", StringArgumentType.string())
                                                    .requires(CommandSourceStack::isPlayer)
                                                    .executes(AuthCommands::handleUpdate))))
                            .then(Commands.literal("delete")
                                    .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                    .then(Commands.argument("player", EntityArgument.player())
                                            .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                            .executes(AuthCommands::handleDelete)))));
        });
    }

    public static void allowPlayer(ServerPlayer player) {
        final var data = PlayerData.of(player);
        final var service = ChatAuthService.getInstance();

        data.getDefaultGameType().ifPresent(player::setGameMode);
        data.setDefaultGameType(null);

        player.connection.send(new ClientboundRemoveEntitiesPacket(-1));
        service.handleAllow(player);
    }

    // FIXME: Not enough to prevent a player from interacting with the server.
    public static void forbidPlayer(ServerPlayer player) {
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

    public static int handleLogin(CommandContext<CommandSourceStack> context) {
        final var player = Objects.requireNonNull(context.getSource().getPlayer());
        final var password = context.getArgument("password", String.class);
        final var data = PlayerData.of(player);
        final var entry = data.getPassword();
        final var service = ChatAuthService.getInstance();

        if (data.isAuthenticated()) {
            return 0;
        } else if (entry.isEmpty()) {
            if (isPasswordInvalid(password)) {
                service.handleUnprocessable(player);
                return 0;
            } else {
                service.handleSuccess(player);
                data.setPassword(password);
                allowPlayer(player);
                return 1;
            }
        } else if (!entry.get().matches(password)) {
            service.handleUnauthorized(player);
            return 0;
        } else {
            service.handleSuccess(player);
            allowPlayer(player);
            return 1;
        }
    }

    public static int handleUpdate(CommandContext<CommandSourceStack> context) {
        final var player = Objects.requireNonNull(context.getSource().getPlayer());
        final var oldPassword = context.getArgument("old-password", String.class);
        final var newPassword = context.getArgument("new-password", String.class);
        final var data = PlayerData.of(player);
        final var entry = data.getPassword();
        final var service = ChatAuthService.getInstance();

        if (entry.isEmpty()) {
            service.handleNotFound(player);
            return 0;
        } else if (!entry.get().matches(oldPassword)) {
            service.handleUnauthorized(player);
            return 0;
        } else if (isPasswordInvalid(newPassword)) {
            service.handleUnprocessable(player);
            return 0;
        } else {
            data.setPassword(newPassword);
            service.handleUpdate(player);
            return 1;
        }
    }

    public static int handleDelete(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var player = Objects.requireNonNull(EntityArgument.getPlayer(context, "player"));
        final var data = PlayerData.of(player);

        data.setPassword(null);
        context.getSource()
                .sendSystemMessage(Component.literal("Deleted password for ").append(player.getName()));

        return 1;
    }

    public static boolean isPasswordInvalid(String password) {
        final var min = TekohaAdditions.CONFIG.passwordMinLength;
        final var max = TekohaAdditions.CONFIG.passwordMaxLength;
        final var len = password.length();
        return min > len || len > max;
    }
}
