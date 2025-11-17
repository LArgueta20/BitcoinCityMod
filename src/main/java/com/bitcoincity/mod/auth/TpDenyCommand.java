package com.bitcoincity.mod.auth;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class TpDenyCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                net.minecraft.commands.Commands.literal("tpdeny")
                        .executes(TpDenyCommand::deny)
        );
    }

    private static int deny(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException(); // jugador que ejecuta /tpdeny
        String username = player.getGameProfile().getName();

        // Verificamos si tiene solicitudes pendientes
        if (!TeleportManager.hasRequest(username)) {
            player.sendSystemMessage(Component.literal("§c[BitcoinCity] §fNo tienes solicitudes de teletransporte pendientes."));
            player.level().playSound(
                    null,
                    player.getX(), player.getY(), player.getZ(),
                    SoundEvents.VILLAGER_NO,
                    SoundSource.PLAYERS,
                    1.0f, 1.0f
            );
            return 0;
        }

        // Rechazamos la solicitud
        TeleportManager.denyRequest(player);

        player.sendSystemMessage(Component.literal("§e[BitcoinCity] §fHas rechazado la solicitud de teletransporte."));

        // 🔔 Sonidos para ambos jugadores
        player.level().playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.NOTE_BLOCK_BASS,
                SoundSource.PLAYERS,
                1.0f, 0.8f
        );

        return 1;
    }
}