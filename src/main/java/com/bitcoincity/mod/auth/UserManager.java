package com.bitcoincity.mod.auth;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_DIR = new File(FMLPaths.CONFIGDIR.get().toFile(), "bitcoincity");
    private static final File USER_FILE = new File(CONFIG_DIR, "users.json");

    private static Map<String, String> users = new HashMap<>();

    /**
     * Carga los usuarios desde users.json
     */
    public static void loadUsers() {
        try {
            if (!USER_FILE.exists()) {
                CONFIG_DIR.mkdirs();
                saveUsers();
                return;
            }

            FileReader reader = new FileReader(USER_FILE);
            users = GSON.fromJson(reader, users.getClass());
            reader.close();

            if (users == null) users = new HashMap<>();

            System.out.println("[BitcoinCity] Usuarios cargados: " + users.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Guarda los usuarios en users.json
     */
    public static void saveUsers() {
        try {
            CONFIG_DIR.mkdirs();
            FileWriter writer = new FileWriter(USER_FILE);
            GSON.toJson(users, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Registra un nuevo usuario
     */
    public static boolean register(String username, String password) {
        String key = username.toLowerCase();
        if (users.containsKey(key)) {
            return false;
        }
        users.put(key, password);
        saveUsers();
        return true;
    }

    /**
     * Verifica si un jugador ya está registrado
     */
    public static boolean isRegistered(String username) {
        return users.containsKey(username.toLowerCase());
    }

    /**
     * Intenta loguear un usuario
     */
    public static boolean login(String username, String password) {
        String stored = users.get(username.toLowerCase());
        return stored != null && stored.equals(password);
    }

    public static boolean verifyPassword(String username, String password) {
        if (username == null || password == null) return false;
        String stored = users.get(username.toLowerCase());
        return stored != null && stored.equals(password);
    }


}