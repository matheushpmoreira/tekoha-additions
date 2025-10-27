package dev.arzcbnh.minecraft.auth;

import java.util.Optional;
import java.util.UUID;

public interface PasswordDatabase {
    /**
     * Stores the given password data.
     *
     * @param data  the password data to store.
     */
    void store(PasswordData data);

    /**
     * Retrieves the password data for the given UUID.
     *
     * @param uuid  the player UUID.
     * @return an {@link Optional} with the stored password data, or an empty one if no data is stored.
     */
    Optional<PasswordData> retrieve(UUID uuid);
}
