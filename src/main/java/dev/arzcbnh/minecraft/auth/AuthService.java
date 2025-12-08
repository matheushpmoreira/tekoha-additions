package dev.arzcbnh.minecraft.auth;

import dev.arzcbnh.minecraft.TekohaAdditions;
import dev.arzcbnh.minecraft.util.DialogProvider;
import dev.arzcbnh.minecraft.util.ModConfig;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.common.ClientboundClearDialogPacket;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuthService {
    private final Map<ServerPlayer, AuthProcess> ongoing = new ConcurrentHashMap<>();
    private final PropertiesPasswordDatabase database;
    private final ModConfig config;

    private final Holder<Dialog> FAIL_DIALOG;
    private final Holder<Dialog> INVALID_DIALOG;
    private final Holder<Dialog> LOGIN_DIALOG;
    private final Holder<Dialog> SIGNUP_DIALOG;

    public AuthService(PropertiesPasswordDatabase database, DialogProvider provider, ModConfig config) {
        this.database = database;
        this.config = config;

        this.FAIL_DIALOG = Holder.direct(provider.get(String.format("%s:login/fail", TekohaAdditions.MOD_ID)));
        this.INVALID_DIALOG = Holder.direct(provider.get(String.format("%s:login/invalid", TekohaAdditions.MOD_ID)));
        this.LOGIN_DIALOG = Holder.direct(provider.get(String.format("%s:login/login", TekohaAdditions.MOD_ID)));
        this.SIGNUP_DIALOG = Holder.direct(provider.get(String.format("%s:login/signup", TekohaAdditions.MOD_ID)));
    }

    public void onJoin(ServerPlayer player) {
        if (ongoing.containsKey(player)) {
            return;
        }

        final var proc = new AuthProcess(player, player.gameMode(), database.retrieve(player.getUUID()).orElse(null));
        player.openDialog(proc.data() == null ? SIGNUP_DIALOG : LOGIN_DIALOG);
        ongoing.put(player, proc);
    }

    public void onLeave(ServerPlayer player) {
        if (!ongoing.containsKey(player)) {
            return;
        }

        final var gamemode = ongoing.remove(player).gamemode();
        player.setGameMode(gamemode);
    }

    public void onRequest(ServerPlayer player, String password) {
        if (!ongoing.containsKey(player)) {
            return;
        }

        final var data = ongoing.get(player).data();

        if (password.isBlank() || password.length() > 64) {
            player.openDialog(INVALID_DIALOG);
        } else if (data == null) {
            database.store(PasswordData.fromPassword(player.getUUID(), password, config));
            onSuccess(player);
        } else if (data.satisfies(password)){
            onSuccess(player);
        } else {
            player.openDialog(FAIL_DIALOG);
        }
    }

    private void onSuccess(ServerPlayer player) {
        player.connection.send(ClientboundClearDialogPacket.INSTANCE);
        player.setGameMode(ongoing.remove(player).gamemode());
    }
}
