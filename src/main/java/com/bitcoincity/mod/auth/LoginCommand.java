package com.bitcoincity.mod.auth;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashSet;

public class LoginCommand {
    private static final HashSet<String> loggedPlayers = new HashSet<>();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("login")
                .then(Commands.argument("password", StringArgumentType.string())
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            String username = player.getGameProfile().getName();
                            String password = StringArgumentType.getString(context, "password");

                            if (!UserManager.isRegistered(username)) {
                                player.sendSystemMessage(Component.literal("§e[BitcoinCity] §cNo estás registrado. Usa §b/register <contraseña>§c para crear una cuenta."));
                                return 0;
                            }

                            if (UserManager.verifyPassword(username, password)) {
                                loggedPlayers.add(username);
                                player.sendSystemMessage(Component.literal("§e[BitcoinCity] §aInicio de sesión exitoso. ¡Bienvenido!"));
                            } else {
                                player.sendSystemMessage(Component.literal("§e[BitcoinCity] §cContraseña incorrecta."));
                            }
                            return 1;
                        })));
    }

    public static boolean isLogged(String username) {
        return loggedPlayers.contains(username);
    }

    public static void logout(String username) {
        loggedPlayers.remove(username);
    }
}