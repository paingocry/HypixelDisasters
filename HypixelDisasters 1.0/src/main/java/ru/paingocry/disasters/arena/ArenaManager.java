package ru.paingocry.disasters.arena;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.paingocry.disasters.Main;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class ArenaManager {

    private final Main plugin;
    private final FileConfiguration arenasConfig;
    private static final Map<String, Arena> arenas = new HashMap<>();
    private static final Map<Player, Arena> playerArenaMap = new HashMap<>();

    // Новый функционал
    private static final Set<String> jumpDisabledArenas = new HashSet<>();
    private static final Set<String> pvpEnabledArenas = new HashSet<>();


    public ArenaManager(Main plugin) {
        this.plugin = plugin;
        this.arenasConfig = plugin.getConfigForArenas();
        loadArenas();
    }

    public static void registerArena(Arena arena) {
        arenas.put(arena.getId(), arena);
    }


    public static void assignPlayer(Player player, Arena arena) {
        playerArenaMap.put(player, arena);
    }

    public static void removePlayer(Player player) {
        playerArenaMap.remove(player);
    }

    public static Arena getArenaByPlayer(Player player) {
        return playerArenaMap.get(player);
    }

    public static Collection<Arena> getAllArenas() {
        return arenas.values();
    }

    // --- Jump disabling flags
    public static void setJumpDisabled(String arenaId, boolean disabled) {
        if (disabled) {
            jumpDisabledArenas.add(arenaId);
        } else {
            jumpDisabledArenas.remove(arenaId);
        }
    }

    public static boolean isJumpDisabled(String arenaId) {
        return jumpDisabledArenas.contains(arenaId);
    }


    public void startArenaGame(String arenaName) {
        Arena arena = getArena(arenaName);
        if (arena != null) {
            arena.startGame();
        }
    }

    public void addPlayerToArena(String arenaName, Player player) {
        Arena arena = getArena(arenaName);
        if (arena != null) {
            arena.addPlayer(player);
        }
    }

    public void onPlayerDeathInArena(String arenaName, Player player) {
        Arena arena = getArena(arenaName);
        if (arena != null) {
            arena.onPlayerDeath(player);
        }
    }

    public void endGameForArena(String arenaName) {
        Arena arena = getArena(arenaName);
        if (arena != null) {
            arena.endGame();
        }
    }

    // --- PvP enabling flags
    public static void setPvpEnabled(String arenaId, boolean enabled) {
        if (enabled) {
            pvpEnabledArenas.add(arenaId);
        } else {
            pvpEnabledArenas.remove(arenaId);
        }
    }

    public static boolean isPvpEnabled(String arenaId) {
        return pvpEnabledArenas.contains(arenaId);
    }

    public static Set<String> getJumpDisabledArenas() {
        return jumpDisabledArenas;
    }

    public static Set<String> getPvpEnabledArenas() {
        return pvpEnabledArenas;
    }

    // Загрузка арен из arenas.yml
    private void loadArenas() {
        arenas.clear();
        if (arenasConfig.contains("arenas")) {
            for (String arenaName : arenasConfig.getConfigurationSection("arenas").getKeys(false)) {
                String displayName = arenasConfig.getString("arenas." + arenaName + ".displayName");
                int minPlayers = arenasConfig.getInt("arenas." + arenaName + ".minPlayers");
                int maxPlayers = arenasConfig.getInt("arenas." + arenaName + ".maxPlayers");

                // Получаем точки арены
                double pos1X = arenasConfig.getDouble("arenas." + arenaName + ".pos1.x");
                double pos1Y = arenasConfig.getDouble("arenas." + arenaName + ".pos1.y");
                double pos1Z = arenasConfig.getDouble("arenas." + arenaName + ".pos1.z");

                double pos2X = arenasConfig.getDouble("arenas." + arenaName + ".pos2.x");
                double pos2Y = arenasConfig.getDouble("arenas." + arenaName + ".pos2.y");
                double pos2Z = arenasConfig.getDouble("arenas." + arenaName + ".pos2.z");

                // Создаем арену и добавляем в список
                Arena arena = new Arena(arenaName, displayName, minPlayers, maxPlayers, null, null);
                arena.setPos1(new Location(null, pos1X, pos1Y, pos1Z)); // Устанавливаем первую точку
                arena.setPos2(new Location(null, pos2X, pos2Y, pos2Z)); // Устанавливаем вторую точку

                arenas.put(arenaName, arena);
            }
        }
    }

    public void addPlayerToArena(Player player, String arenaName) {
        Arena arena = arenas.get(arenaName);
        if (arena == null) {
            player.sendMessage(ChatColor.RED + "Арена не найдена!");
            return;
        }

        if (arena.getPlayerCount() >= arena.getMaxPlayers()) {
            player.sendMessage(ChatColor.RED + "На арене нет свободных мест.");
            return;
        }

        arena.addPlayer(player);
        broadcastArenaMessage(arena, ChatColor.YELLOW + "Игрок " + player.getName() + " присоединился: " + arena.getPlayerCount() + "/" + arena.getMaxPlayers());
    }

    // Метод для удаления игрока с арены
    public void removePlayerFromArena(Player player, String arenaName) {
        Arena arena = arenas.get(arenaName);
        if (arena == null) {
            player.sendMessage(ChatColor.RED + "Арена не найдена!");
            return;
        }

        if (!arena.getPlayers().contains(player)) {
            player.sendMessage(ChatColor.RED + "Вы не на этой арене.");
            return;
        }

        arena.removePlayer(player);
        broadcastArenaMessage(arena, ChatColor.YELLOW + "Игрок " + player.getName() + " вышел: " + arena.getPlayerCount() + "/" + arena.getMaxPlayers());
    }

    // Метод для отправки сообщения всем игрокам на арене
    private void broadcastArenaMessage(Arena arena, String message) {
        for (Player p : arena.getPlayers()) {
            p.sendMessage(message);
        }
    }

    public Map<String, Arena> getArenas() {
        return arenas;
    }

    // Сохранение арен в arenas.yml
    public void saveArena(Arena arena) {
        String arenaName = arena.getId();
        arenasConfig.set("arenas." + arenaName + ".displayName", arena.getDisplayName());
        arenasConfig.set("arenas." + arenaName + ".minPlayers", arena.getMinPlayers());
        arenasConfig.set("arenas." + arenaName + ".maxPlayers", arena.getMaxPlayers());

        // Сохраняем координаты
        arenasConfig.set("arenas." + arenaName + ".pos1.x", arena.getPos1().getX());
        arenasConfig.set("arenas." + arenaName + ".pos1.y", arena.getPos1().getY());
        arenasConfig.set("arenas." + arenaName + ".pos1.z", arena.getPos1().getZ());

        arenasConfig.set("arenas." + arenaName + ".pos2.x", arena.getPos2().getX());
        arenasConfig.set("arenas." + arenaName + ".pos2.y", arena.getPos2().getY());
        arenasConfig.set("arenas." + arenaName + ".pos2.z", arena.getPos2().getZ());

        plugin.saveArenasConfig();  // Сохраняем изменения в файл
    }

    // Получение арены по имени
    public Arena getArena(String name) {
        return arenas.get(name);
    }


    // Удаление арены
    public void deleteArena(String arenaName) {
        arenasConfig.set("arenas." + arenaName, null);
        arenas.remove(arenaName);
        plugin.saveArenasConfig();  // Сохраняем изменения
    }
}
