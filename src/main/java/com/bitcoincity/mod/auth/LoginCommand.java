package com.bitcoincity.mod.auth;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashSet;
import java.util.Set;

public class LoginCommand {

    // Jugadores actualmente autenticados (solo en memoria)
    private static final Set<String> loggedPlayers = new HashSet<>();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("login")
                .then(Commands.argument("password", StringArgumentType.word())
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            String username = player.getName().getString();
                            String password = StringArgumentType.getString(context, "password");

                            if (!UserManager.isRegistered(username)) {
                                player.sendSystemMessage(Component.literal("§c[BitcoinCity Auth] §fNo estás registrado. Usa §b/register <contraseña> <repetir>."));
                                return 0;
                            }

                            if (loggedPlayers.contains(username.toLowerCase())) {
                                player.sendSystemMessage(Component.literal("§e[BitcoinCity Auth] §fYa has iniciado sesión."));
                                return 0;
                            }

                            if (UserManager.login(username, password)) {
                                loggedPlayers.add(username.toLowerCase());
                                player.sendSystemMessage(Component.literal("§a[BitcoinCity Auth] §fInicio de sesión exitoso. ¡Bienvenido de nuevo!"));
                            } else {
                                player.sendSystemMessage(Component.literal("§c[BitcoinCity Auth] §fContraseña incorrecta."));
                            }

                            return 1;
                        })));
    }

    public static boolean isLogged(String username) {
        return loggedPlayers.contains(username.toLowerCase());
    }

    public static void logout(String username) {
        loggedPlayers.remove(username.toLowerCase());
    }
}