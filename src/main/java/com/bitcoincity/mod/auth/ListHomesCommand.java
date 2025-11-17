package com.bitcoincity.mod.auth;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;

public class ListHomesCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("homes")
                .executes(ListHomesCommand::listHomes)
        );
    }

    private static int listHomes(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String username = player.getName().getString();

        Map<String, Map<String, net.minecraft.core.BlockPos>> allHomes = SetHomeCommand.getAllHomes();
        Map<String, net.minecraft.core.BlockPos> playerHomes = allHomes.get(username);

        if (playerHomes == null || playerHomes.isEmpty()) {
            player.sendSystemMessage(Component.literal("§e[BitcoinCity] §fNo tienes hogares guardados."));
            return 0;
        }

        StringBuilder sb = new StringBuilder("§a[BitcoinCity] §fTus hogares:\n");
        for (String name : playerHomes.keySet()) {
            sb.append("§b• §f").append(name).append("\n");
        }

        player.sendSystemMessage(Component.literal(sb.toString()));
        return 1;
    }
}