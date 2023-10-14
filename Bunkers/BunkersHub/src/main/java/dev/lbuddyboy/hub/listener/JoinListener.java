package dev.lbuddyboy.hub.listener;

import dev.lbuddyboy.hub.lHub;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 22/09/2021 / 9:43 PM
 * LBuddyBoy-Development / me.lbuddyboy.hub.listener
 */
public class JoinListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		lHub.getInstance().getItemHandler().setItems(event.getPlayer());
	}

}
