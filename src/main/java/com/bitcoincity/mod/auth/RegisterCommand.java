package com.bitcoincity.mod.auth;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class RegisterCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("register")
                .then(Commands.argument("password", StringArgumentType.string())
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            String username = player.getGameProfile().getName();
                            String password = StringArgumentType.getString(context, "password");

                            if (UserManager.isRegistered(username)) {
                                player.sendSystemMessage(Component.literal("§e[BitcoinCity] §cYa estás registrado. Usa §b/login <contraseña>§c para iniciar sesión."));
                            } else {
                                UserManager.register(username, password);
                                player.sendSystemMessage(Component.literal("§e[BitcoinCity] §aTe has registrado correctamente. Ahora usa §b/login <contraseña>§a para entrar."));
                            }
                            return 1;
                        })));
    }
}