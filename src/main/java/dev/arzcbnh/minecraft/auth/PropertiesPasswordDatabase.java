package dev.arzcbnh.minecraft.auth;

import dev.arzcbnh.minecraft.TekohaAdditions;
import dev.arzcbnh.minecraft.util.ModConfig;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

/**
 * A password database that stores passwords in a properties file.
 */
// TODO: Invert dependency on passwords file path and don't make it a singleton
public class PropertiesPasswordDatabase implements PasswordDatabase {
    private static final Path AUTH_FILE_PATH = ModConfig.getInstance().modDataPath.resolve("passwords.properties");
    private static final PropertiesPasswordDatabase instance = new PropertiesPasswordDatabase();

    private PropertiesPasswordDatabase() {
        // TODO: Maybe don't do this in the constructor
        // Ensure passwords file exists
        try {
            Files.createFile(AUTH_FILE_PATH);
        } catch (FileAlreadyExistsException e) {
            // Do nothing
        } catch (IOException e) {
            TekohaAdditions.LOGGER.error("Could not create passwords file, will prevent players from logging in", e);
        }
    }

    public static PropertiesPasswordDatabase getInstance() {
        return instance;
    }

    @Override
    public void store(PasswordData data) {
        final var props = new Properties();

        try (final var reader = new FileInputStream(AUTH_FILE_PATH.toFile())) {
            props.load(reader);
        } catch (IOException e) {
            TekohaAdditions.LOGGER.error("Could not read from passwords file", e);
        }

        final String key = data.uuid().toString();
        final String value = String.format("%s:%s:%d:%s", Hex.encodeHexString(data.hash()), Hex.encodeHexString(data.salt()), data.iterations(), data.algorithm());
        props.setProperty(key, value);

        try (final var writer = new FileOutputStream(AUTH_FILE_PATH.toFile())) {
            props.store(writer, null);
        } catch (IOException e) {
            TekohaAdditions.LOGGER.error("Could not write to passwords file", e);
        }
    }

    @Override
    public Optional<PasswordData> retrieve(UUID uuid) {
        try (FileReader reader = new FileReader(AUTH_FILE_PATH.toFile())) {
            final var props = new Properties();
            props.load(reader);

            final String[] data = props.getProperty(uuid.toString()).split(":");
            final byte[] hash = Hex.decodeHex(data[0]);
            final byte[] salt = Hex.decodeHex(data[1]);
            final int iterations = Integer.parseInt(data[2]);
            final String algorithm = data[3];

            return Optional.of(new PasswordData(uuid, hash, salt, iterations, algorithm));
        } catch (NullPointerException e) {
            TekohaAdditions.LOGGER.debug("No password data stored for {}", uuid);
        } catch (ArrayIndexOutOfBoundsException | DecoderException e) {
            TekohaAdditions.LOGGER.error("Failed to parse password data for {}", uuid, e);
        } catch (IOException e) {
            TekohaAdditions.LOGGER.error("Could not read from passwords file", e);
        }

        return Optional.empty();
    }
}
