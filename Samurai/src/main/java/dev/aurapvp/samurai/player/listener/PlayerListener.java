package dev.aurapvp.samurai.player.listener;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.essential.offline.OfflineData;
import dev.aurapvp.samurai.player.SamuraiPlayer;
import dev.aurapvp.samurai.util.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerListener implements Listener {

    @EventHandler
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
        if (!Samurai.isENABLED()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, CC.RED + "The server is still starting up.");
            return;
        }

        UUID uuid = event.getUniqueId();
        String name = event.getName();
        SamuraiPlayer player = Samurai.getInstance().getPlayerHandler().loadPlayer(uuid, false);

        if (player == null) {
            player = new SamuraiPlayer(uuid);
            player.updated();
        }

        player.setName(name);

        Samurai.getInstance().getPlayerHandler().getPlayers().put(uuid, player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        SamuraiPlayer samuraiPlayer = Samurai.getInstance().getPlayerHandler().getPlayers().getOrDefault(player.getUniqueId(), null);

        if (samuraiPlayer == null) return;

        /*
        Save offline inventories
         */

        OfflineData data = new OfflineData(player.getInventory().getArmorContents(), player.getInventory().getContents(), player.getLocation(), false);
        samuraiPlayer.setOfflineData(data);
        samuraiPlayer.updated();

        if (samuraiPlayer.wasUpdated()) samuraiPlayer.save(true);
        Samurai.getInstance().getPlayerHandler().getPlayers().remove(player.getUniqueId());
    }

}
