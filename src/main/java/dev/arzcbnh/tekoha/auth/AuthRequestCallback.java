package dev.arzcbnh.tekoha.auth;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;

public interface AuthRequestCallback {
    Event<AuthRequestCallback> LOGIN =
            EventFactory.createArrayBacked(AuthRequestCallback.class, listeners -> (player, password) -> {
                for (AuthRequestCallback listener : listeners) {
                    listener.offer(player, password);
                }
            });

    Event<AuthRequestCallback> SIGNUP =
            EventFactory.createArrayBacked(AuthRequestCallback.class, listeners -> (player, password) -> {
                for (AuthRequestCallback listener : listeners) {
                    listener.offer(player, password);
                }
            });

    void offer(ServerPlayer player, String password);
}
