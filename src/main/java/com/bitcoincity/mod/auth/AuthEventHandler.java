package com.bitcoincity.mod.auth;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.event.TickEvent;


@Mod.EventBusSubscriber(modid = "bitcoincity", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AuthEventHandler {

    public static void register() {
        MinecraftForge.EVENT_BUS.register(AuthEventHandler.class);
    }

    // Bloquear movimiento si no está logueado
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.level().isClientSide()) return;

        ServerPlayer player = (ServerPlayer) event.player;
        String username = player.getGameProfile().getName();

        if (!LoginCommand.isLogged(username)) {
            player.teleportTo(player.serverLevel(), player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
        }
    }

    // Bloquear interacción si no está logueado
    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        ServerPlayer player = (ServerPlayer) event.getEntity();
        String username = player.getGameProfile().getName();

        if (!LoginCommand.isLogged(username)) {
            event.setCanceled(true);
        }
    }

    // Desloguear si sale o cambia de dimensión
    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        String username = event.getEntity().getGameProfile().getName();
        LoginCommand.logout(username);
    }

    @SubscribeEvent
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        String username = event.getEntity().getGameProfile().getName();
        LoginCommand.logout(username);
    }

    @SubscribeEvent
    public static void onPlayerJoin(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        String username = player.getName().getString();

        // Si el jugador NO está registrado
        if (!UserManager.isRegistered(username)) {
            player.sendSystemMessage(Component.literal(
                    "§e[BitcoinCity] §cNo estás registrado. Usa §b/register <contraseña> §cpara crear una cuenta."
            ));
        }
        // Si está registrado pero no ha iniciado sesión
        else if (!LoginCommand.isLogged(username)) {
            player.sendSystemMessage(Component.literal(
                    "§e[BitcoinCity] §cDebes iniciar sesión con §b/login <contraseña> §cpara jugar."
            ));
        }
    }

    @SubscribeEvent
    public static void onPlayerReminderTick(net.minecraftforge.event.TickEvent.PlayerTickEvent event) {
        if (!(event.player instanceof ServerPlayer player)) return;
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) return;

        String username = player.getName().getString();

        // Solo recordamos a los que no están logueados o registrados
        if (!UserManager.isRegistered(username) || !LoginCommand.isLogged(username)) {

            // Cada 200 ticks = 10 segundos aprox
            if (player.tickCount % 200 == 0) {
                player.sendSystemMessage(Component.literal(
                        "§e[BitcoinCity] §cRecuerda registrarte o iniciar sesión para poder jugar.\n" +
                                "§bUsa /register <contraseña> o /login <contraseña>"
                ));

                // Reproducir sonido discreto
                player.playNotifySound(net.minecraft.sounds.SoundEvents.NOTE_BLOCK_BELL.value(),
                        net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
    }

}
