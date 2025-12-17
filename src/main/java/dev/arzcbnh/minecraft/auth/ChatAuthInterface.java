package dev.arzcbnh.minecraft.auth;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.CommonColors;

import java.util.Arrays;

public class ChatAuthInterface implements AuthInterface {
    private final AuthService service;

    public ChatAuthInterface(AuthService service) {
        this.service = service;
    }

    @Override
    public void begin(ServerPlayer player) {
        service.freezePlayer(player);
        ChatMessage.WELCOME.send(player);
    }

    @Override
    public void leave(ServerPlayer player) {
        service.unfreezePlayer(player);
    }

    @Override
    public void onLoginRequest(ServerPlayer player, String password) {
        final var result = service.handleLoginRequest(player, password);
        ChatMessage.sendResponse(result, player);

//        switch (result) {
//            case NotFound -> ChatMessage.MISSING.sendTo(player);
//            case Unauthorized -> ChatMessage.WRONG.sendTo(player);
//            case OK -> ChatMessage.SUCCESS.sendTo(player);
//            case null, default -> {}
//        }
    }

    @Override
    public void onSignupRequest(ServerPlayer player, String password) {
        final var result = service.handleSignupRequest(player, password);
        ChatMessage.sendResponse(result, player);

//        switch (result) {
//            case Conflict -> ChatMessage.CONFLICT.sendTo(player);
//            case UnprocessableEntity -> ChatMessage.INVALID.sendTo(player);
//            case OK -> ChatMessage.SUCCESS.sendTo(player);
//            case null, default -> {}
//        }
    }

    @Override
    public void onUpdateRequest(ServerPlayer player, String oldPassword, String newPassword) {
        final var result = service.handlePasswordChangeRequest(player, oldPassword, newPassword);
        ChatMessage.sendResponse(result, player);

//        switch (result) {
//            case NotFound -> ChatMessage.MISSING.sendTo(player);
//            case Unauthorized -> ChatMessage.WRONG.sendTo(player);
//            case UnprocessableEntity -> ChatMessage.INVALID.sendTo(player);
//            case OK -> ChatMessage.SUCCESS.sendTo(player);
//            case null, default -> {}
//        }
    }

    @Override
    public void onDeleteRequest(ServerPlayer player, String password) {
        final var result =  service.handleDeleteRequest(player, password);
        ChatMessage.sendResponse(result, player);

//        switch (result) {
//            case NotFound -> ChatMessage.MISSING.sendTo(player);
//            case Unauthorized -> ChatMessage.WRONG.sendTo(player);
//            case OK -> ChatMessage.SUCCESS.sendTo(player);
//            case null, default -> {}
//        }
    }

    public void init() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            ServerPlayerEvents.JOIN.register(this::begin);
        });
    }

    private enum ChatMessage {
        CONFLICT("conflict", "login"),
        INVALID("invalid"),
        WELCOME("login", "login"),
        MISSING("missing", "signup"),
//        SIGNUP("signup", "signup"),
        SUCCESS("success"),
        WRONG("wrong");

        private final Component component;

        ChatMessage(String key, String... insert) {
            final var slots = Arrays.stream(insert).map(text -> Component.literal(text)
                    .withStyle(Style.EMPTY.withUnderlined(true).withClickEvent(new ClickEvent.SuggestCommand(text))
                    .withColor(CommonColors.WHITE))).toArray();

            this.component = Component.translatable("tekoha.auth.chat." + key, slots).withColor(CommonColors.GRAY);
        }

        public void send(ServerPlayer player) {
            player.sendSystemMessage(this.component);
        }

        public static void sendResponse(AuthResponse response, ServerPlayer player) {
            switch (response) {
                case Conflict -> CONFLICT.send(player);
                case NotFound -> MISSING.send(player);
                case OK -> SUCCESS.send(player);
                case Unauthorized -> WRONG.send(player);
                case UnprocessableEntity -> INVALID.send(player);
            }
        }
    }
}
