package dev.arzcbnh.minecraft.mixin;

import dev.arzcbnh.minecraft.TekohaAdditions;
import dev.arzcbnh.minecraft.auth.AuthHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.UUID;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Shadow
    public abstract PlayerManager getPlayerManager();

    @Inject(method = "handleCustomClickAction", at = @At("HEAD"))
    private void handleCustomClickAction(Identifier id, Optional<NbtElement> payload, CallbackInfo ci) {
        if (!id.getNamespace().equals(TekohaAdditions.MOD_ID) || payload.isEmpty()) {
            return;
        }

        final var nbt = payload.get().asCompound().orElseGet(NbtCompound::new);

        switch (id.getPath()) {
            case "auth":
                try {
                    final var uuid = UUID.fromString(nbt.getString("uuid", ""));
                    final var player = this.getPlayerManager().getPlayer(uuid);
                    AuthHandler.offer(player, nbt.getString("password", ""));
                } catch (IllegalArgumentException e) {
                    TekohaAdditions.LOGGER.warn("Invalid UUID {} in authentication custom action", nbt.getString("uuid", ""));
                }
        }
    }
}
