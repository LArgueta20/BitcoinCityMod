package com.bitcoincity.mod;

import com.bitcoincity.mod.auth.UserManager;
import net.minecraftforge.fml.common.Mod;

@Mod("bitcoincity")
public class BitcoinCity {

    public BitcoinCity() {
        // Cargar usuarios al iniciar el mod
        UserManager.loadUsers();
        System.out.println("[BitcoinCity] Sistema de autenticación inicializado correctamente ✅");
    }
}