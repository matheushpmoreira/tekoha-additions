package dev.arzcbnh.tekoha.misc;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.arzcbnh.tekoha.data.PlayerData;
import java.util.Objects;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;

public class ArmorCommands {
    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, build, selection) -> {
            dispatcher.register(Commands.literal("tekoha")
                    .then(Commands.literal("armor")
                            .then(Commands.literal("head")
                                    .then(Commands.argument("hidden", BoolArgumentType.bool())
                                            .requires(CommandSourceStack::isPlayer)
                                            .executes(ArmorCommands::setHeadVisibility)))
                            .then(Commands.literal("chest")
                                    .then(Commands.argument("hidden", BoolArgumentType.bool())
                                            .requires(CommandSourceStack::isPlayer)
                                            .executes(ArmorCommands::setChestVisibility)))
                            .then(Commands.literal("legs")
                                    .then(Commands.argument("hidden", BoolArgumentType.bool())
                                            .requires(CommandSourceStack::isPlayer)
                                            .executes(ArmorCommands::setLegsVisibility)))
                            .then(Commands.literal("feet")
                                    .then(Commands.argument("hidden", BoolArgumentType.bool())
                                            .requires(CommandSourceStack::isPlayer)
                                            .executes(ArmorCommands::setFeetVisibility)))));
        });
    }

    private static int setHeadVisibility(CommandContext<CommandSourceStack> context) {
        final var player = Objects.requireNonNull(context.getSource().getPlayer());
        final var hidden = BoolArgumentType.getBool(context, "hidden");
        setEquipmentHidden(player, EquipmentSlot.HEAD, hidden);
        return 1;
    }

    private static int setChestVisibility(CommandContext<CommandSourceStack> context) {
        final var player = Objects.requireNonNull(context.getSource().getPlayer());
        final var hidden = BoolArgumentType.getBool(context, "hidden");
        setEquipmentHidden(player, EquipmentSlot.CHEST, hidden);
        return 1;
    }

    private static int setLegsVisibility(CommandContext<CommandSourceStack> context) {
        final var player = Objects.requireNonNull(context.getSource().getPlayer());
        final var hidden = BoolArgumentType.getBool(context, "hidden");
        setEquipmentHidden(player, EquipmentSlot.LEGS, hidden);
        return 1;
    }

    private static int setFeetVisibility(CommandContext<CommandSourceStack> context) {
        final var player = Objects.requireNonNull(context.getSource().getPlayer());
        final var hidden = BoolArgumentType.getBool(context, "hidden");
        setEquipmentHidden(player, EquipmentSlot.FEET, hidden);
        return 1;
    }

    public static void setEquipmentHidden(ServerPlayer player, EquipmentSlot slot, boolean hidden) {
        PlayerData.of(player).setEquipmentHidden(slot, hidden);
    }
}
