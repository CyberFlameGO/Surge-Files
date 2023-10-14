package dev.lbuddyboy.samurai.util.modsuite;

import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ModUtils {

	public static final String MOD_MODE_META = "modmode";
	public static final String INVISIBILITY_META = "invisible";

	public static final Set<UUID> hideStaff;
	static {
		hideStaff = new HashSet<>();
	}

	public static boolean isModMode(final Player player) {
		return player.hasMetadata(MOD_MODE_META);
	}

	public static boolean isInvisible(final Player player) {
		return player.hasMetadata(INVISIBILITY_META);
	}

	public static void showStaff(final Player player) {
		hideStaff.remove(player.getUniqueId());
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.hasMetadata("invisible")) {
				player.showPlayer(Samurai.getInstance(), p);
			}
		}
	}

	public static void hideStaff(final Player player) {
		hideStaff.add(player.getUniqueId());
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.hasMetadata("invisible")) {
				player.hidePlayer(Samurai.getInstance(), p);
			}
		}
	}

	public static void randomTeleport(final Player player) {
		List<Player> players = new ArrayList<>();

		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if (onlinePlayer == player || isModMode(onlinePlayer)) continue;
			players.add(onlinePlayer);
		}

		if (players.isEmpty()) {
			player.sendMessage(ChatColor.RED + "No players to teleport to.");
			return;
		}

		player.teleport(players.get(ThreadLocalRandom.current().nextInt(players.size())));
	}

}