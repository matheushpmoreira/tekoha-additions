package dev.arzcbnh.minecraft.auth;

import java.util.Optional;
import java.util.UUID;

public interface PasswordDatabase {
    void store(PasswordData data);
    boolean contains (UUID uuid);
    Optional<PasswordData> retrieve(UUID uuid);
}
