package com.bitcoincity.mod.auth;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class DelHomeCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("delhome")
                .then(Commands.argument("nombre", StringArgumentType.word())
                        .executes(DelHomeCommand::deleteHome))
        );
    }

    private static int deleteHome(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String homeName = StringArgumentType.getString(context, "nombre");
        String username = player.getName().getString();

        if (SetHomeCommand.getHome(username, homeName) == null) {
            player.sendSystemMessage(Component.literal("§c[BitcoinCity] §fNo tienes un hogar llamado §b" + homeName + "§f."));
            return 0;
        }

        SetHomeCommand.deleteHome(username, homeName);
        player.sendSystemMessage(Component.literal("§a[BitcoinCity] §fHogar §b" + homeName + "§f eliminado correctamente."));
        return 1;
    }
}