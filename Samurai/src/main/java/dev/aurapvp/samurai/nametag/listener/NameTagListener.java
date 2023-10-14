package dev.aurapvp.samurai.nametag.listener;

import dev.aurapvp.samurai.Samurai;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class NameTagListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Samurai.getInstance().getNameTagHandler().playerJoin(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Samurai.getInstance().getNameTagHandler().playerQuit(event.getPlayer());
    }

}
