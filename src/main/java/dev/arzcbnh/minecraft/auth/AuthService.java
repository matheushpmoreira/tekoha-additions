package dev.arzcbnh.minecraft.auth;

import dev.arzcbnh.minecraft.TekohaAdditions;
import dev.arzcbnh.minecraft.data.PlayerData;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.level.ServerPlayer;

public interface AuthService {
    int beginAuth(ServerPlayer player);

    int leaveAuth(ServerPlayer player);

    int handleLoginRequest(ServerPlayer player, String password);

    int handleSignupRequest(ServerPlayer player, String password);

    int handlePasswordChangeRequest(ServerPlayer player, String oldPassword, String newPassword);

    int handleDeleteRequest(ServerPlayer player, String password);

    int deletePlayerPassword(ServerPlayer player);
}
