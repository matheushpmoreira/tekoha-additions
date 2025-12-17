package dev.arzcbnh.tekoha.auth;

import dev.arzcbnh.tekoha.TekohaAdditions;
import dev.arzcbnh.tekoha.data.PlayerData;
import io.netty.buffer.Unpooled;
import java.util.Arrays;
import java.util.UUID;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
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

public class ChatAuthService implements AuthService {
    @Override
    public int beginAuth(ServerPlayer player) {
        final var data = PlayerData.of(player);

        if (data.getDefaultGameType().isEmpty()) {
            data.setDefaultGameType(player.gameMode());
        }

        // FIXME: Not enough to prevent all player interactions.
        player.setGameMode(GameType.SPECTATOR);

        // Mounts the player on a fake invisible entity to prevent movement. The entity ID used is -1; I think Minecraft
        // only uses non-negative integers so it never conflicts. The passenger packet has to be decoded from a byte
        // buffer, since the entity is fake. The buffer has three four-byte integers: the vehicle ID, the passenger
        // array length, and the passengers ID, of which there's always one. I picked a chicken because a player keeps
        // the same eye level when mounting it, but unfortunately I wasn't able to make it invisible, nor able to hide
        // the "vehicle hearts". That must do for now.
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
        ChatMessage.Welcome.sendTo(player);

        return 1;
    }

    @Override
    public int endAuth(ServerPlayer player) {
        final var data = PlayerData.of(player);
        data.getDefaultGameType().ifPresent(player::setGameMode);
        data.setDefaultGameType(null);

        player.connection.send(new ClientboundRemoveEntitiesPacket(-1));
        ChatMessage.Success.sendTo(player);

        return 1;
    }

    @Override
    public int handleLoginRequest(ServerPlayer player, String password) {
        final var data = PlayerData.of(player);
        final var entry = data.getPassword();

        if (data.isAuthenticated()) {
            return 0;
        } else if (entry.isEmpty()) {
            ChatMessage.NotFound.sendTo(player);
        } else if (!entry.get().matches(password)) {
            ChatMessage.Unauthorized.sendTo(player);
        } else {
            endAuth(player);
        }

        return 1;
    }

    @Override
    public int handleSignupRequest(ServerPlayer player, String password) {
        final var data = PlayerData.of(player);

        if (data.isAuthenticated()) {
            return 0;
        } else if (data.getPassword().isPresent()) {
            ChatMessage.Conflict.sendTo(player);
        } else if (isPasswordInvalid(password)) {
            ChatMessage.Unprocessable.sendTo(player);
        } else {
            endAuth(player);
            data.setPassword(password);
        }

        return 1;
    }

    @Override
    public int handleUpdateRequest(ServerPlayer player, String oldPassword, String newPassword) {
        final var data = PlayerData.of(player);
        final var entry = data.getPassword();

        if (entry.isEmpty()) {
            ChatMessage.NotFound.sendTo(player);
        } else if (!entry.get().matches(oldPassword)) {
            ChatMessage.Unauthorized.sendTo(player);
        } else if (isPasswordInvalid(newPassword)) {
            ChatMessage.Unprocessable.sendTo(player);
        } else {
            data.setPassword(newPassword);
        }

        return 1;
    }

    @Override
    public int handleDeleteRequest(ServerPlayer player, String password) {
        final var data = PlayerData.of(player);
        final var entry = data.getPassword();

        if (entry.isEmpty()) {
            ChatMessage.NotFound.sendTo(player);
        } else if (!entry.get().matches(password)) {
            ChatMessage.Unauthorized.sendTo(player);
        } else {
            data.setPassword(null);
        }

        return 1;
    }

    public boolean isPasswordInvalid(String password) {
        return TekohaAdditions.CONFIG.passwordMinLength > password.length()
                || password.length() > TekohaAdditions.CONFIG.passwordMaxLength;
    }

    public void init() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> ServerPlayerEvents.JOIN.register(this::beginAuth));
    }

    private enum ChatMessage {
        Conflict("conflict", "/tekoha login"),
        Delete("delete", "/tekoha signup"),
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

            this.component =
                    Component.translatable("tekoha.auth.chat." + key, slots).withColor(CommonColors.GRAY);
        }

        public void sendTo(ServerPlayer player) {
            player.sendSystemMessage(this.component);
        }
    }
}
