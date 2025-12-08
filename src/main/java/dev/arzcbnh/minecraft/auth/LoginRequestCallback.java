package dev.arzcbnh.minecraft.auth;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;

public interface LoginRequestCallback {
    Event<LoginRequestCallback> EVENT = EventFactory.createArrayBacked(LoginRequestCallback.class,
            listeners -> (player, password) -> {
        for (LoginRequestCallback listener : listeners) {
            listener.offer(player, password);
        }
    });

    void offer(ServerPlayer player, String password);
}
