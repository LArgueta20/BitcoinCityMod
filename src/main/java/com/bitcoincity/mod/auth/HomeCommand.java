package com.bitcoincity.mod.auth;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class HomeCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("home")
                .then(Commands.argument("nombre", StringArgumentType.word())
                        .executes(HomeCommand::goHome))
        );
    }

    private static int goHome(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String homeName = StringArgumentType.getString(context, "nombre");
        String username = player.getName().getString();

        BlockPos home = SetHomeCommand.getHome(username, homeName);

        if (home == null) {
            player.sendSystemMessage(Component.literal("§c[BitcoinCity] §fNo tienes un hogar con el nombre §b" + homeName + "§f."));
            return 0;
        }

        player.teleportTo(home.getX() + 0.5, home.getY(), home.getZ() + 0.5);
        player.sendSystemMessage(Component.literal("§a[BitcoinCity] §fTeletransportado al hogar §b" + homeName + "§f."));
        return 1;
    }
}