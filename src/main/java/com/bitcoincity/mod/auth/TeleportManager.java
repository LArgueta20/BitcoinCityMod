package com.bitcoincity.mod.auth;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TeleportManager {
    private static final Map<String, TeleportRequest> tpaRequests = new HashMap<>();

    // Duración máxima: 60 segundos
    private static final long REQUEST_LIFETIME = 60_000;

    public static void sendRequest(ServerPlayer from, ServerPlayer to) {
        String targetName = to.getName().getString();
        String requesterName = from.getName().getString();

        tpaRequests.put(targetName, new TeleportRequest(requesterName, System.currentTimeMillis()));

        from.sendSystemMessage(Component.literal("§e[BitcoinCity] §fSolicitud enviada a §b" + targetName + "§f (expira en 60s)."));
        to.sendSystemMessage(Component.literal("§e[BitcoinCity] §b" + requesterName + "§f quiere teletransportarse hacia ti.\n" +
                "§fUsa §a/tpaccept §fo §c/tpdeny §fen los próximos §e60 segundos§f."));
    }

    public static void sendHereRequest(ServerPlayer from, ServerPlayer to) {
        String targetName = to.getName().getString();
        String requesterName = from.getName().getString();

        tpaRequests.put(targetName, new TeleportRequest(requesterName, System.currentTimeMillis(), true));

        from.sendSystemMessage(Component.literal("§e[BitcoinCity] §fSolicitud enviada a §b" + targetName + "§f para que se teletransporte hacia ti (expira en 60s)."));
        to.sendSystemMessage(Component.literal("§e[BitcoinCity] §b" + requesterName + "§f quiere que te teletransportes hacia él.\n" +
                "§fUsa §a/tpaccept §fo §c/tpdeny §fen los próximos §e60 segundos§f."));
    }


    public static boolean hasRequest(String targetName) {
        TeleportRequest request = tpaRequests.get(targetName);
        if (request == null) return false;

        // Verificar expiración
        if (System.currentTimeMillis() - request.timestamp > REQUEST_LIFETIME) {
            tpaRequests.remove(targetName);
            return false;
        }

        return true;
    }

    public static void acceptRequest(ServerPlayer target) {
        String targetName = target.getName().getString();
        TeleportRequest request = tpaRequests.remove(targetName);
        if (request == null) {
            target.sendSystemMessage(Component.literal("§c[BitcoinCity] §fNo tienes solicitudes válidas o ya expiraron."));
            return;
        }

        ServerPlayer requester = target.server.getPlayerList().getPlayerByName(request.requester);
        if (requester != null) {
            if (request.here) {
                // El target se teletransporta hacia el requester
                target.teleportTo(requester.serverLevel(), requester.getX(), requester.getY(), requester.getZ(), requester.getYRot(), requester.getXRot());
                requester.sendSystemMessage(Component.literal("§a[BitcoinCity] §f" + targetName + " §fse ha teletransportado hacia ti."));
                target.sendSystemMessage(Component.literal("§a[BitcoinCity] §fTe has teletransportado hacia §b" + requester.getName().getString() + "§f."));
            } else {
                // TPA normal
                requester.teleportTo(target.serverLevel(), target.getX(), target.getY(), target.getZ(), target.getYRot(), target.getXRot());
                requester.sendSystemMessage(Component.literal("§a[BitcoinCity] §fTu solicitud fue aceptada. Te has teletransportado a §b" + targetName + "§f."));
                target.sendSystemMessage(Component.literal("§a[BitcoinCity] §fHas aceptado la solicitud de §b" + request.requester + "§f."));
            }
        }
    }


    public static void denyRequest(ServerPlayer target) {
        String targetName = target.getName().getString();
        TeleportRequest request = tpaRequests.remove(targetName);
        if (request == null) {
            target.sendSystemMessage(Component.literal("§c[BitcoinCity] §fNo tienes solicitudes válidas o ya expiraron."));
            return;
        }

        ServerPlayer requester = target.server.getPlayerList().getPlayerByName(request.requester);
        if (requester != null) {
            requester.sendSystemMessage(Component.literal("§c[BitcoinCity] §fTu solicitud a §b" + targetName + "§f fue rechazada."));
        }

        target.sendSystemMessage(Component.literal("§e[BitcoinCity] §fHas rechazado la solicitud de §b" + request.requester + "§f."));
    }

    // Revisión en cada tick
    public static void tick(ServerPlayer anyPlayer) {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, TeleportRequest>> it = tpaRequests.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, TeleportRequest> entry = it.next();
            TeleportRequest request = entry.getValue();
            long elapsed = now - request.timestamp;

            if (elapsed > REQUEST_LIFETIME) {
                it.remove();

                ServerPlayer target = anyPlayer.server.getPlayerList().getPlayerByName(entry.getKey());
                ServerPlayer requester = anyPlayer.server.getPlayerList().getPlayerByName(request.requester);

                if (target != null)
                    target.sendSystemMessage(Component.literal("§c[BitcoinCity] §fLa solicitud de §b" + request.requester + "§f ha expirado."));
                if (requester != null)
                    requester.sendSystemMessage(Component.literal("§c[BitcoinCity] §fTu solicitud a §b" + entry.getKey() + "§f ha expirado."));

                continue;
            }

            // Mostrar barra de progreso
            ServerPlayer target = anyPlayer.server.getPlayerList().getPlayerByName(entry.getKey());
            if (target != null) {
                int secondsLeft = (int) ((REQUEST_LIFETIME - elapsed) / 1000);
                target.displayClientMessage(
                        Component.literal("§e🕒 Solicitud de §b" + request.requester + " §fexpira en §c" + secondsLeft + "s"),
                        true // true = mostrar en action bar
                );
            }
        }
    }

    private static class TeleportRequest {
        String requester;
        long timestamp;
        boolean here; // true = tpahere, false = tpa

        TeleportRequest(String requester, long timestamp) {
            this(requester, timestamp, false);
        }

        TeleportRequest(String requester, long timestamp, boolean here) {
            this.requester = requester;
            this.timestamp = timestamp;
            this.here = here;
        }
    }

}