package dev.arzcbnh.minecraft.auth;

import dev.arzcbnh.minecraft.TekohaAdditions;
import dev.arzcbnh.minecraft.util.ModConfig;
import net.minecraft.dialog.AfterAction;
import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.DialogButtonData;
import net.minecraft.dialog.DialogCommonData;
import net.minecraft.dialog.action.DynamicCustomDialogAction;
import net.minecraft.dialog.body.ItemDialogBody;
import net.minecraft.dialog.body.PlainMessageDialogBody;
import net.minecraft.dialog.input.TextInputControl;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.dialog.type.DialogInput;
import net.minecraft.dialog.type.NoticeDialog;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class AuthFormDialog {
    private static final Text title = Text.translatable(String.format("dialog.%s.auth.title", TekohaAdditions.MOD_ID));
    private static final Text passwordInputLabel = Text.translatable(String.format("dialog.%s.auth.input.password", TekohaAdditions.MOD_ID));
    private static final Text confirmButtonLabel = Text.translatable(String.format("dialog.%s.auth.button.confirm", TekohaAdditions.MOD_ID));

    private AuthFormDialog() {}

    public static Dialog getInstance(UUID uuid, Message message) {
        final var messageBody = new PlainMessageDialogBody(Text.translatable(message.key), PlainMessageDialogBody.DEFAULT_WIDTH);
        final var passwordInput = new DialogInput("password", new TextInputControl(DialogButtonData.DEFAULT_WIDTH, passwordInputLabel, true, "", ModConfig.getInstance().passwordMaxLength, Optional.empty()));
        final var common = new DialogCommonData(title, Optional.empty(), false, false, AfterAction.NONE, List.of(messageBody), List.of(passwordInput));

        final var additions = new NbtCompound();
        additions.putString("uuid", uuid.toString());

        final var confirmAction = new DynamicCustomDialogAction(Identifier.of(TekohaAdditions.MOD_ID, "auth"), Optional.of(additions));
        final var confirmButton = new DialogActionButtonData(new DialogButtonData(confirmButtonLabel, DialogButtonData.DEFAULT_WIDTH), Optional.of(confirmAction));

        return new NoticeDialog(common, confirmButton);
    }

    public enum Message {
        SIGNUP(String.format("dialog.%s.auth.signup", TekohaAdditions.MOD_ID)),
        LOGIN(String.format("dialog.%s.auth.login", TekohaAdditions.MOD_ID)),
        FAIL(String.format("dialog.%s.auth.fail", TekohaAdditions.MOD_ID));

        public final String key;

        Message(String key) {
            this.key = key;
        }
    }
}
