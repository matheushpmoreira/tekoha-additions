package dev.arzcbnh.tekoha.auth;

import java.util.Arrays;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.CommonColors;

public class ChatAuthService implements AuthService {
    private static ChatAuthService instance;

    private ChatAuthService() {}

    public static ChatAuthService getInstance() {
        if (instance == null) {
            instance = new ChatAuthService();
        }

        return instance;
    }

    @Override
    public void handleAllow(ServerPlayer player) {
        // Do nothing
    }

    @Override
    public void handleConflict(ServerPlayer player) {
        ChatMessage.Conflict.sendTo(player);
    }

    @Override
    public void handleForbid(ServerPlayer player) {
        ChatMessage.Welcome.sendTo(player);
    }

    @Override
    public void handleNotFound(ServerPlayer player) {
        ChatMessage.NotFound.sendTo(player);
    }

    @Override
    public void handleSuccess(ServerPlayer player) {
        ChatMessage.Success.sendTo(player);
    }

    @Override
    public void handleUnauthorized(ServerPlayer player) {
        ChatMessage.Unauthorized.sendTo(player);
    }

    @Override
    public void handleUnprocessable(ServerPlayer player) {
        ChatMessage.Unprocessable.sendTo(player);
    }

    @Override
    public void handleUpdate(ServerPlayer player) {
        ChatMessage.Update.sendTo(player);
    }

    private enum ChatMessage {
        Conflict("conflict", "/tekoha login"),
        NotFound("notfound", "/tekoha signup"),
        Success("success"),
        Unauthorized("unauthorized"),
        Unprocessable("unprocessable"),
        Update("update"),
        Welcome("welcome", "/tekoha login", "/tekoha signup");

        private final Component component;

        ChatMessage(String key, String... insert) {
            final var slots = Arrays.stream(insert)
                    .map(text -> Component.literal(text)
                            .withStyle(Style.EMPTY
                                    .withUnderlined(true)
                                    .withClickEvent(new ClickEvent.SuggestCommand(text + " "))
                                    .withColor(CommonColors.WHITE)))
                    .toArray();

            this.component = Component.literal("*")
                    .withColor(CommonColors.RED)
                    .append(Component.translatable("tekoha.auth.chat." + key, slots)
                            .withColor(CommonColors.LIGHT_GRAY));
        }

        public void sendTo(ServerPlayer player) {
            player.sendSystemMessage(this.component);
        }
    }
}
