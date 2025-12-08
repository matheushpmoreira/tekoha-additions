package dev.arzcbnh.minecraft.util;

import dev.arzcbnh.minecraft.TekohaAdditions;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dialog.Dialog;

import java.util.Objects;

public class RegistryDialogProvider implements DialogProvider {
    private final Registry<Dialog> registry;

    public RegistryDialogProvider(Registry<Dialog> registry) {
        this.registry = registry;
    }

    @Override
    public Dialog get(String path) {
        try {
            return Objects.requireNonNull(registry.get(ResourceLocation.parse(path)).orElse(null)).value();
        } catch (NullPointerException e) {
            TekohaAdditions.LOGGER.error("Unable to locate dialog with path {}", path, e);
        }

        return null;
    }
}
