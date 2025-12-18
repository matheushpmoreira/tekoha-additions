package dev.arzcbnh.tekoha.auth;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.arzcbnh.tekoha.data.PlayerData;
import dev.arzcbnh.tekoha.util.ModConfig;
import io.netty.buffer.Unpooled;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
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
    private final int passwordMinLength;
    private final int passwordMaxLength;
    private final AuthService defaultService;

    public AuthCommands(ModConfig config) {
        this.passwordMinLength = config.passwordMinLength;
        this.passwordMaxLength = config.passwordMaxLength;
        this.defaultService = AuthService.fromType(config.defaultAuthService);
    }

    public void allowPlayer(ServerPlayer player) {
        final var data = PlayerData.of(player);
        final var service = data.getAuthService().orElse(this.defaultService);

        data.getDefaultGameType().ifPresent(player::setGameMode);
        data.setDefaultGameType(null);

        player.connection.send(new ClientboundRemoveEntitiesPacket(-1));
        service.handleAllow(player);
    }

    // FIXME: Not enough to prevent a player from interacting with the server.
    public void forbidPlayer(ServerPlayer player) {
        final var data = PlayerData.of(player);
        final var service = data.getAuthService().orElse(this.defaultService);

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

    public int handleLogin(CommandContext<CommandSourceStack> context) {
        final var player = Objects.requireNonNull(context.getSource().getPlayer());
        final var password = context.getArgument("password", String.class);
        final var data = PlayerData.of(player);
        final var entry = data.getPassword();
        final var service = data.getAuthService().orElse(this.defaultService);

        if (data.isAuthenticated()) {
            return 0;
        } else if (entry.isEmpty()) {
            service.handleNotFound(player);
            return 0;
        } else if (!entry.get().matches(password)) {
            service.handleUnauthorized(player);
            return 0;
        } else {
            service.handleSuccess(player);
            this.allowPlayer(player);
            return 1;
        }
    }

    public int handleSignup(CommandContext<CommandSourceStack> context) {
        final var player = Objects.requireNonNull(context.getSource().getPlayer());
        final var password = context.getArgument("password", String.class);
        final var data = PlayerData.of(player);
        final var service = data.getAuthService().orElse(this.defaultService);

        if (data.isAuthenticated()) {
            return 0;
        } else if (data.getPassword().isPresent()) {
            service.handleConflict(player);
            return 0;
        } else if (isPasswordInvalid(password)) {
            service.handleUnprocessable(player);
            return 0;
        } else {
            service.handleSuccess(player);
            data.setPassword(password);
            this.allowPlayer(player);
            return 1;
        }
    }

    public int handleUpdate(CommandContext<CommandSourceStack> context) {
        final var player = Objects.requireNonNull(context.getSource().getPlayer());
        final var oldPassword = context.getArgument("old-password", String.class);
        final var newPassword = context.getArgument("new-password", String.class);
        final var data = PlayerData.of(player);
        final var entry = data.getPassword();
        final var service = data.getAuthService().orElse(this.defaultService);

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

    public int handleDelete(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var player = Objects.requireNonNull(EntityArgument.getPlayer(context, "player"));
        final var data = PlayerData.of(player);

        data.setPassword(null);
        context.getSource()
                .sendSystemMessage(Component.literal("Deleted password for ").append(player.getName()));

        return 1;
    }

    public boolean isPasswordInvalid(String password) {
        return passwordMinLength > password.length() || password.length() > passwordMaxLength;
    }
}
