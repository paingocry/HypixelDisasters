package ru.paingocry.disasters;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.paingocry.disasters.commands.DisastersCommand;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Main extends JavaPlugin {

    private static Main instance;
    private File arenasFile;
    private FileConfiguration arenasConfig;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new JumpListener(), this);
        getServer().getPluginManager().registerEvents(new PvpListener(), this);
        instance = this;

        // Создание config.yml (если его нет)
        saveDefaultConfig();

        // Создание arenas.yml (если его нет)
        createArenasFile();

        // Инициализация команд
        getCommand("disasters").setExecutor(new DisastersCommand(this));

        getLogger().info("Disasters plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disasters plugin disabled!");
    }

    public static Main getInstance() {
        return instance;
    }

    public void setLobbyLocation(Location location) {
        getConfig().set("lobby.world", location.getWorld().getName());
        getConfig().set("lobby.x", location.getX());
        getConfig().set("lobby.y", location.getY());
        getConfig().set("lobby.z", location.getZ());
        getConfig().set("lobby.yaw", location.getYaw());
        getConfig().set("lobby.pitch", location.getPitch());
        saveConfig();
    }

    public Location getLobbyLocation() {
        String worldName = getConfig().getString("lobby.world");
        World world = Bukkit.getWorld(worldName);
        double x = getConfig().getDouble("lobby.x");
        double y = getConfig().getDouble("lobby.y");
        double z = getConfig().getDouble("lobby.z");
        float yaw = (float) getConfig().getDouble("lobby.yaw");
        float pitch = (float) getConfig().getDouble("lobby.pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }

    private static final Set<UUID> noJumpPlayers = new HashSet<>();

    public static Set<UUID> getNoJumpPlayers() {
        return noJumpPlayers;
    }

    private static final Set<String> pvpEnabledArenas = new HashSet<>();

    public static Set<String> getPvpEnabledArenas() {
        return pvpEnabledArenas;
    }

    // Создание и загрузка arenas.yml
    private void createArenasFile() {
        arenasFile = new File(getDataFolder(), "arenas.yml");
        if (!arenasFile.exists()) {
            saveResource("arenas.yml", false);  // Сохраняем arenas.yml, если его нет
        }
        arenasConfig = getConfigForArenas();
    }

    // Получаем конфигурацию для arenas.yml
    public FileConfiguration getConfigForArenas() {
        if (arenasConfig == null) {
            arenasConfig = YamlConfiguration.loadConfiguration(arenasFile);
        }
        return arenasConfig;
    }

    // Сохраняем arenas.yml
    public void saveArenasConfig() {
        if (arenasConfig == null || arenasFile == null) return;

        try {
            arenasConfig.save(arenasFile);
        } catch (Exception e) {
            getLogger().warning("Не удалось сохранить arenas.yml!");
        }
    }
}