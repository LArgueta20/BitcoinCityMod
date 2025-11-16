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
                .then(Commands.argument("password", StringArgumentType.word())
                        .then(Commands.argument("confirm", StringArgumentType.word())
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayer();
                                    String username = player.getName().getString();
                                    String password = StringArgumentType.getString(context, "password");
                                    String confirm = StringArgumentType.getString(context, "confirm");

                                    if (!password.equals(confirm)) {
                                        player.sendSystemMessage(Component.literal("§c[BitcoinCity Auth] §fLas contraseñas no coinciden."));
                                        return 0;
                                    }

                                    if (UserManager.isRegistered(username)) {
                                        player.sendSystemMessage(Component.literal("§e[BitcoinCity Auth] §fYa estás registrado. Usa §b/login <contraseña>."));
                                        return 0;
                                    }

                                    boolean ok = UserManager.register(username, password);
                                    if (ok) {
                                        player.sendSystemMessage(Component.literal("§a[BitcoinCity Auth] §fTe has registrado correctamente 🪙"));
                                    } else {
                                        player.sendSystemMessage(Component.literal("§c[BitcoinCity Auth] §fError al registrarte. Intenta nuevamente."));
                                    }

                                    return 1;
                                }))));
    }
}