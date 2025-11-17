package com.bitcoincity.mod;

import com.bitcoincity.mod.auth.AuthEventHandler;
import com.bitcoincity.mod.auth.UserManager;
import net.minecraftforge.fml.common.Mod;

@Mod("bitcoincity")
public class BitcoinCity {

    public BitcoinCity() {
        // Cargar usuarios guardados (si existen)
        UserManager.loadUsers();

        // Registrar eventos de autenticación
        AuthEventHandler.register();

        System.out.println("[BitcoinCity] Sistema de autenticación inicializado correctamente ✅");
    }
}
