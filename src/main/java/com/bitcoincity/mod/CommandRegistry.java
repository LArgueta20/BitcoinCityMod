package com.bitcoincity.mod;

import com.bitcoincity.mod.auth.RegisterCommand;
import com.bitcoincity.mod.auth.LoginCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "bitcoincity")
public class CommandRegistry {

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        // Registrar comandos de autenticación
        RegisterCommand.register(event.getDispatcher());
        LoginCommand.register(event.getDispatcher());

        System.out.println("[BitcoinCity] Comandos /register y /login registrados ✅");
    }
}