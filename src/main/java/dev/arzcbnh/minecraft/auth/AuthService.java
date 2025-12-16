package dev.arzcbnh.minecraft.auth;

import dev.arzcbnh.minecraft.TekohaAdditions;
import dev.arzcbnh.minecraft.data.PlayerData;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

///**
// * Handles the authentication of players.
// */
public interface AuthService {
//    /**
//     * Puts a player in the authentication state. The player is forbidden from interacting with the server (breaking,
//     * moving, running commands, etc.) in any way but for authenticating.
//     */
    default void enterAuthState(ServerPlayer player) {
        final var data = PlayerData.of(player);

        if (data.getDefaultGameType().isEmpty()) {
            TekohaAdditions.LOGGER.info("No game type found, storing {}", player.gameMode());
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
    }

//    /**
//     * Removes the player from the authentication state. It should normally be called after the player is successfully authenticated. Does nothing if the player is not in the authentication state.
//     */
    default void exitAuthState(ServerPlayer player) {
        final var data =  PlayerData.of(player);
        data.getDefaultGameType().ifPresent(player::setGameMode);
        data.setDefaultGameType(null);

        player.connection.send(new ClientboundRemoveEntitiesPacket(-1));
    }

//    /**
//     * Handles a login request, namely whether to complete the authentication process given the credentials. May still respond in other ways to the request, such as showing the result on chat.
//     */
    void handleLoginRequest(ServerPlayer player, String password);

//    /**
//     * Handles a signup request.
//     */
    void handleSignupRequest(ServerPlayer player, String password);

//    /**
//     * Handles a password change request.
//     */
    void handlePasswordChangeRequest(ServerPlayer player, String oldPassword, String newPassword);

//    /**
//     * Handles a delete request.
//     */
    void handleDeleteRequest(ServerPlayer player, String password);

//    /**
//     * Initializes the service.
//     */
    default void init() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            ServerPlayerEvents.JOIN.register(this::enterAuthState);
        });
    }
}
