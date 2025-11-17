package com.bitcoincity.mod.auth;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class TpaCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("tpa")
                .then(Commands.argument("jugador", StringArgumentType.word())
                        .executes(TpaCommand::sendTpa))
        );
    }

    private static int sendTpa(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String targetName = StringArgumentType.getString(context, "jugador");

        ServerPlayer target = player.server.getPlayerList().getPlayerByName(targetName);
        if (target == null) {
            player.sendSystemMessage(Component.literal("§c[BitcoinCity] §fEl jugador §b" + targetName + "§f no está en línea."));
            return 0;
        }

        if (target == player) {
            player.sendSystemMessage(Component.literal("§c[BitcoinCity] §fNo puedes enviarte una solicitud a ti mismo."));
            return 0;
        }

        TeleportManager.sendRequest(player, target);

        player.sendSystemMessage(Component.literal("§e[BitcoinCity] §fSolicitud de teletransporte enviada a §b" + targetName + "§f."));
        target.sendSystemMessage(Component.literal("§e[BitcoinCity] §b" + player.getName().getString() +
                " §fquiere teletransportarse hacia ti.\n§fUsa §a/tpaccept §fo §c/tpdeny §fpara responder."));

        return 1;
    }
}