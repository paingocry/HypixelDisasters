package ru.paingocry.disasters;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import ru.paingocry.disasters.arena.Arena;
import ru.paingocry.disasters.arena.ArenaManager;

public class PvpListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) return;

        Player damager = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();

        Arena damagerArena = ArenaManager.getArenaByPlayer(damager);
        Arena victimArena = ArenaManager.getArenaByPlayer(victim);

        if (damagerArena == null || victimArena == null || !damagerArena.equals(victimArena)) {
            event.setCancelled(true);
            return;
        }

        if (!Main.getPvpEnabledArenas().contains(damagerArena.getId())) {
            event.setCancelled(true);
        }
    }
}