package dev.lbuddyboy.communicate.profile;

import dev.lbuddyboy.communicate.BunkersCom;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 23/09/2021 / 2:28 AM
 * LBuddyBoy Development / me.lbuddyboy.core.profile
 */
public class ProfileListener implements Listener {

	@EventHandler
	public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event) {

		Profile profile = new Profile(event.getUniqueId());
		profile.setName(event.getName());

		BunkersCom.getInstance().getProfileHandler().getProfiles().put(event.getUniqueId(), profile);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		Bukkit.getScheduler().runTaskAsynchronously(BunkersCom.getInstance(), () -> {
			Profile profile = BunkersCom.getInstance().getProfileHandler().getProfiles().get(player.getUniqueId());


			profile.save();
		});

	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		Bukkit.getScheduler().runTaskAsynchronously(BunkersCom.getInstance(), () -> {

			Profile profile = BunkersCom.getInstance().getProfileHandler().getByUUID(player.getUniqueId());

			profile.save();
			BunkersCom.getInstance().getProfileHandler().getProfiles().remove(profile.getUniqueId());
		});
	}

}
