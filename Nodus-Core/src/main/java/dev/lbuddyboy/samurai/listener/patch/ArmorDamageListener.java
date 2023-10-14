package dev.lbuddyboy.samurai.listener.patch;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;

public class ArmorDamageListener implements Listener {

    private static ThreadLocalRandom localRandom = ThreadLocalRandom.current();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onItemDamage(PlayerItemDamageEvent e) {

        if (localRandom.nextInt(100) >= 15) {
            e.setCancelled(true);
        }
        
    }
}
