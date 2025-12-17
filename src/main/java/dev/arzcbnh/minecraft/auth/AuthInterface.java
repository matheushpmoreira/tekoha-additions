package dev.arzcbnh.minecraft.auth;

import net.minecraft.server.level.ServerPlayer;

public interface AuthInterface {
    void begin(ServerPlayer player);

    void leave(ServerPlayer player);

    void onLoginRequest(ServerPlayer player, String password);

    void onSignupRequest(ServerPlayer player, String password);

    void onUpdateRequest(ServerPlayer player, String oldPassword, String newPassword);

    void onDeleteRequest(ServerPlayer player, String password);
}
