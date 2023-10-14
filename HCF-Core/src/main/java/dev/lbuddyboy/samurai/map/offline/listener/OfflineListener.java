package dev.lbuddyboy.samurai.map.offline.listener;

import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class OfflineListener implements Listener {

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent event) {
        Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> {
            Samurai.getInstance().getOfflineHandler().load(event.getUniqueId());
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        ItemStack[] stacks = Samurai.getInstance().getOfflineHandler().getDeathsClaim().remove(event.getPlayer().getUniqueId());

        if (stacks == null) return;

        Samurai.getInstance().getOfflineHandler().setReviveContents(event.getPlayer().getUniqueId(), stacks);
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        ItemStack[] stacks = Samurai.getInstance().getOfflineHandler().getDeathsClaim().remove(event.getPlayer().getUniqueId());

        if (stacks == null) return;

        Samurai.getInstance().getOfflineHandler().setReviveContents(event.getPlayer().getUniqueId(), stacks);
    }

}
