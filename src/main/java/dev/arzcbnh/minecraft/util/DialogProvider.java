package dev.arzcbnh.minecraft.util;

import net.minecraft.server.dialog.Dialog;

public interface DialogProvider {
    Dialog get(String path);
}
