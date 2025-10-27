package dev.arzcbnh.minecraft.util;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

import static dev.arzcbnh.minecraft.TekohaAdditions.MOD_ID;

public class ModConfig {
    private static ModConfig instance;

    public Path modDataPath;

    public String passwordHashAlgorithm;
    public int passwordHashIterations;
    public int passwordHashLength;
    public int passwordSaltLength;
    public int passwordMaxLength;

    private ModConfig() {}

    public static ModConfig getInstance() {
        if (instance == null) {
            instance = new ModConfig();
            instance.load();
        }

        return instance;
    }

    private void load() {
        modDataPath = FabricLoader.getInstance().getGameDir().resolve(MOD_ID);

        passwordHashAlgorithm = "PBKDF2WithHmacSHA256";
        passwordHashIterations = 100000;
        passwordHashLength = 256;
        passwordSaltLength = 16;
        passwordMaxLength = 64;
    }
}
