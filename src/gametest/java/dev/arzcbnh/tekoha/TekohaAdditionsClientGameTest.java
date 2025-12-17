package dev.arzcbnh.tekoha;

import java.lang.reflect.Method;
import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.minecraft.gametest.framework.GameTestHelper;

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
