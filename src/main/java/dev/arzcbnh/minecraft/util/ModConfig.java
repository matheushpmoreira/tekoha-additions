package dev.arzcbnh.minecraft.util;

import dev.arzcbnh.minecraft.TekohaAdditions;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Properties;

import static dev.arzcbnh.minecraft.TekohaAdditions.MOD_ID;

public class ModConfig {
//    public final Path modDataPath = FabricLoader.getInstance().getGameDir().resolve(MOD_ID);

    public final String passwordHashAlgorithm;
    public final int passwordHashIterations;
    public final int passwordHashLength;
    public final int passwordSaltLength;
    public final int passwordMaxLength;

    public ModConfig(Properties props) {
        this.passwordHashAlgorithm = props.getProperty("passwordHashAlgorithm");
        this.passwordHashIterations = Integer.parseInt(props.getProperty("passwordHashIterations"));
        this.passwordHashLength = Integer.parseInt(props.getProperty("passwordHashLength"));
        this.passwordSaltLength = Integer.parseInt(props.getProperty("passwordSaltLength"));
        this.passwordMaxLength = Integer.parseInt(props.getProperty("passwordMaxLength"));
    }

    public static ModConfig load() {
        final var filename = MOD_ID + ".properties";
        final var path = FabricLoader.getInstance().getConfigDir().resolve(filename);
        final var file = path.toFile();
        final var props = new Properties();

        if (!file.exists()) {
            try (final var in = TekohaAdditions.class.getResourceAsStream(filename)) {
                Files.copy(Objects.requireNonNull(in), path, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (final var in = Files.newInputStream(path)) {
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ModConfig(props);
    }
}
