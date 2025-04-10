package ru.paingocry.disasters.commands;

import ru.paingocry.disasters.Main;
import ru.paingocry.disasters.arena.Arena;
import ru.paingocry.disasters.arena.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Arrays;

public class DisastersCommand implements CommandExecutor {

    private final Main plugin;
    private final ArenaManager arenaManager;

    public DisastersCommand(Main plugin) {
        this.plugin = plugin;
        this.arenaManager = new ArenaManager(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Только игрок может использовать эту команду.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(ChatColor.YELLOW + "/disasters <subcommand>");
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "gui":
                openGui(player);
                break;
            case "create":
                if (args.length < 5) {
                    player.sendMessage(ChatColor.RED + "Использование: /disasters create <name> <displayName> <minPlayers> <maxPlayers>");
                    return true;
                }
                createArena(player, args[1], args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]));
                break;
            case "pos1":
            case "pos2":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Использование: /disasters pos1 <arenaName>");
                    return true;
                }
                setPosition(player, sub, args[1]);
                break;
            case "delete":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Использование: /disasters delete <arenaName>");
                    return true;
                }
                deleteArena(player, args[1]);
                break;
            case "join":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Использование: /disasters join <arenaName>");
                    return true;
                }
                arenaManager.addPlayerToArena(player, args[1]);
                break;
            case "leave":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Использование: /disasters leave <arenaName>");
                    return true;
                }
                arenaManager.removePlayerFromArena(player, args[1]);
                break;
            case "randomjoin":
                randomJoinArena(player);
                break;
            case "setlobby":
                plugin.setLobbyLocation(player.getLocation());
                sender.sendMessage("Лобби установлено в текущих координатах.");
                break;
            default:
                player.sendMessage(ChatColor.RED + "Неизвестная подкоманда: " + sub);
                break;
        }


        return true;
    }

    private void openGui(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("messages.guiTitle", "Выбор карты")));

        ItemStack lava = new ItemStack(Material.LAVA_BUCKET);
        ItemMeta meta = lava.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Играть");
        List<String> lore = Arrays.asList("тест1", "тест2", "тест3", "тест4", "тест5");
        meta.setLore(lore);
        lava.setItemMeta(meta);

        gui.setItem(13, lava); // Центр инвентаря
        player.openInventory(gui);
    }

    private void createArena(Player player, String name, String displayName, int minPlayers, int maxPlayers) {
        Arena existingArena = arenaManager.getArena(name);
        if (existingArena != null) {
            player.sendMessage(ChatColor.RED + "Арена с таким названием уже существует.");
            return;
        }

        Arena arena = new Arena(name, displayName, minPlayers, maxPlayers, null, null);
        arenaManager.saveArena(arena);
        player.sendMessage(ChatColor.GREEN + "Арена " + displayName + " успешно создана!");
    }

    private void setPosition(Player player, String positionType, String arenaName) {
        Arena arena = arenaManager.getArena(arenaName);
        if (arena == null) {
            player.sendMessage(ChatColor.RED + "Арена с таким названием не найдена.");
            return;
        }



        if (positionType.equals("pos1")) {
            arena.setPos1(player.getLocation());
            player.sendMessage(ChatColor.GREEN + "Первая точка арены " + arenaName + " установлена.");
        } else if (positionType.equals("pos2")) {
            arena.setPos2(player.getLocation());
            player.sendMessage(ChatColor.GREEN + "Вторая точка арены " + arenaName + " установлена.");
        }

        arenaManager.saveArena(arena); // Сохраняем изменения
    }

    private void deleteArena(Player player, String arenaName) {
        Arena arena = arenaManager.getArena(arenaName);
        if (arena == null) {
            player.sendMessage(ChatColor.RED + "Арена с таким названием не найдена.");
            return;
        }

        arenaManager.deleteArena(arenaName);
        player.sendMessage(ChatColor.GREEN + "Арена " + arenaName + " успешно удалена.");
    }

    private void joinArena(Player player, String arenaName) {
        Arena arena = arenaManager.getArena(arenaName);
        if (arena == null) {
            player.sendMessage(ChatColor.RED + "Арена с таким названием не найдена.");
            return;
        }

        if (arena.getMinPlayers() > 1) {
            player.sendMessage(ChatColor.YELLOW + "Присоединились к арене " + arenaName);
            // Тут будет логика для присоединения игрока к арене
        } else {
            player.sendMessage(ChatColor.RED + "Недостаточно игроков для начала игры.");
        }
    }

    private void randomJoinArena(Player player) {
        Map<String, Arena> arenas = arenaManager.getArenas();
        if (arenas.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Нет доступных арен.");
            return;
        }

        Random random = new Random();
        Object[] arenaArray = arenas.values().toArray();
        Arena randomArena = (Arena) arenaArray[random.nextInt(arenaArray.length)];

        player.sendMessage(ChatColor.YELLOW + "Присоединились к случайной арене: " + randomArena.getDisplayName());
    }
}