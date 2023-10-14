package dev.lbuddyboy.communicate.database.redis.packets;

import dev.lbuddyboy.communicate.BunkersCom;
import dev.lbuddyboy.communicate.database.redis.JedisPacket;
import dev.lbuddyboy.communicate.profile.Profile;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 20/12/2021 / 1:20 AM
 * lCore-Premium / me.lbuddyboy.core.database.redis.packets
 */

@AllArgsConstructor
public class UserUpdatePacket implements JedisPacket {

	private Profile newProfile;

	@Override
	public void onReceive() {
		if (Bukkit.getPlayer(newProfile.getUniqueId()) != null) return;

		Bukkit.getScheduler().runTaskAsynchronously(BunkersCom.getInstance(), () -> {
			BunkersCom.getInstance().getProfileHandler().getProfiles().put(newProfile.getUniqueId(), newProfile);
		});
	}

	@Override
	public String getID() {
		return "User Update";
	}
}
