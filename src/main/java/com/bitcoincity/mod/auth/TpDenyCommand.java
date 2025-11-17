package com.bitcoincity.mod.auth;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class TpDenyCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("tpdeny")
                .executes(TpDenyCommand::deny)
        );
    }

    private static int deny(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer target = context.getSource().getPlayerOrException();
        String username = target.getName().getString();

        if (!TeleportManager.hasRequest(username)) {
            target.sendSystemMessage(Component.literal("§c[BitcoinCity] §fNo tienes solicitudes de teletransporte pendientes."));
            return 0;
        }

        TeleportManager.denyRequest(target);
        target.sendSystemMessage(Component.literal("§e[BitcoinCity] §fHas rechazado la solicitud de teletransporte."));
        return 1;
    }
}