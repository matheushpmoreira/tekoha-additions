package dev.arzcbnh.tekoha.auth;

import com.mojang.serialization.Codec;
import net.minecraft.server.level.ServerPlayer;

public interface AuthService {
    Codec<AuthService> CODEC = Codec.STRING.xmap(AuthService::fromType, AuthService::toType);

    static String toType(AuthService service) {
        return switch (service) {
            case ChatAuthService ignored -> "chat";
            case null, default -> throw new RuntimeException("Unknown AuthService instance: " + service);
        };
    }

    static AuthService fromType(String str) {
        return switch (str) {
            case "chat" -> ChatAuthService.getInstance();
            case null, default -> throw new RuntimeException("Unknown AuthService type: " + str);
        };
    }

    void handleAllow(ServerPlayer player);

    void handleConflict(ServerPlayer player);

    void handleForbid(ServerPlayer player);

    void handleNotFound(ServerPlayer player);

    void handleSuccess(ServerPlayer player);

    void handleUnauthorized(ServerPlayer player);

    void handleUnprocessable(ServerPlayer player);

    void handleUpdate(ServerPlayer player);
}
