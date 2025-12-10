package dev.arzcbnh.minecraft.auth;

import dev.arzcbnh.minecraft.TekohaAdditions;
import dev.arzcbnh.minecraft.data.PlayerData;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

/**
 * Handles the authentication of players.
 */
public interface AuthService {
    /**
     * Puts a player in the authentication state. The player is forbidden from interacting with the server (breaking,
     * moving, running commands, etc.) in any way but for authenticating.
     */
    default void enterAuthState(ServerPlayer player) {
        final var data = PlayerData.of(player);

        if (data.getDefaultGameType().isEmpty()) {
            TekohaAdditions.LOGGER.info("No game type found, storing {}", player.gameMode());
            data.setDefaultGameType(player.gameMode());
        }

        // FIXME: I know this doesn't prevent all player interactions, but it's enough for now.
        player.setGameMode(GameType.SPECTATOR);
    }

    /**
     * Removes the player from the authentication state. It should normally be called after the player is successfully authenticated. Does nothing if the player is not in the authentication state.
     */
    default void exitAuthState(ServerPlayer player) {
        final var data =  PlayerData.of(player);
        data.getDefaultGameType().ifPresent(player::setGameMode);
        data.setDefaultGameType(null);
    }

    /**
     * Handles a login request, namely whether to complete the authentication process given the credentials. May still respond in other ways to the request, such as showing the result on chat.
     */
    void handleLoginRequest(ServerPlayer player, String password);

    /**
     * Handles a signup request.
     */
    void handleSignupRequest(ServerPlayer player, String password);

    /**
     * Handles a password change request.
     */
    void handlePasswordChangeRequest(ServerPlayer player, String oldPassword, String newPassword);

    /**
     * Handles a delete request.
     */
    void handleDeleteRequest(ServerPlayer player, String password);

    /**
     * Initializes the service.
     */
    default void init() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            ServerPlayerEvents.JOIN.register(this::enterAuthState);
        });
    }
}
