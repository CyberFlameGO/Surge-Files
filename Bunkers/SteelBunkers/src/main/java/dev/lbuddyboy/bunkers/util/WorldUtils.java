package dev.lbuddyboy.bunkers.util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 26/03/2022 / 10:36 AM
 * SteelBunkers / com.steelpvp.bunkers.util
 */
public class WorldUtils {

	public static void butcher() {
		for (World world : Bukkit.getWorlds()) {
			for (Entity entity : world.getEntities()) {
				if (entity instanceof Player) continue;
				entity.remove();
			}
		}
	}

}
