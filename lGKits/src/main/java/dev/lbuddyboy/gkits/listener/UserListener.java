package dev.lbuddyboy.gkits.listener;

import dev.lbuddyboy.gkits.object.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 09/07/2021 / 1:44 PM
 * GKits / me.lbuddyboy.gkits.listener
 */
public class UserListener implements Listener {

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		event.getEntity().getActivePotionEffects().clear();
	}

	@EventHandler
	public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {

		User profile = new User(event.getUniqueId());

		User.getUsers().put(profile.getUuid(), profile);

	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		User user = User.getUsers().remove(event.getPlayer().getUniqueId());
		if (user == null) return;
		if (user.isNeedsSave()) {
			user.save(true);
			user.setNeedsSave(false);
		}
	}

}
