package dev.arzcbnh.tekoha.misc;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.arzcbnh.tekoha.data.PlayerData;
import dev.arzcbnh.tekoha.mixin.LivingEntityAccessor;
import java.util.HashMap;
import java.util.Map;
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
                                            .executes(ArmorCommands::setFeetVisibility)))
                            .then(Commands.literal("all")
                                    .then(Commands.argument("hidden", BoolArgumentType.bool())
                                            .requires(CommandSourceStack::isPlayer)
                                            .executes(ArmorCommands::setAllVisibility)))));
        });
    }

    private static int setHeadVisibility(CommandContext<CommandSourceStack> context) {
        final var player = Objects.requireNonNull(context.getSource().getPlayer());
        final var hidden = BoolArgumentType.getBool(context, "hidden");
        final var map = new HashMap<>(Map.of(EquipmentSlot.HEAD, player.getItemBySlot(EquipmentSlot.HEAD)));
        setEquipmentHidden(player, EquipmentSlot.HEAD, hidden);
        ((LivingEntityAccessor) player).tekoha$handleEquipmentChanges(map);
        return 1;
    }

    private static int setChestVisibility(CommandContext<CommandSourceStack> context) {
        final var player = Objects.requireNonNull(context.getSource().getPlayer());
        final var hidden = BoolArgumentType.getBool(context, "hidden");
        final var map = new HashMap<>(Map.of(EquipmentSlot.CHEST, player.getItemBySlot(EquipmentSlot.CHEST)));
        setEquipmentHidden(player, EquipmentSlot.CHEST, hidden);
        ((LivingEntityAccessor) player).tekoha$handleEquipmentChanges(map);
        return 1;
    }

    private static int setLegsVisibility(CommandContext<CommandSourceStack> context) {
        final var player = Objects.requireNonNull(context.getSource().getPlayer());
        final var hidden = BoolArgumentType.getBool(context, "hidden");
        final var map = new HashMap<>(Map.of(EquipmentSlot.LEGS, player.getItemBySlot(EquipmentSlot.LEGS)));
        setEquipmentHidden(player, EquipmentSlot.LEGS, hidden);
        ((LivingEntityAccessor) player).tekoha$handleEquipmentChanges(map);
        return 1;
    }

    private static int setFeetVisibility(CommandContext<CommandSourceStack> context) {
        final var player = Objects.requireNonNull(context.getSource().getPlayer());
        final var hidden = BoolArgumentType.getBool(context, "hidden");
        final var map = new HashMap<>(Map.of(EquipmentSlot.FEET, player.getItemBySlot(EquipmentSlot.FEET)));
        setEquipmentHidden(player, EquipmentSlot.FEET, hidden);
        ((LivingEntityAccessor) player).tekoha$handleEquipmentChanges(map);
        return 1;
    }

    private static int setAllVisibility(CommandContext<CommandSourceStack> context) {
        final var player = Objects.requireNonNull(context.getSource().getPlayer());
        final var hidden = BoolArgumentType.getBool(context, "hidden");
        final var map = new HashMap<>(Map.of(
                EquipmentSlot.HEAD,
                player.getItemBySlot(EquipmentSlot.HEAD),
                EquipmentSlot.CHEST,
                player.getItemBySlot(EquipmentSlot.CHEST),
                EquipmentSlot.LEGS,
                player.getItemBySlot(EquipmentSlot.LEGS),
                EquipmentSlot.FEET,
                player.getItemBySlot(EquipmentSlot.FEET)));
        setEquipmentHidden(player, EquipmentSlot.HEAD, hidden);
        setEquipmentHidden(player, EquipmentSlot.CHEST, hidden);
        setEquipmentHidden(player, EquipmentSlot.LEGS, hidden);
        setEquipmentHidden(player, EquipmentSlot.FEET, hidden);
        ((LivingEntityAccessor) player).tekoha$handleEquipmentChanges(map);
        return 1;
    }

    public static void setEquipmentHidden(ServerPlayer player, EquipmentSlot slot, boolean hidden) {
        PlayerData.of(player).setEquipmentHidden(slot, hidden);
    }
}
