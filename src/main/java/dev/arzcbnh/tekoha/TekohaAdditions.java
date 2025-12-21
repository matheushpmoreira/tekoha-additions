package dev.arzcbnh.tekoha;

import dev.arzcbnh.tekoha.auth.*;
import dev.arzcbnh.tekoha.misc.ArmorCommands;
import dev.arzcbnh.tekoha.util.ModConfig;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TekohaAdditions implements ModInitializer {
    public static final String MOD_ID = "tekoha";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final ModConfig CONFIG = ModConfig.load();

    @Override
    public void onInitialize() {
        AuthCommands.init();
        ArmorCommands.init();
    }
}
