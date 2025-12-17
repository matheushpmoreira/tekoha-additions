package dev.arzcbnh.tekoha.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.arzcbnh.tekoha.TekohaAdditions;
import dev.arzcbnh.tekoha.auth.PasswordEntry;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jetbrains.annotations.Nullable;

public class PlayerData extends SavedData {
    public final ServerPlayer player;
    private PasswordEntry password;
    private GameType defaultGameType;

    private PlayerData(ServerPlayer player, @Nullable PasswordEntry password, @Nullable GameType defaultGameType) {
        this.player = player;
        this.password = password;
        this.defaultGameType = defaultGameType;
    }

    public static PlayerData of(ServerPlayer player) {
        final Codec<PlayerData> codec = RecordCodecBuilder.create(instance -> instance.group(
                        PasswordEntry.CODEC.optionalFieldOf("password").forGetter(PlayerData::getPassword),
                        GameType.CODEC.optionalFieldOf("defaultGameType").forGetter(PlayerData::getDefaultGameType))
                .apply(
                        instance,
                        (password, gametype) -> new PlayerData(player, password.orElse(null), gametype.orElse(null))));

        final SavedDataType<PlayerData> type = new SavedDataType<>(
                String.format("%s-player-%s", TekohaAdditions.MOD_ID, player.getUUID()),
                () -> new PlayerData(player, null, null),
                codec,
                null);

        return Objects.requireNonNull(player.level().getServer().getLevel(ServerLevel.OVERWORLD))
                .getDataStorage()
                .computeIfAbsent(type);
    }

    public Optional<PasswordEntry> getPassword() {
        return Optional.ofNullable(password);
    }

    public void setPassword(@Nullable String password) {
        this.password = password == null ? null : PasswordEntry.of(password);
        this.setDirty();
    }

    public Optional<GameType> getDefaultGameType() {
        return Optional.ofNullable(defaultGameType);
    }

    public void setDefaultGameType(@Nullable GameType defaultGameType) {
        this.defaultGameType = defaultGameType;
        this.setDirty();
    }

    public boolean isAuthenticated() {
        return this.defaultGameType == null;
    }
}
