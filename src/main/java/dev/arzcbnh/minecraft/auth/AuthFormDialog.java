//package dev.arzcbnh.minecraft.auth;
//
//import dev.arzcbnh.minecraft.TekohaAdditions;
//import dev.arzcbnh.minecraft.util.ModConfig;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.server.dialog.ActionButton;
//import net.minecraft.server.dialog.CommonButtonData;
//import net.minecraft.server.dialog.CommonDialogData;
//import net.minecraft.server.dialog.Dialog;
//import net.minecraft.server.dialog.DialogAction;
//import net.minecraft.server.dialog.Input;
//import net.minecraft.server.dialog.NoticeDialog;
//import net.minecraft.server.dialog.action.CustomAll;
//import net.minecraft.server.dialog.body.PlainMessage;
//import net.minecraft.server.dialog.input.TextInput;
//
//public class AuthFormDialog {
//    public static final Dialog FAIL = createInstance("fail");
//    public static final Dialog LOGIN = createInstance("login");
//    public static final Dialog SIGNUP = createInstance("signup");
//
//    private static final Component title = Component.translatable(String.format("dialog.%s.auth.title", TekohaAdditions.MOD_ID));
//    private static final Component passwordInputLabel = Component.translatable(String.format("dialog.%s.auth.input.password", TekohaAdditions.MOD_ID));
//    private static final Component confirmButtonLabel = Component.translatable(String.format("dialog.%s.auth.button.confirm", TekohaAdditions.MOD_ID));
//
//    private static Dialog createInstance(String key) {
//        final var messageBody = new PlainMessage(Component.translatable(String.format("dialog.%s.auth.%s", TekohaAdditions.MOD_ID, key)), PlainMessage.DEFAULT_WIDTH);
//        final var passwordInput = new Input("password", new TextInput(CommonButtonData.DEFAULT_WIDTH, passwordInputLabel, true, "", ModConfig.getInstance().passwordMaxLength, Optional.empty()));
//        final var common = new CommonDialogData(title, Optional.empty(), false, false, DialogAction.NONE, List.of(messageBody), List.of(passwordInput));
//
//        final var additions = new CompoundTag();
//        additions.putString("uuid", uuid.toString());
//
//        final var confirmAction = new CustomAll(ResourceLocation.fromNamespaceAndPath(TekohaAdditions.MOD_ID, "auth"), Optional.of(additions));
//        final var confirmButton = new ActionButton(new CommonButtonData(confirmButtonLabel, CommonButtonData.DEFAULT_WIDTH), Optional.of(confirmAction));
//
//        return new NoticeDialog(common, confirmButton);
//        ;
//    }
//}
