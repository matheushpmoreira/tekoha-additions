package dev.arzcbnh.minecraft.auth;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.serialization.JsonOps;
import dev.arzcbnh.minecraft.TekohaAdditions;
import dev.arzcbnh.minecraft.data.PlayerData;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.CommonColors;
import net.minecraft.world.level.GameType;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;

public class ChatAuthService implements AuthService {
    // TODO: I would love if I could store text components in language files. I tried looking into how SNBT is parsed
    // into text components, but it threw me for a loop. I know I don't have much knowledge, but the Minecraft codebase
    // has to be objectively spaghetti. I'll keep it like this for now, I don't even intend to use the chat service for
    // long; and it works well enough.
    private final Component CONFLICT = composeComponent("conflict", "login");
    private final Component INVALID =  composeComponent("invalid", null);
    private final Component LOGIN = composeComponent("login", "login");
    private final Component MISSING = composeComponent("missing", "signup");
    private final Component SIGNUP = composeComponent("signup", "signup");
    private final Component SUCCESS = composeComponent("success", null);
    private final Component WRONG =  composeComponent("wrong", null);

    private static Component composeComponent(String translationKey, String command) {
        if (command == null) {
            return Component.translatable("tekoha.auth.chat." + translationKey).withColor(CommonColors.GRAY);
        } else {
            final var clickEvent = new ClickEvent.SuggestCommand("/" + command + " ");
            final var insert = Component.literal("/" + command)
                    .withColor(CommonColors.WHITE)
                    .withStyle(Style.EMPTY.withUnderlined(true).withClickEvent(clickEvent));

            return Component.translatable("tekoha.auth.chat." + translationKey, insert).withColor(CommonColors.GRAY);
        }
    }

    @Override
    public void enterAuthState(ServerPlayer player) {
        AuthService.super.enterAuthState(player);
        final var data = PlayerData.of(player);

        if (data.getPassword().isPresent()) {
            player.sendSystemMessage(LOGIN);
        } else {
            player.sendSystemMessage(SIGNUP);
        }
    }

    @Override
    public void exitAuthState(ServerPlayer player) {
        AuthService.super.exitAuthState(player);
        player.sendSystemMessage(SUCCESS);
    }

    @Override
    public void handleLoginRequest(ServerPlayer player, String password) {
        final var entry = PlayerData.of(player).getPassword();

        if (entry.isEmpty()) {
            player.sendSystemMessage(MISSING);
        } else if (!entry.get().matches(password)) {
            player.sendSystemMessage(WRONG);
        } else {
            exitAuthState(player);
        }
    }

    @Override
    public void handleSignupRequest(ServerPlayer player, String password) {
        final var data = PlayerData.of(player);

        if (data.getPassword().isPresent()) {
            player.sendSystemMessage(CONFLICT);
        } else if (TekohaAdditions.CONFIG.passwordMinLength > password.length() || password.length() > TekohaAdditions.CONFIG.passwordMaxLength) {
            player.sendSystemMessage(INVALID);
        } else {
            data.setPassword(password);
            exitAuthState(player);
        }
    }

    @Override
    public void handlePasswordChangeRequest(ServerPlayer player, String oldPassword, String newPassword) {
        final var data = PlayerData.of(player);
        final var entry = data.getPassword();

        if (entry.isEmpty()) {
            player.sendSystemMessage(MISSING);
        } else if (!entry.get().matches(oldPassword)) {
            player.sendSystemMessage(WRONG);
        } else if (TekohaAdditions.CONFIG.passwordMinLength > newPassword.length() || newPassword.length() > TekohaAdditions.CONFIG.passwordMaxLength) {
            player.sendSystemMessage(INVALID);
        } else {
            data.setPassword(newPassword);
        }
    }

    @Override
    public void handleDeleteRequest(ServerPlayer player, String password) {
        final var data = PlayerData.of(player);
        final var entry = data.getPassword();

        if (entry.isEmpty()) {
            player.sendSystemMessage(MISSING);
        } else if (!entry.get().matches(password)) {
            player.sendSystemMessage(WRONG);
        } else {
            data.setPassword(null);
        }
    }

    @Override
    public void init() {
        AuthService.super.init();

        CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, selection) -> {
            dispatcher.register(Commands.literal("login")
                    .then(Commands.argument("password", StringArgumentType.greedyString())
                            .executes(ctx -> {
                                this.handleLoginRequest(ctx.getSource().getPlayer(), ctx.getArgument("password", String.class));
                                return 0;
                            }).requires(CommandSourceStack::isPlayer)));

            dispatcher.register(Commands.literal("signup")
                    .then(Commands.argument("password", StringArgumentType.greedyString())
                            .executes(ctx -> {
                                this.handleSignupRequest(ctx.getSource().getPlayer(), ctx.getArgument("password", String.class));
                                return 0;
                            }).requires(CommandSourceStack::isPlayer)));
//
//            dispatcher.register(Commands.literal("changePassword")
//                    .then(Commands.argument("password", StringArgumentType.greedyString())
//                            .executes(ctx -> {
//                                this.handleSignupRequest(ctx.getSource().getPlayer(), ctx.getArgument("password", String.class));
//                                return 0;
//                            }).requires(CommandSourceStack::isPlayer)));
//
//            dispatcher.register(Commands.literal("deletePassword")
//                    .then(Commands.argument("password", StringArgumentType.greedyString())
//                            .executes(ctx -> {
//                                this.handleSignupRequest(ctx.getSource().getPlayer(), ctx.getArgument("password", String.class));
//                                return 0;
//                            }).requires(CommandSourceStack::isPlayer)));
        });
    }
}
