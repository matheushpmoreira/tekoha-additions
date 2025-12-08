package dev.arzcbnh.minecraft.auth;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public record AuthProcess(ServerPlayer player, GameType gamemode, PasswordData data) {}
