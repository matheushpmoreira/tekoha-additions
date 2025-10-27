package dev.arzcbnh.minecraft.auth;

import dev.arzcbnh.minecraft.util.ModConfig;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.UUID;

public record PasswordData(UUID uuid, byte[] hash, byte[] salt, int iterations, String algorithm) {
    public boolean satisfies(String password) {
        return Arrays.equals(hash, genHash(password, salt, iterations, hash.length * 8, algorithm));
    }

    public boolean isBlank() {
        return this.satisfies("");
    }

    public static PasswordData fromPassword(UUID uuid, String password) {
        final var config = ModConfig.getInstance();
        final byte[] salt = genSalt(config.passwordSaltLength);
        final byte[] hash = genHash(password, salt, config.passwordHashIterations, config.passwordHashLength, config.passwordHashAlgorithm);
        return new PasswordData(uuid, hash, salt, config.passwordHashIterations, config.passwordHashAlgorithm);
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
            e.printStackTrace();
            return null;
        }
    }
}
