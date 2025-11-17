package com.bitcoincity.mod.auth;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class TpAcceptCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("tpaccept")
                .executes(TpAcceptCommand::accept)
        );
    }

    private static int accept(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException(); // <- jugador que ejecuta /tpaccept
        String username = player.getGameProfile().getName(); // nombre del jugador
        ServerPlayer requester = TeleportManager.getRequester(player); // quien envió la solicitud

        if (requester == null) {
            player.sendSystemMessage(Component.literal(
                    "§c[BitcoinCity] §fNo tienes solicitudes de teletransporte pendientes."
            ));

            // Sonido de "no" de aldeano solo para el jugador
            player.level().playSound(
                    null, // null = solo este jugador lo escucha
                    player.getX(), player.getY(), player.getZ(),
                    SoundEvents.VILLAGER_NO, // SIN .value()
                    SoundSource.PLAYERS,
                    1.0f, 1.0f
            );

            return 0;
        }

        // Aceptar la solicitud
        TeleportManager.acceptRequest(player);

        player.sendSystemMessage(Component.literal("§a[BitcoinCity] §fHas aceptado la solicitud de §b" + requester.getName().getString() + "§f."));
        requester.sendSystemMessage(Component.literal("§a[BitcoinCity] §fTu solicitud de teletransporte fue aceptada por §b" + player.getName().getString() + "§f."));

        // 🔔 Sonido de confirmación para ambos jugadores
        player.level().playSound(
                null, // null = solo el jugador lo escucha
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.NOTE_BLOCK_BELL,
                SoundSource.PLAYERS,
                1.0f, 1.2f // volumen y tono
        );

        requester.level().playSound(
                null,
                requester.getX(), requester.getY(), requester.getZ(),
                SoundEvents.NOTE_BLOCK_BELL,
                SoundSource.PLAYERS,
                1.0f, 0.8f
        );



        return 1;
    }

}