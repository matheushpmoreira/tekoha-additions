package dev.arzcbnh.tekoha.auth;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.arzcbnh.tekoha.TekohaAdditions;
import dev.arzcbnh.tekoha.util.ModConfig;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.*;

public record PasswordEntry(byte[] hash, byte[] salt, int iterations, String algorithm) {
    public static final Codec<byte[]> BYTE_ARR_CODEC = Codec.STRING.xmap(Base64.getDecoder()::decode, Base64.getEncoder()::encodeToString);
    public static final Codec<PasswordEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BYTE_ARR_CODEC.fieldOf("hash").forGetter(PasswordEntry::hash),
            BYTE_ARR_CODEC.fieldOf("salt").forGetter(PasswordEntry::salt),
            Codec.INT.fieldOf("iterations").forGetter(PasswordEntry::iterations),
            Codec.STRING.fieldOf("algorithm").forGetter(PasswordEntry::algorithm)
    ).apply(instance, PasswordEntry::new));

    public boolean matches(String password) {
        return Arrays.equals(hash, genHash(password, salt, iterations, hash.length * 8, algorithm));
    }

    public static PasswordEntry of(String password) {
        return of(password, TekohaAdditions.CONFIG);
    }

    public static PasswordEntry of(String password, ModConfig config) {
        return of(password, config.passwordHashLength, config.passwordSaltLength, config.passwordHashIterations, config.passwordHashAlgorithm);
    }

    public static PasswordEntry of(String password, int hashLength, int saltLength, int iterations, String algorithm) {
        final byte[] salt = genSalt(saltLength);
        final byte[] hash = genHash(password, salt, iterations, hashLength, algorithm);

        return new PasswordEntry(hash, salt, iterations, algorithm);
    }

    private static byte[] genSalt(int saltLength) {
        final var random = new SecureRandom();
        final byte[] salt = new byte[saltLength];
        random.nextBytes(salt);

        return salt;
    }

    private static byte[] genHash(String password, byte[] salt, int iterations, int hashLength, String algorithm) {
        try {
            final KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, hashLength);
            final SecretKeyFactory factory = SecretKeyFactory.getInstance(algorithm);
            return factory.generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
