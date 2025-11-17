package com.bitcoincity.mod;

import com.bitcoincity.mod.auth.RegisterCommand;
import com.bitcoincity.mod.auth.LoginCommand;
import com.bitcoincity.mod.auth.SetHomeCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.bitcoincity.mod.auth.*;

@Mod.EventBusSubscriber(modid = "bitcoincity")
public class CommandRegistry {

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        // Registrar comandos de autenticación
        RegisterCommand.register(event.getDispatcher());
        LoginCommand.register(event.getDispatcher());
        SetHomeCommand.register(event.getDispatcher());
        HomeCommand.register(event.getDispatcher());
        DelHomeCommand.register(event.getDispatcher());
        ListHomesCommand.register(event.getDispatcher());
        TpaCommand.register(event.getDispatcher());
        TpAcceptCommand.register(event.getDispatcher());
        TpDenyCommand.register(event.getDispatcher());
        TpaHereCommand.register(event.getDispatcher());

        System.out.println("[BitcoinCity] Comandos /register y /login registrados ✅");
    }
}