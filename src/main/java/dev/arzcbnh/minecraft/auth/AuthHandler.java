package dev.arzcbnh.minecraft.auth;

import net.minecraft.network.packet.s2c.common.ClearDialogS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuthHandler {
    private static final Map<ServerPlayerEntity, AuthHandler> pending = new ConcurrentHashMap<>();

    private final PasswordDatabase database;
    private final ServerPlayerEntity player;
    private final GameMode gamemode;
    private final PasswordData data;

    private AuthHandler(ServerPlayerEntity player, PasswordDatabase db) {
        this.database = db;
        this.player = player;
        this.gamemode = player.getGameMode();
        this.data = database.retrieve(player.getUuid()).orElse(null);
    }

    /**
     * Begins the authentication process for a player, which includes freezing them and prompting for a password.
     * @param player    the player to authenticate.
     */
    public static void begin(ServerPlayerEntity player) {
        // TODO: invert database dependency
        final var instance = new AuthHandler(player, PropertiesPasswordDatabase.getInstance());
        instance.begin();
    }

    public void begin() {
        pending.put(player, this);

        if (data == null) {
            this.deny(AuthFormDialog.Message.SIGNUP);
        } else if (data.isBlank()) {
            allow();
        } else {
            deny(AuthFormDialog.Message.LOGIN);
        }
    }

    /**
     * Offer a password for the manager to validate against. The player is unfrozen and authenticated if valid,
     * or prompted again if not. Does nothing if no handler exists for the player.
     *
     * @param player    the player to authenticate.
     * @param password  the offered password.
     */
    public static void offer(ServerPlayerEntity player, String password) {
        final var instance = pending.get(player);

        if (instance != null) {
            instance.offer(password);
        }
    }

    public void offer(String password) {
        if (data == null) {
            database.store(PasswordData.fromPassword(player.getUuid(), password));
            allow();
        } else if (data.satisfies(password)) {
            allow();
        } else {
            deny(AuthFormDialog.Message.FAIL);
        }
    }

    /**
     * Cancel the authentication process for a player. Useful for returning the player to original game mode without giving them access to the world.
     * @param player    the player to cancel authentication for.
     */
    public static void cancel(ServerPlayerEntity player) {
        final var instance = pending.get(player);

        if (instance != null) {
            instance.cancel();
        }
    }

    public void cancel() {
        player.changeGameMode(gamemode);
        pending.remove(player);
    }

    private void deny(AuthFormDialog.Message message) {
        player.openDialog(RegistryEntry.of(AuthFormDialog.getInstance(player.getUuid(), message)));
        player.changeGameMode(GameMode.SPECTATOR);
    }

    private void allow() {
        // ServerPlayerEntity lacks a helper method for clearing dialogs for some reason
        player.networkHandler.sendPacket(ClearDialogS2CPacket.INSTANCE);
        player.changeGameMode(gamemode);
        pending.remove(player);
    }
}
