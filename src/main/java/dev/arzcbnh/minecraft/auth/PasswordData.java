package dev.arzcbnh.minecraft.auth;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.arzcbnh.minecraft.util.ModConfig;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public record PasswordData(UUID uuid, byte[] hash, byte[] salt, int iterations, String algorithm) {
//public class PasswordData extends SavedData {
//    public static final Codec<byte[]> BYTE_ARR_CODEC = Codec.STRING.xmap(Base64.getDecoder()::decode, Base64.getEncoder()::encodeToString);
//    public static final Codec<PasswordData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
//            UUIDUtil.CODEC.fieldOf("uuid").forGetter(obj -> obj.uuid),
//            BYTE_ARR_CODEC.fieldOf("hash").forGetter(obj -> obj.hash),
//            BYTE_ARR_CODEC.fieldOf("salt").forGetter(obj -> obj.salt),
//            Codec.INT.fieldOf("iterations").forGetter(obj -> obj.iterations),
//            Codec.STRING.fieldOf("algorithm").forGetter(obj -> obj.algorithm)
//    ).apply(instance, PasswordData::new));
//
//    public PasswordData(UUID uuid, byte[] hash, byte[] salt, int iterations, String algorithm) {
//        this.uuid = uuid;
//        this.hash = hash;
//        this.salt = salt;
//        this.iterations = iterations;
//        this.algorithm = algorithm;
//    }
//    private final byte[
    
    public boolean satisfies(String password) {
        return Arrays.equals(hash, genHash(password, salt, iterations, hash.length * 8, algorithm));
    }

    public static PasswordData fromPassword(UUID uuid, String password, int hashLength, int saltLength, int iterations, String algorithm) {
        final byte[] salt = genSalt(saltLength);
        final byte[] hash = genHash(password, salt, iterations, hashLength, algorithm);
        return new PasswordData(uuid, hash, salt, iterations, algorithm);
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
