package dev.lbuddyboy.hub.queue.custom;

import dev.lbuddyboy.hub.lHub;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 08/11/2021 / 12:38 AM
 * LBuddyBoy-Development / me.lbuddyboy.hub.queue.custom
 */
public class QueueListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		CustomQueue queue = lHub.getInstance().getQueueHandler().getQueueByPlayer(event.getPlayer());
		if (queue != null) {
			lHub.getInstance().getQueueHandler().getPlayerTaskMap().get(event.getPlayer().getUniqueId()).cancel();
			lHub.getInstance().getQueueHandler().getPlayerTaskMap().remove(event.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		CustomQueue queue = lHub.getInstance().getQueueHandler().getQueueByPlayer(event.getPlayer());
		if (queue != null) {
			queue.removeFromQueue(event.getPlayer());
		}
	}

}
