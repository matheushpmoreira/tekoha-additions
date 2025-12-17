package dev.arzcbnh.tekoha;

import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

import java.lang.reflect.Method;

public class TekohaAdditionsClientGameTest implements CustomTestMethodInvoker {
//    public Player player;

    @Override
    public void invokeTestMethod(GameTestHelper helper, Method method) throws ReflectiveOperationException {
//        if (this.player == null) {
//            this.player = helper.makeMockPlayer(GameType.CREATIVE);
//        }
    }

//    @GameTest
//    public void whenJoin_freezePlayer(GameTestHelper helper) {
//        this.player = helper.makeMockPlayer(GameType.CREATIVE);
//    }
//
//    @GameTest
//    public void whenLoginWithoutRegister_thenNotFoundError(GameTestHelper helper) {
//        helper.
//    }
}
