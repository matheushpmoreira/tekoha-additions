package dev.arzcbnh.tekoha.auth;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.arzcbnh.tekoha.TekohaAdditions;
import dev.arzcbnh.tekoha.data.PlayerData;
import io.netty.buffer.Unpooled;
import java.util.Objects;
import java.util.UUID;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;

public class AuthCommands {
    public static void init() {
        final var prefix = Component.literal("*").withColor(CommonColors.RED);

        final var command = Component.translatable("tekoha.auth.command")
                .withStyle(Style.EMPTY
                        .withUnderlined(true)
                        .withClickEvent(new ClickEvent.SuggestCommand("/login "))
                        .withColor(CommonColors.WHITE));

        final var msg = prefix.append(
                Component.translatable("tekoha.auth.welcome", command).withColor(CommonColors.LIGHT_GRAY));

        ServerLifecycleEvents.SERVER_STARTED.register(server -> ServerPlayerEvents.JOIN.register(player -> {
            AuthCommands.disablePlayerInteraction(player);
            player.sendSystemMessage(msg);
        }));

        CommandRegistrationCallback.EVENT.register((dispatcher, build, selection) -> {
            dispatcher.register(Commands.literal("login")
                    .then(Commands.argument("password", StringArgumentType.greedyString())
                            .requires(CommandSourceStack::isPlayer)
                            .executes(AuthCommands::login)));

            dispatcher.register(Commands.literal("tekoha")
                    .then(Commands.literal("auth")
                            .then(Commands.literal("login")
                                    .then(Commands.argument("password", StringArgumentType.greedyString())
                                            .requires(CommandSourceStack::isPlayer)
                                            .executes(AuthCommands::login)))
                            .then(Commands.literal("update")
                                    .then(Commands.argument("old-password", StringArgumentType.string())
                                            .then(Commands.argument("new-password", StringArgumentType.string())
                                                    .requires(CommandSourceStack::isPlayer)
                                                    .executes(AuthCommands::update))))
                            .then(Commands.literal("delete")
                                    .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                    .then(Commands.argument("player", EntityArgument.player())
                                            .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                            .executes(AuthCommands::delete)))
                            .then(Commands.literal("allow")
                                    .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                    .then(Commands.argument("player", EntityArgument.player())
                                            .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                            .executes(AuthCommands::allow)))
                            .then(Commands.literal("forbid")
                                    .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                    .then(Commands.argument("player", EntityArgument.player())
                                            .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                            .executes(AuthCommands::forbid)))));
        });
    }

    private static int login(CommandContext<CommandSourceStack> context) {
        final var player = Objects.requireNonNull(context.getSource().getPlayer());
        final var password = context.getArgument("password", String.class);
        final var result = handleLoginRequest(player, password);
        result.notify(player);

        if (result == AuthResult.SUCCESS || result == AuthResult.REGISTERED) {
            enablePlayerInteraction(player);
        }

        return 1;
    }

    private static int update(CommandContext<CommandSourceStack> context) {
        final var player = Objects.requireNonNull(context.getSource().getPlayer());
        final var oldPassword = context.getArgument("old-password", String.class);
        final var newPassword = context.getArgument("new-password", String.class);
        final var result = handleUpdateRequest(player, oldPassword, newPassword);
        result.notify(player);

        if (result == AuthResult.SUCCESS || result == AuthResult.REGISTERED) {
            enablePlayerInteraction(player);
        }

        return 1;
    }

    private static int delete(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var player = EntityArgument.getPlayer(context, "player");
        PlayerData.of(player).setPassword(null);
        context.getSource().sendSystemMessage(Component.translatable("tekoha.auth.deleted", player.getName()));
        return 1;
    }

    private static int allow(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var player = EntityArgument.getPlayer(context, "player");
        enablePlayerInteraction(player);
        context.getSource().sendSystemMessage(Component.translatable("tekoha.auth.allowed", player.getName()));
        return 1;
    }

