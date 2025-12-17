package dev.arzcbnh.tekoha.auth;

//import static dev.arzcbnh.minecraft.auth.AuthFormDialog.InputError.*;
//import static dev.arzcbnh.minecraft.auth.AuthFormDialog.Type.*;

public class DialogAuthService {
//public class DialogAuthService implements AuthService {
//    private final Map<ServerPlayer, GameType> previousGameMode = new ConcurrentHashMap<>();
//    private final Map<ServerPlayer, PasswordData> playerData = new ConcurrentHashMap<>();
//    private final PropertiesPasswordDatabase database;
//    private final ModConfig config;
//
//    private final Holder<Dialog> FAIL_DIALOG;
//    private final Holder<Dialog> INVALID_DIALOG;
//    private final Holder<Dialog> LOGIN_DIALOG;
//    private final Holder<Dialog> SIGNUP_DIALOG;
//
//    public DialogAuthService(PropertiesPasswordDatabase database, DialogProvider provider, ModConfig config) {
//        this.database = database;
//        this.config = config;
//
//        this.FAIL_DIALOG = Holder.direct(provider.get(String.format("%s:login/fail", TekohaAdditions.MOD_ID)));
//        this.INVALID_DIALOG = Holder.direct(provider.get(String.format("%s:login/invalid", TekohaAdditions.MOD_ID)));
//        this.LOGIN_DIALOG = Holder.direct(provider.get(String.format("%s:login/login", TekohaAdditions.MOD_ID)));
//        this.SIGNUP_DIALOG = Holder.direct(provider.get(String.format("%s:login/signup", TekohaAdditions.MOD_ID)));
//    }
//
//    @Override
//    public void beginAuthProcess(ServerPlayer player) {
//        capturePlayerState(player);
//        player.openDialog(retrievePlayerData(player) != null ? LOGIN_DIALOG : SIGNUP_DIALOG);
//    }
//
//    @Override
//    public void cancelAuthProcess(ServerPlayer player) {
//        restorePlayerState(player);
//        clearPlayerData(player);
//    }
//
//    @Override
//    public void completeAuthProcess(ServerPlayer player) {
//        restorePlayerState(player);
//        clearPlayerData(player);
//    }
//
//    @Override
//    public void handleLoginRequest(ServerPlayer player, String password) {
//        final var data = retrievePlayerData(player);
//
//        if (data == null) {
//            player.openDialog(getDialog(SIGNUP));
//        } else if (!data.satisfies(password)){
//            player.openDialog(getDialog(LOGIN, MISMATCH));
//        } else {
//            completeAuthProcess(player);
//        }
//    }
//
//    @Override
//    public void handleSignupRequest(ServerPlayer player, String password) {
//        final var data = retrievePlayerData(player);
//
//        if (data != null) {
//            player.openDialog(getDialog(LOGIN));
//        } else if (password.isBlank() || password.length() > config.passwordMaxLength) {
//            player.openDialog(getDialog(SIGNUP, INVALID));
//        } else {
//            registerPlayer(player, password);
//            completeAuthProcess(player);
//        }
//    }
//
//    private void capturePlayerState(ServerPlayer player) {
//        previousGameMode.put(player, player.gameMode());
//        player.setGameMode(GameType.SPECTATOR);
//    }
//
//    private void restorePlayerState(ServerPlayer player) {
//        Optional.ofNullable(previousGameMode.remove(player)).ifPresent(player::setGameMode);
//        player.connection.send(ClientboundClearDialogPacket.INSTANCE);
//    }
//
//    private PasswordData retrievePlayerData(ServerPlayer player) {
//        return playerData.computeIfAbsent(player, p -> database.retrieve(p.getUUID()).orElse(null));
//    }
//
//    private void clearPlayerData(ServerPlayer player) {
//        playerData.remove(player);
//    }
//
//    private void registerPlayer(ServerPlayer player, String password) {
//        final var data = PasswordData.fromPassword(
//                player.getUUID(), password,
//                config.passwordHashLength,
//                config.passwordSaltLength,
//                config.passwordHashIterations,
//                config.passwordHashAlgorithm
//        );
//
//        database.store(data);
//    }
//
//    private Holder<Dialog> getDialog(AuthFormDialog.Type type) {
//        return Holder.direct(AuthFormDialog.of(type, config.passwordMaxLength));
//    }
//
//    private Holder<Dialog> getDialog(AuthFormDialog.Type type, AuthFormDialog.InputError error) {
//        return Holder.direct(AuthFormDialog.of(type, error, config.passwordMaxLength));
//    }
}
