package dev.arzcbnh.minecraft;

import dev.arzcbnh.minecraft.auth.*;
import dev.arzcbnh.minecraft.command.TekohaCommands;
import dev.arzcbnh.minecraft.util.ModConfig;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TekohaAdditions implements ModInitializer {
    public static final String MOD_ID = "tekoha";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final ModConfig CONFIG = ModConfig.load();

    @Override
    public void onInitialize() {
        TekohaCommands.init(new ChatAuthService());
//        new AuthServiceImpl().init();
    }
}