    private static int forbid(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var player = EntityArgument.getPlayer(context, "player");
        disablePlayerInteraction(player);
        context.getSource().sendSystemMessage(Component.translatable("tekoha.auth.forbade", player.getName()));
        return 1;
    }

    private static AuthResult handleLoginRequest(ServerPlayer player, String password) {
        final var data = PlayerData.of(player);
        final var entry = data.getPassword();

        if (data.isAuthenticated()) {
            return AuthResult.ALREADY_AUTHENTICATED;
        }

        if (entry.isEmpty()) {
            if (isPasswordInvalid(password)) {
                return AuthResult.INVALID_PASSWORD;
            } else {
                data.setPassword(password);
                return AuthResult.REGISTERED;
            }
        } else if (entry.get().matches(password)) {
            return AuthResult.SUCCESS;
        } else {
            return AuthResult.WRONG_PASSWORD;
        }
    }

    private static AuthResult handleUpdateRequest(ServerPlayer player, String oldPassword, String newPassword) {
        final var data = PlayerData.of(player);
        final var entry = data.getPassword();

        if (entry.isEmpty()) {
            if (isPasswordInvalid(newPassword)) {
                return AuthResult.INVALID_PASSWORD;
            } else {
                data.setPassword(newPassword);
                return AuthResult.REGISTERED;
            }
        } else if (!entry.get().matches(oldPassword)) {
            return AuthResult.WRONG_PASSWORD;
        } else if (isPasswordInvalid(newPassword)) {
            return AuthResult.INVALID_PASSWORD;
        } else {
            data.setPassword(newPassword);
            return AuthResult.UPDATED;
        }
    }

    public static void enablePlayerInteraction(ServerPlayer player) {
        final var data = PlayerData.of(player);
        data.getDefaultGameType().ifPresent(player::setGameMode);
        data.setDefaultGameType(null);
        player.connection.send(new ClientboundRemoveEntitiesPacket(-1));
    }

    // FIXME: Not enough to prevent a player from interacting with the server.
    public static void disablePlayerInteraction(ServerPlayer player) {
        final var data = PlayerData.of(player);

        if (data.getDefaultGameType().isEmpty()) {
            data.setDefaultGameType(player.gameMode());
        }

        player.setGameMode(GameType.SPECTATOR);

        player.connection.send(new ClientboundAddEntityPacket(
                -1,
                UUID.randomUUID(),
                player.getX(),
                player.getY(),
                player.getZ(),
                player.getXRot(),
                player.getYRot(),
                EntityType.CHICKEN,
                0,
                Vec3.ZERO,
                player.getYHeadRot()));

        player.connection.send(
                ClientboundSetPassengersPacket.STREAM_CODEC.decode(new FriendlyByteBuf(Unpooled.buffer(12))
                        .writeVarInt(-1)
                        .writeVarInt(1)
                        .writeVarInt(player.getId())));
    }

    public static boolean isPasswordInvalid(String password) {
        final var min = TekohaAdditions.CONFIG.passwordMinLength;
        final var max = TekohaAdditions.CONFIG.passwordMaxLength;
        final var len = password.length();
        return min > len || len > max;
    }

    private enum AuthResult {
        ALREADY_AUTHENTICATED("tekoha.auth.already_authenticated"),
        INVALID_PASSWORD("tekoha.auth.invalid_password"),
        REGISTERED("tekoha.auth.registered"),
        SUCCESS("tekoha.auth.success"),
        UPDATED("tekoha.auth.updated"),
        WRONG_PASSWORD("tekoha.auth.wrong_password");

        private final Component component;

        AuthResult(String key) {
            final var prefix = Component.literal("*").withColor(CommonColors.RED);
            this.component = prefix.append(Component.translatable(key).withColor(CommonColors.LIGHT_GRAY));
        }

        public void notify(ServerPlayer player) {
            player.sendSystemMessage(this.component);
        }
    }
}
