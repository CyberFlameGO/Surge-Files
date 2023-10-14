package dev.lbuddyboy.samurai.custom.battlepass.listener;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.battlepass.BattlePassProgress;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BattlePassListeners implements Listener {

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        if (Samurai.getInstance().getBattlePassHandler().isAdminDisabled()) {
            return;
        }

        Player player = event.getPlayer();
        Bukkit.getServer().getScheduler().runTaskAsynchronously(Samurai.getInstance(), () -> {
            Samurai.getInstance().getBattlePassHandler().loadProgress(event.getPlayer().getUniqueId());

            if (player.hasPermission("reclaim.partner")) {
                BattlePassProgress progress = Samurai.getInstance().getBattlePassHandler().getProgress(player.getUniqueId());
                if (progress != null) {
                    if (!progress.isPremium()) {
                        progress.setPremium(true);
                    }
                }
            }
        });
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        if (Samurai.getInstance().getBattlePassHandler().isAdminDisabled()) {
            return;
        }

        Bukkit.getServer().getScheduler().runTaskAsynchronously(Samurai.getInstance(), () -> {
            Samurai.getInstance().getBattlePassHandler().unloadProgress(event.getPlayer().getUniqueId());
        });
    }

}
