package com.bitcoincity.mod.auth;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

public class TpaHereCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("tpahere")
                .then(Commands.argument("jugador", net.minecraft.commands.arguments.EntityArgument.player())
                        .executes(TpaHereCommand::execute)));
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer sender = context.getSource().getPlayerOrException();
        ServerPlayer target = net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "jugador");

        if (sender.getName().getString().equals(target.getName().getString())) {
            sender.sendSystemMessage(Component.literal("§c[BitcoinCity] §fNo puedes enviarte una solicitud a ti mismo."));
            return 0;
        }

        // Enviar solicitud tipo "HERE"
        TeleportManager.sendHereRequest(sender, target);
        return 1;
    }
}