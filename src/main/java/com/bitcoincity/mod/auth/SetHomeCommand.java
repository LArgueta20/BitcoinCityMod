package com.bitcoincity.mod.auth;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class SetHomeCommand {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_DIR = new File(FMLPaths.CONFIGDIR.get().toFile(), "bitcoincity");
    private static final File HOMES_FILE = new File(CONFIG_DIR, "homes.json");

    // Mapa: jugador -> (nombreHome -> posición)
    private static final Map<String, Map<String, BlockPos>> homes = new HashMap<>();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sethome")
                .then(Commands.argument("nombre", StringArgumentType.word())
                        .executes(SetHomeCommand::setHome))
        );

        // Carga los homes al iniciar
        loadHomes();
    }

    private static int setHome(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String homeName = StringArgumentType.getString(context, "nombre");
        BlockPos pos = player.blockPosition();
        String username = player.getName().getString();

        homes.putIfAbsent(username, new HashMap<>());
        homes.get(username).put(homeName, pos);

        saveHomes();

        player.sendSystemMessage(Component.literal(
                "§a[BitcoinCity] §fHas establecido el hogar §b" + homeName +
                        "§f en §b" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()));

        return 1;
    }

    public static BlockPos getHome(String username, String homeName) {
        Map<String, BlockPos> playerHomes = homes.get(username);
        if (playerHomes == null) return null;
        return playerHomes.get(homeName);
    }

    public static Map<String, Map<String, BlockPos>> getAllHomes() {
        return homes;
    }

    // --- Persistencia en archivo JSON ---

    private static void saveHomes() {
        try {
            if (!CONFIG_DIR.exists()) CONFIG_DIR.mkdirs();
            FileWriter writer = new FileWriter(HOMES_FILE);
            GSON.toJson(homes, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadHomes() {
        if (!HOMES_FILE.exists()) return;

        try (FileReader reader = new FileReader(HOMES_FILE)) {
            Type type = new TypeToken<Map<String, Map<String, BlockPos>>>(){}.getType();
            Map<String, Map<String, BlockPos>> loaded = GSON.fromJson(reader, type);
            if (loaded != null) homes.putAll(loaded);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteHome(String username, String homeName) {
        if (homes.containsKey(username)) {
            homes.get(username).remove(homeName);
            saveHomes();
        }
    }
}