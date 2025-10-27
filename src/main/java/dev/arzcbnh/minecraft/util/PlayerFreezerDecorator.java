package dev.arzcbnh.minecraft.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

public class PlayerFreezerDecorator {
    public final ServerPlayerEntity player;

    private boolean isFrozen;
    private float movementSpeed;
    private GameMode gamemode;

    public PlayerFreezerDecorator(ServerPlayerEntity player) {
        this.player = player;
    }

    public void freeze() {
        if (this.isFrozen) {
            return;
        }

        this.movementSpeed = this.player.getMovementSpeed();
        this.gamemode = this.player.getGameMode();

        this.isFrozen = true;
        player.setMovementSpeed(0.0F);
//        player.changeGameMode(GameMode.SPECTATOR);
    }

    public void unfreeze() {
        if (!this.isFrozen) {
            return;
        }

        this.isFrozen = false;
//        player.setMovementSpeed(this.movementSpeed);
        player.changeGameMode(this.gamemode);
    }
}
