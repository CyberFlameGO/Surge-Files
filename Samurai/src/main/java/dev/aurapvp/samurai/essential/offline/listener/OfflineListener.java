package dev.aurapvp.samurai.essential.offline.listener;

import dev.aurapvp.samurai.essential.offline.OfflineData;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.player.SamuraiPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class OfflineListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        OfflineData cache = Samurai.getInstance().getOfflineHandler().fetchCache(player.getUniqueId());

        if (cache != null && !cache.isEdited()) {
            SamuraiPlayer samuraiPlayer = Samurai.getInstance().getPlayerHandler().loadPlayer(player.getUniqueId(), true);
            samuraiPlayer.setOfflineData(null);
            samuraiPlayer.wasUpdated();
            return;
        }

        if (cache == null) return;

        player.getInventory().setArmorContents(cache.getArmor());
        player.getInventory().setContents(cache.getContents());
        player.teleport(cache.getLocation());
    }

}
