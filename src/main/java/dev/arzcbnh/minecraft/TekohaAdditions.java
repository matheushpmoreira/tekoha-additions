package dev.arzcbnh.minecraft;

import dev.arzcbnh.minecraft.auth.AuthService;
import dev.arzcbnh.minecraft.auth.LoginRequestCallback;
import dev.arzcbnh.minecraft.auth.PropertiesPasswordDatabase;
import dev.arzcbnh.minecraft.util.ModConfig;
import dev.arzcbnh.minecraft.util.RegistryDialogProvider;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.registries.Registries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

//        System.out.println("I'm initialized somehow");
        // create data dir
//        try {
//            Files.createDirectory(ModConfig.getInstance().modDataPath);
//        } catch (FileAlreadyExistsException e) {
//            // Ignore
//        } catch (IOException e) {
//            throw new RuntimeException("Could not create data directory", e);
//        }

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            final var database = new PropertiesPasswordDatabase();
            final var provider = new RegistryDialogProvider(server.registryAccess().lookupOrThrow(Registries.DIALOG));
            final var service = new AuthService(database, provider, ModConfig.load());

            ServerPlayerEvents.JOIN.register(service::onJoin);
            ServerPlayerEvents.LEAVE.register(service::onLeave);
            LoginRequestCallback.EVENT.register(service::onRequest);
        });

    }
}
