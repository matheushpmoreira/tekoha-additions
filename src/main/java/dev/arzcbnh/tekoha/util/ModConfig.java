package dev.arzcbnh.tekoha.util;

import dev.arzcbnh.tekoha.TekohaAdditions;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

public class ModConfig {
    public final String passwordHashAlgorithm;
    public final int passwordHashIterations;
    public final int passwordHashLength;
    public final int passwordSaltLength;
    public final int passwordMaxLength;
    public final int passwordMinLength;

    public ModConfig(Properties props) {
        this.passwordHashAlgorithm = props.getProperty("passwordHashAlgorithm", "PBKDF2WithHmacSHA256");
        this.passwordHashIterations = Integer.parseInt(props.getProperty("passwordHashIterations", "100000"));
        this.passwordHashLength = Integer.parseInt(props.getProperty("passwordHashLength", "256"));
        this.passwordSaltLength = Integer.parseInt(props.getProperty("passwordSaltLength", "16"));
        this.passwordMaxLength = Integer.parseInt(props.getProperty("passwordMaxLength", "64"));
        this.passwordMinLength = Integer.parseInt(props.getProperty("passwordMinLength", "6"));
    }

    public static ModConfig load() {
//        final var filename = TekohaAdditions.MOD_ID + ".properties";
        final var path = FabricLoader.getInstance().getConfigDir().resolve(TekohaAdditions.MOD_ID + ".properties");
//        final var file = path.toFile();
        final var props = new Properties();

//        if (!file.exists()) {
//            try (final var in = TekohaAdditions.class.getResourceAsStream("/tekoha.properties")) {
//                Files.copy(Objects.requireNonNull(in), path, StandardCopyOption.REPLACE_EXISTING);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }

        try (final var in = Files.newInputStream(path)) {
            props.load(in);
        } catch (FileNotFoundException e) {
            // Ignore
        } catch (IOException e) {
            TekohaAdditions.LOGGER.error("Unable to read configuration file, will use defaults", e);
        }

        return new ModConfig(props);
    }
}
