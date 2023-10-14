package dev.lbuddyboy.hub.rank.impl;

import dev.lbuddyboy.hub.rank.RankCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 08/11/2021 / 12:20 AM
 * LBuddyBoy-Development / me.lbuddyboy.hub.rank.impl
 */
public class Vault implements RankCore {
	@Override
	public String id() {
		return "Vault";
	}

	@Override
	public String getRankName(UUID player) {
		return getHandler().getChat().getPrimaryGroup(Bukkit.getPlayer(player));
	}

	@Override
	public String getRankDisplayName(UUID player) {
		return getHandler().getChat().getPrimaryGroup(Bukkit.getPlayer(player));
	}

	@Override
	public int getRankWeight(UUID player) {
		return 0;
	}

	@Override
	public ChatColor getRankColor(UUID player) {
		return ChatColor.WHITE;
	}

	@Override
	public String getPrefix(UUID player) {
		return getHandler().getChat().getGroupPrefix("world", getRankName(player));
	}

	@Override
	public String getSuffix(UUID player) {
		return getHandler().getChat().getGroupSuffix("world", getRankName(player));
	}
}
