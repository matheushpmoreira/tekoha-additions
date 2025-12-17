package dev.arzcbnh.tekoha.auth;

import net.minecraft.server.level.ServerPlayer;

public interface AuthService {
    // TODO: Maybe drop these integers. They're only useful for running commands.
    int beginAuth(ServerPlayer player);

    int endAuth(ServerPlayer player);

    int handleLoginRequest(ServerPlayer player, String password);

    int handleSignupRequest(ServerPlayer player, String password);

    int handleUpdateRequest(ServerPlayer player, String oldPassword, String newPassword);

    int handleDeleteRequest(ServerPlayer player, String password);
}
