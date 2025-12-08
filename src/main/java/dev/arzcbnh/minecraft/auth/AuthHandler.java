//package dev.arzcbnh.minecraft.auth;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import net.minecraft.core.Holder;
//import net.minecraft.network.protocol.common.ClientboundClearDialogPacket;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.world.level.GameType;
//
//public class AuthHandler {
//    private static final Map<ServerPlayer, AuthHandler> pending = new ConcurrentHashMap<>();
//
//    private final PropertiesPasswordDatabase database;
//    private final ServerPlayer player;
//    private final GameType gamemode;
//    private final PasswordData data;
//
//    private AuthHandler(ServerPlayer player, PropertiesPasswordDatabase db) {
//        this.database = db;
//        this.player = player;
//        this.gamemode = player.gameMode();
//        this.data = database.retrieve(player.getUUID()).orElse(null);
//    }
//
//    /**
//     * Begins the authentication process for a player, which includes freezing them and prompting for a password.
//     * @param player    the player to authenticate.
//     */
//    public static void begin(ServerPlayer player) {
//        // TODO: invert database dependency
//        final var instance = new AuthHandler(player, PropertiesPasswordDatabase.getInstance());
//        instance.begin();
//    }
//
//    public void begin() {
//        pending.put(player, this);
//
//        if (data == null) {
//            this.deny(AuthFormDialog.Message.SIGNUP);
//        } else if (data.isBlank()) {
//            allow();
//        } else {
//            deny(AuthFormDialog.Message.LOGIN);
//        }
//    }
//
//    /**
//     * Offer a password for the manager to validate against. The player is unfrozen and authenticated if valid,
//     * or prompted again if not. Does nothing if no handler exists for the player.
//     *
//     * @param player    the player to authenticate.
//     * @param password  the offered password.
//     */
//    public static void offer(ServerPlayer player, String password) {
//        final var instance = pending.get(player);
//
//        if (instance != null) {
//            instance.offer(password);
//        }
//    }
//
//    public void offer(String password) {
//        if (data == null) {
//            database.store(PasswordData.fromPassword(player.getUUID(), password));
//            allow();
//        } else if (data.satisfies(password)) {
//            allow();
//        } else {
//            deny(AuthFormDialog.Message.FAIL);
//        }
//    }
//
//    /**
//     * Cancel the authentication process for a player. Useful for returning the player to original game mode without giving them access to the world.
//     * @param player    the player to cancel authentication for.
//     */
//    public static void cancel(ServerPlayer player) {
//        final var instance = pending.get(player);
//
//        if (instance != null) {
//            instance.cancel();
//        }
//    }
//
//    public void cancel() {
//        player.setGameMode(gamemode);
//        pending.remove(player);
//    }
//
////    private void deny(AuthFormDialog.Message message) {
////        player.openDialog(Holder.direct(AuthFormDialog.getInstance(player.getUUID(), message)));
////        player.setGameMode(GameType.SPECTATOR);
////    }
////
////    private void allow() {
////        // ServerPlayerEntity lacks a helper method for clearing dialogs for some reason
////        player.connection.send(ClientboundClearDialogPacket.INSTANCE);
////        player.setGameMode(gamemode);
////        pending.remove(player);
////    }
//}
