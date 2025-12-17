package dev.arzcbnh.tekoha.auth;

public class AuthFormDialog {
//    public static final String LOGIN_PATH = "auth/login";
//    public static final String SIGNUP_PATH = "auth/signup";
//    public static final String PASSWORD_INPUT_KEY = "password";
//
//    private static MutableComponent createComponent(String id) {
//        return Component.translatable(String.format("dialog.%s.auth.%s", TekohaAdditions.MOD_ID, id));
//    }
//
//    private static Dialog createInstance(List<DialogBody> body, String path, int passwordMaxLength) {
//        final var passwordInput = new Input(PASSWORD_INPUT_KEY, new TextInput(CommonButtonData.DEFAULT_WIDTH, createComponent("password"), true, "", passwordMaxLength, Optional.empty()));
//        final var common = new CommonDialogData(createComponent("title"), Optional.empty(), false, false, DialogAction.NONE, body, List.of(passwordInput));
//
//        final var confirmAction = new CustomAll(ResourceLocation.fromNamespaceAndPath(TekohaAdditions.MOD_ID, path), Optional.empty());
//        final var confirmButton = new ActionButton(new CommonButtonData(createComponent("confirm"), CommonButtonData.DEFAULT_WIDTH), Optional.of(confirmAction));
//
//        return new NoticeDialog(common, confirmButton);
//    }
//
//    public static Dialog of(Type type, int passwordMaxLength) {
//        if (type == Type.LOGIN) {
//            return createInstance(List.of(type.body), "auth/login", passwordMaxLength);
//        } else if (type == Type.SIGNUP) {
//            return createInstance(List.of(type.body), "auth/signup", passwordMaxLength);
//        } else if (type == Type.LOGIN_FAIL) {
//            return createInstance(List.of(type.body), "auth/login", passwordMaxLength);
//        } else {
//            return createInstance(List.of(type.body), "auth/signup", passwordMaxLength);
//        }
//    }
//
//    public enum Type {
//        LOGIN,
//        SIGNUP,
//        LOGIN_FAIL,
//        SIGNUP_FAIL;
//
//        public final List<DialogBody> body;
//
//        Type(String langKey, boolean isError) {
//            final var component = createComponent(langKey);
//
//            if (isError) {
//                component.withColor(CommonColors.RED);
//            }
//
//            this.body = new PlainMessage(component, PlainMessage.DEFAULT_WIDTH);
//        }
//    }
//
//    public enum InputError {
//        MISMATCH("mismatch"),
//        INVALID("invalid");
//
//        public final PlainMessage body;
//
//        InputError(String id) {
//        }
//    }
}
