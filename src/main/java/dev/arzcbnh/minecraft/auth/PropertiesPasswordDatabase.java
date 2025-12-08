package dev.arzcbnh.minecraft.auth;

import dev.arzcbnh.minecraft.TekohaAdditions;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.rmi.ServerError;
import java.util.Base64;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

public class PropertiesPasswordDatabase implements PasswordDatabase {
    public static final int VERSION = 1;
    private final Path path;

    public PropertiesPasswordDatabase(Path path) {
        this.path = path;
    }

    @Override
    public void store(PasswordData data) {
        final var props = new Properties();

        try (final var in = Files.newInputStream(path)) {
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final var encoder = Base64.getEncoder();
        final String key = data.uuid().toString();
        final String value = String.format("%s:%s:%d:%s", encoder.encodeToString(data.hash()), encoder.encodeToString(data.salt()), data.iterations(), data.algorithm());
        props.setProperty(key, value);

        try (final var out = Files.newOutputStream(path)) {
            props.store(out, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean contains(UUID uuid) {
        final var props = new Properties();

        try (final var in = Files.newInputStream(path)) {
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return props.containsKey(uuid.toString());
    }

    @Override
    public Optional<PasswordData> retrieve(UUID uuid) {
        final var props = new Properties();

        try (final var in = Files.newInputStream(path)) {
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (props.containsKey(uuid.toString())) {
            final var decoder = Base64.getDecoder();
            final String[] data = props.getProperty(uuid.toString()).split(":");
            final byte[] hash = decoder.decode(data[0]);
            final byte[] salt = decoder.decode(data[1]);
            final int iterations = Integer.parseInt(data[2]);
            final String algorithm = data[3];

            return Optional.of(new PasswordData(uuid, hash, salt, iterations, algorithm));
        }

        return Optional.empty();
    }
}
