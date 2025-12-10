package dev.arzcbnh.minecraft;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.arzcbnh.minecraft.auth.*;
import dev.arzcbnh.minecraft.util.ModConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.impl.attachment.AttachmentPersistentState;
import net.fabricmc.loader.impl.game.minecraft.launchwrapper.FabricClientTweaker;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.SnbtGrammar;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.TellRawCommand;
import net.minecraft.util.parsing.packrat.commands.CommandArgumentParser;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TekohaAdditions implements ModInitializer {
    public static final String MOD_ID = "tekoha";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final ModConfig CONFIG = ModConfig.load();

    @Override
    public void onInitialize() {
        new ChatAuthService().init();
    }
}
