package dev.arzcbnh.tekoha.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.arzcbnh.tekoha.TekohaAdditions;
import dev.arzcbnh.tekoha.auth.AuthService;
import dev.arzcbnh.tekoha.auth.PasswordEntry;
import java.util.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jetbrains.annotations.Nullable;

public class PlayerData extends SavedData {
    public final ServerPlayer player;
    private PasswordEntry password;
    private GameType defaultGameType;
    private AuthService authService;
    private final Set<EquipmentSlot> hiddenEquipment;

    private PlayerData(
            ServerPlayer player,
            @Nullable PasswordEntry password,
            @Nullable GameType gametype,
            @Nullable AuthService service,
            Set<EquipmentSlot> hiddenEquipment) {
        this.player = player;
        this.password = password;
        this.defaultGameType = gametype;
        this.authService = service;
        this.hiddenEquipment = hiddenEquipment;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private PlayerData(
            ServerPlayer player,
            Optional<PasswordEntry> password,
            Optional<GameType> gametype,
            Optional<AuthService> service,
            List<EquipmentSlot> hiddenEquipment) {
        this(
                player,
                password.orElse(null),
                gametype.orElse(null),
                service.orElse(null),
                new HashSet<>(hiddenEquipment));
    }

    private PlayerData(ServerPlayer player) {
        this.player = player;
        this.hiddenEquipment = new HashSet<>();
    }

    public static PlayerData of(ServerPlayer player) {
        final Codec<PlayerData> codec = RecordCodecBuilder.create(instance -> instance.group(
                        PasswordEntry.CODEC.optionalFieldOf("password").forGetter(PlayerData::getPassword),
                        GameType.CODEC.optionalFieldOf("gametype").forGetter(PlayerData::getDefaultGameType),
                        AuthService.CODEC.optionalFieldOf("service").forGetter(PlayerData::getAuthService),
                        EquipmentSlot.CODEC
                                .listOf()
                                .fieldOf("hiddenEquipment")
                                .forGetter(obj -> obj.hiddenEquipment.stream().toList()))
                .apply(
                        instance,
                        (password, gametype, service, equipment) ->
                                new PlayerData(player, password, gametype, service, equipment)));

        final SavedDataType<PlayerData> type = new SavedDataType<>(
                "%s-player-%s".formatted(TekohaAdditions.MOD_ID, player.getUUID()),
                () -> new PlayerData(player),
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

    public Optional<AuthService> getAuthService() {
        return Optional.ofNullable(authService);
    }

    public void setAuthService(@Nullable AuthService service) {
        this.authService = service;
    }

    public boolean isEquipmentHidden(EquipmentSlot slot) {
        return hiddenEquipment.contains(slot);
    }

    public void setEquipmentHidden(EquipmentSlot slot, boolean bl) {
        if (bl) {
            hiddenEquipment.add(slot);
        } else {
            hiddenEquipment.remove(slot);
        }
    }

    public boolean isAuthenticated() {
        return this.defaultGameType == null;
    }
}
