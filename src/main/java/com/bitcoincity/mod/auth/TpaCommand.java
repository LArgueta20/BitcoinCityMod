package com.bitcoincity.mod.auth;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class TpaCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("tpa")
                        .then(Commands.argument("jugador", EntityArgument.player()) // <--- TAB FUNCIONA
                                .executes(TpaCommand::sendTpa))
        );
    }

    private static int sendTpa(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        ServerPlayer player = context.getSource().getPlayerOrException();

        // ✅ Se obtiene directamente el jugador objetivo (sin strings)
        ServerPlayer target = EntityArgument.getPlayer(context, "jugador");

        if (target == player) {
            player.sendSystemMessage(
                    Component.literal("§c[BitcoinCity] §fNo puedes enviarte una solicitud a ti mismo.")
            );
            return 0;
        }

        // Enviar solicitud
        TeleportManager.sendRequest(player, target);

        // Mensajes
        player.sendSystemMessage(Component.literal(
                "§e[BitcoinCity] §fSolicitud de teletransporte enviada a §b" + target.getName().getString() + "§f."
        ));

        target.sendSystemMessage(Component.literal(
                "§e[BitcoinCity] §b" + player.getName().getString()
                        + " §fquiere teletransportarse hacia ti.\n§fUsa §a/tpaccept §fo §c/tpdeny §fpara responder."
        ));

        // 🔔 Sonido cuando llega la solicitud
        target.level().playSound(
                null,
                target.getX(), target.getY(), target.getZ(),
                SoundEvents.NOTE_BLOCK_PLING,
                SoundSource.PLAYERS,
                1.0f, 1.2f
        );

        return 1;
    }
}