package ru.paingocry.disasters.arena;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.paingocry.disasters.Main;

import java.util.*;

import static org.bukkit.Bukkit.getWorld;

public class Arena {
    private final Map<Location, Material> originalBlocks = new HashMap<>();
    private final String id;
    private final String displayName;
    private final int minPlayers;
    private final int maxPlayers;
    private Location pos1;
    private Location pos2;
    private final Set<Player> players; // Множество игроков на арене
    private boolean gameStarted;
    private int countdown; // Таймер отсчета времени
    private boolean gameInProgress;

    public Arena(String id, String displayName, int minPlayers, int maxPlayers, Location pos1, Location pos2) {
        this.id = id;
        this.displayName = displayName;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.players = new HashSet<>();
        this.gameStarted = false;
        this.gameInProgress = false;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public Location getPos1() {
        return pos1;
    }

    public int getPlayerCount() {
        return players.size();
    }

    public void setPos1(Location pos1) {
        this.pos1 = pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public void setPos2(Location pos2) {
        this.pos2 = pos2;
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public boolean isGameInProgress() {
        return gameInProgress;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public void setGameInProgress(boolean gameInProgress) {
        this.gameInProgress = gameInProgress;
    }

    // Метод для добавления игрока на арену
    public void addPlayer(Player player) {
        players.add(player);
    }

    // Метод для удаления игрока с арены
    public void removePlayer(Player player) {
        players.remove(player);
    }

    // Метод для начала игры с отсчетом времени
    public void startGame() {
        saveOriginalState();
        if (gameStarted || players.size() < minPlayers) {
            return;
        }

        gameStarted = true;

        // Таймер отсчета
        countdown = 10; // Начинаем с 10 секунд

        new BukkitRunnable() {
            @Override
            public void run() {
                if (countdown <= 0) {
                    startDisasters();  // После отсчета начинаем происшествия
                    cancel();
                } else {
                    sendTitleToPlayers("У вас есть " + countdown + " секунд до начала игры", "", 10, 20, 10);
                    countdown--;
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);  // 20 тиков = 1 секунда
    }

    // Метод для отправки тайтлов игрокам
    private void sendTitleToPlayers(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        for (Player player : players) {
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    // Метод для начала происшествий
    public void startDisasters() {
        // Запускаем 5 случайных происшествий за 5 минут
        new BukkitRunnable() {
            int disastersCount = 0;

            @Override
            public void run() {
                if (disastersCount >= 5) {
                    cancel();
                    return;
                }

                // Выбираем случайное происшествие
                int disasterId = (int) (Math.random() * 8) + 1;  // Рандомный выбор происшествия

                switch (disasterId) {
                    case 1: // Кислотный дождь
                        startAcidRain();
                        break;
                    case 2: // Драконы
                        spawnDragons();
                        break;
                    case 3: // Драконы
                        startLightning();
                        break;
                    case 4: // Драконы
                        startZombieApocalypse();
                        break;
                    case 5: // Драконы
                        disableJumping();
                        break;
                    case 6: // Драконы
                        startFlood();
                        break;
                    case 7: // Драконы
                        startPVP();
                        break;
                    case 8: // Драконы
                        startAnvilRain();
                        break;
                    // Добавим другие происшествия по мере реализации
                    default:
                        break;
                }

                disastersCount++;
            }
        }.runTaskTimer(Main.getInstance(), 0, 6000);  // 6000 тиков = 5 минут
    }

    private void spawnDragons() {
        World world = pos1.getWorld();

        double x = pos1.getX() + Math.random() * (pos2.getX() - pos1.getX());
        double z = pos1.getZ() + Math.random() * (pos2.getZ() - pos1.getZ());
        Location dragonLocation1 = new Location(world, x, world.getHighestBlockYAt((int) x, (int) z), z);

        x = pos1.getX() + Math.random() * (pos2.getX() - pos1.getX());
        z = pos1.getZ() + Math.random() * (pos2.getZ() - pos1.getZ());
        Location dragonLocation2 = new Location(world, x, world.getHighestBlockYAt((int) x, (int) z), z);

        world.spawnEntity(dragonLocation1, EntityType.ENDER_DRAGON);
        world.spawnEntity(dragonLocation2, EntityType.ENDER_DRAGON);
    }

    private void startLightning() {
        World world = pos1.getWorld();

        new BukkitRunnable() {
            int lightningCount = 0;

            @Override
            public void run() {
                if (lightningCount >= 10) {
                    cancel();
                    return;
                }

                double x = pos1.getX() + Math.random() * (pos2.getX() - pos1.getX());
                double z = pos1.getZ() + Math.random() * (pos2.getZ() - pos1.getZ());
                Location lightningLocation = new Location(world, x, world.getHighestBlockYAt((int) x, (int) z), z);

                world.strikeLightning(lightningLocation);
                lightningCount++;
            }
        }.runTaskTimer(Main.getInstance(), 0, 200);
    }

    private void startZombieApocalypse() {
        World world = pos1.getWorld();

        new BukkitRunnable() {
            @Override
            public void run() {
                double x = pos1.getX() + Math.random() * (pos2.getX() - pos1.getX());
                double z = pos1.getZ() + Math.random() * (pos2.getZ() - pos1.getZ());
                Location zombieLocation = new Location(world, x, world.getHighestBlockYAt((int) x, (int) z), z);

                world.spawnEntity(zombieLocation, EntityType.ZOMBIE);
            }
        }.runTaskTimer(Main.getInstance(), 0, 40);
    }

    private void disableJumping() {
        Set<Player> arenaPlayers = new HashSet<>(players);

        for (Player player : arenaPlayers) {
            Main.getNoJumpPlayers().add(player.getUniqueId());
        }

        sendTitleToPlayers("Прыжки отключены!", "", 10, 20, 10);

        // Через 30 секунд включаем прыжки обратно
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : arenaPlayers) {
                    Main.getNoJumpPlayers().remove(player.getUniqueId());
                }
                sendTitleToPlayers("Прыжки включены!", "", 10, 20, 10);
            }
        }.runTaskLater(Main.getInstance(), 600); // 30 секунд = 600 тиков
    }

    private void startFlood() {
        World world = pos1.getWorld();

        new BukkitRunnable() {
            @Override
            public void run() {
                for (int y = 0; y < 60; y++) {
                    for (double x = pos1.getX(); x < pos2.getX(); x++) {
                        for (double z = pos1.getZ(); z < pos2.getZ(); z++) {
                            Location loc = new Location(world, x, y, z);
                            if (loc.getBlock().getType() == Material.AIR) {
                                loc.getBlock().setType(Material.WATER);
                            }
                        }
                    }
                }

                for (Player player : players) {
                    if (player.getLocation().getBlock().getType() == Material.WATER) {
                        player.damage(1);
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

    private void startPVP() {
        if (players.size() < 10) return;

        sendTitleToPlayers("PvP активировано!", "", 10, 40, 10);
        Main.getPvpEnabledArenas().add(this.id);

        // Следим за количеством игроков, отключаем PvP при <=8
        new BukkitRunnable() {
            @Override
            public void run() {
                if (players.size() <= 8) {
                    sendTitleToPlayers("PvP завершено!", "", 10, 40, 10);
                    Main.getPvpEnabledArenas().remove(id);
                    cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 40); // Проверка каждые 2 секунды
    }

    private void startAnvilRain() {
        World world = pos1.getWorld();

        new BukkitRunnable() {
            @Override
            public void run() {
                for (double x = pos1.getX(); x < pos2.getX(); x++) {
                    for (double z = pos1.getZ(); z < pos2.getZ(); z++) {
                        Location loc = new Location(world, x, 100, z);
                        if (loc.getBlock().getType() == Material.AIR) {
                            FallingBlock anvil = world.spawnFallingBlock(loc, Material.ANVIL, (byte) 0);
                            anvil.setHurtEntities(true);
                        }
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

    private void startAcidRain() {
        World world = pos1.getWorld();
        world.setStorm(true);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : players) {
                    if (player.getLocation().getBlock().getType() == Material.AIR) {
                        player.damage(0.5);
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

    private void startRandomDisasters() {
        int disasterId = (int) (Math.random() * 8) + 1;  // Рандомный выбор происшествия

        switch (disasterId) {
            case 1:
                startAcidRain();
                break;
            case 2:
                spawnDragons();
                break;
            case 3:
                startLightning();
                break;
            case 4:
                startZombieApocalypse();
                break;
            case 5:
                startPVP();
                break;
            case 6:
                disableJumping();
                break;
            case 7:
                startFlood();
                break;
            case 8:
                startAnvilRain();
                break;
            default:
                break;
        }
    }

    private void saveOriginalState() {
        World world = pos1.getWorld();

        for (int x = Math.min(pos1.getBlockX(), pos2.getBlockX()); x <= Math.max(pos1.getBlockX(), pos2.getBlockX()); x++) {
            for (int y = Math.min(pos1.getBlockY(), pos2.getBlockY()); y <= Math.max(pos1.getBlockY(), pos2.getBlockY()); y++) {
                for (int z = Math.min(pos1.getBlockZ(), pos2.getBlockZ()); z <= Math.max(pos1.getBlockZ(), pos2.getBlockZ()); z++) {
                    Location loc = new Location(world, x, y, z);
                    originalBlocks.put(loc, loc.getBlock().getType());
                }
            }
        }
    }

    private void sendMessageToPlayers(String message) {
        for (Player player : players) {
            player.sendMessage(message);
        }
    }

    public void resetArena() {
        World world = pos1.getWorld();

        for (Map.Entry<Location, Material> entry : originalBlocks.entrySet()) {
            Location loc = entry.getKey();
            Material originalMaterial = entry.getValue();

            // Восстанавливаем блок
            world.getBlockAt(loc).setType(originalMaterial);
        }

        // Очищаем игроков
        for (Player player : new HashSet<>(players)) {
            player.teleport(Main.getInstance().getLobbyLocation());
            players.remove(player);
        }

        // Сброс состояний
        gameInProgress = false;
        gameStarted = false;
        originalBlocks.clear(); // Очищаем карту после восстановления
    }

    public void onPlayerDeath(Player player) {
        player.sendMessage("Вы погибли! Вы теперь в режиме наблюдателя.");
        player.setGameMode(GameMode.SPECTATOR); // Переводим в режим наблюдателя
        updateRemainingPlayers();
    }

    private void updateRemainingPlayers() {
        // Проверяем количество оставшихся игроков
        List<Player> alivePlayers = new ArrayList<>();
        for (Player player : players) {
            if (player.getGameMode() == GameMode.SURVIVAL) {
                alivePlayers.add(player);
            }
        }

        if (alivePlayers.size() <= 0) {
            endGame(); // Если все игроки погибли, заканчиваем игру
            sendMessageToPlayers("Никто не выжил.");
        }
    }

    // Метод для завершения игры
    public void endGame() {
        // Проверяем, кто выжил
        List<String> survivors = new ArrayList<>();
        for (Player player : players) {
            if (player.getGameMode() == GameMode.SURVIVAL) {
                survivors.add(player.getName());
            }
        }

        if (survivors.isEmpty()) {
            sendMessageToPlayers("Никто не выжил.");
        } else {
            String survivorList = String.join(", ", survivors);
            sendMessageToPlayers("Выжившие игроки: " + survivorList);
        }

        // Телепортация всех игроков в лобби
        for (Player player : players) {
            player.teleport(Main.getInstance().getLobbyLocation());
        }

        resetArena();
    }




}