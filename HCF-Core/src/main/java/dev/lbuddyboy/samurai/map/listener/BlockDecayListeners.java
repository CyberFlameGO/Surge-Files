package dev.lbuddyboy.samurai.map.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;

public class BlockDecayListeners implements Listener {

    @EventHandler
    public void onLeavesDecayEvent(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

}
