package dev.arzcbnh.minecraft.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public class PlayerFreezerDecorator {
    public final ServerPlayer player;

    private boolean isFrozen;
    private float movementSpeed;
    private GameType gamemode;

    public PlayerFreezerDecorator(ServerPlayer player) {
        this.player = player;
    }

    public void freeze() {
        if (this.isFrozen) {
            return;
        }

        this.movementSpeed = this.player.getSpeed();
        this.gamemode = this.player.gameMode();

        this.isFrozen = true;
        player.setSpeed(0.0F);
//        player.changeGameMode(GameMode.SPECTATOR);
    }

    public void unfreeze() {
        if (!this.isFrozen) {
            return;
        }

        this.isFrozen = false;
//        player.setMovementSpeed(this.movementSpeed);
        player.setGameMode(this.gamemode);
    }
}
