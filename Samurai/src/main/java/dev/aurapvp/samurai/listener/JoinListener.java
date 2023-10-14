package dev.aurapvp.samurai.listener;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.SettingsConfiguration;
import dev.aurapvp.samurai.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
        if (Samurai.isENABLED()) return;

        event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
        event.setKickMessage(CC.translate("&cThe server is still starting up..."));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Location spawn = SettingsConfiguration.SPAWN_LOCATION.getLocation();

        event.setJoinMessage(null);

        if (player.hasPlayedBefore()) return;

        player.teleport(spawn);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

}
