package dev.arzcbnh.minecraft.auth;

import dev.arzcbnh.minecraft.TekohaAdditions;
import dev.arzcbnh.minecraft.data.PlayerData;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class ChatAuthService implements AuthService {
    @Override
    public int beginAuth(ServerPlayer player) {
        final var data = PlayerData.of(player);

        if (data.getDefaultGameType().isEmpty()) {
            data.setDefaultGameType(player.gameMode());
        }

        // FIXME: I know this doesn't prevent all player interactions, but it's enough for now.
        player.setGameMode(GameType.SPECTATOR);

        // Mounts the player on a fake invisible entity to prevent movement. The entity ID used is -1; I think Minecraft
        // only uses non-negative integers so it never conflicts. The passenger packet has to be decoded from a byte
        // buffer, since the entity is fake. The buffer has three four-byte integers: the vehicle ID, the passenger
        // array length, and the passengers ID, of which there's always one. I picked a chicken because a player keeps
        // the same eye level when mounting it, but unfortunately I wasn't able to make it invisible, nor able to hide
        // the "vehicle hearts". That must do for now.
        player.connection.send(new ClientboundAddEntityPacket(-1, UUID.randomUUID(), player.getX(), player.getY(), player.getZ(), player.getXRot(), player.getYRot(), EntityType.CHICKEN, 0, Vec3.ZERO, player.getYHeadRot()));
        player.connection.send(ClientboundSetPassengersPacket.STREAM_CODEC.decode(new FriendlyByteBuf(Unpooled.buffer(12)).writeVarInt(-1).writeVarInt(1).writeVarInt(player.getId())));

        return 1;
    }

    @Override
    public int leaveAuth(ServerPlayer player) {
        final var data =  PlayerData.of(player);
        data.getDefaultGameType().ifPresent(player::setGameMode);
        data.setDefaultGameType(null);

        player.connection.send(new ClientboundRemoveEntitiesPacket(-1));
        return 1;
    }

    @Override
    public int handleLoginRequest(ServerPlayer player, String password) {
        final var entry = PlayerData.of(player).getPassword();

        if (entry.isEmpty()) {
            return AuthResponse.NotFound;
        } else if (!entry.get().matches(password)) {
            return AuthResponse.Unauthorized;
        } else {
            beginAuth(player);
            return AuthResponse.OK;
        }

        return 1;
    }

    @Override
    public int handleSignupRequest(ServerPlayer player, String password) {
        final var data = PlayerData.of(player);

        if (data.getPassword().isPresent()) {
            return AuthResponse.Conflict;
        } else if (isPasswordInvalid(password)) {
            return AuthResponse.UnprocessableEntity;
        } else {
            unfreezePlayer(player);
            data.setPassword(password);
            return AuthResponse.OK;
        }
    }

    @Override
    public int handlePasswordChangeRequest(ServerPlayer player, String oldPassword, String newPassword) {
        final var data = PlayerData.of(player);
        final var entry = data.getPassword();

        if (entry.isEmpty()) {
            return AuthResponse.NotFound;
        } else if (!entry.get().matches(oldPassword)) {
            return AuthResponse.Unauthorized;
        } else if (isPasswordInvalid(newPassword)) {
            return AuthResponse.UnprocessableEntity;
        } else {
            data.setPassword(newPassword);
            return AuthResponse.OK;
        }
    }

    @Override
    public int handleDeleteRequest(ServerPlayer player, String password) {
        final var data = PlayerData.of(player);
        final var entry = data.getPassword();

        if (entry.isEmpty()) {
            return AuthResponse.NotFound;
        } else if (!entry.get().matches(password)) {
            return AuthResponse.Unauthorized;
        } else {
            data.setPassword(null);
            return AuthResponse.OK;
        }
    }

    @Override
    public void deletePlayerPassword(ServerPlayer player) {
        final var data = PlayerData.of(player);
        data.setPassword(null);
    }

    public boolean isPasswordInvalid(String password) {
        return TekohaAdditions.CONFIG.passwordMinLength > password.length() ||
                password.length() > TekohaAdditions.CONFIG.passwordMaxLength;
    }
}
