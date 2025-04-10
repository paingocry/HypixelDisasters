package ru.paingocry.disasters;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class JumpListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!Main.getNoJumpPlayers().contains(player.getUniqueId())) return;

        // Проверка на прыжок: перемещение вверх с земли
        if (event.getFrom().getY() < event.getTo().getY() && player.isOnGround()) {
            event.setCancelled(true);
        }
    }
}