package dev.arzcbnh.minecraft;

import dev.arzcbnh.minecraft.auth.AuthHandler;
import dev.arzcbnh.minecraft.util.ModConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;

public class TekohaAdditions implements ModInitializer {
    public static final String MOD_ID = "tekoha";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {

//
//        if (!MOD_DATA_PATH.toFile().exists()) {
//            MOD_DATA_PATH.toFile().mkdirs();
//        }
//
//        if (!AUTH_FILE_PATH.toFile().exists()) {
//            try {
//                AUTH_FILE_PATH.toFile().createNewFile();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        // create data dir
        try {
            Files.createDirectory(ModConfig.getInstance().modDataPath);
        } catch (FileAlreadyExistsException e) {
            // Ignore
        } catch (IOException e) {
            throw new RuntimeException("Could not create data directory", e);
        }

        ServerPlayerEvents.JOIN.register(AuthHandler::begin);
        ServerPlayerEvents.LEAVE.register(AuthHandler::cancel);
    }
}
