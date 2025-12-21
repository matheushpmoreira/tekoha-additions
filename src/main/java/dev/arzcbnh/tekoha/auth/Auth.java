package dev.arzcbnh.tekoha.auth;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class Auth {
    public void init() {
        AuthCommands.init();

        ServerLifecycleEvents.SERVER_STARTED.register(
                server -> ServerPlayerEvents.JOIN.register(AuthCommands::forbidPlayer));
    }
}
