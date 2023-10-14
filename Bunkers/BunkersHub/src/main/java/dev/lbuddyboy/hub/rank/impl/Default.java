package dev.lbuddyboy.hub.rank.impl;

import dev.lbuddyboy.hub.rank.RankCore;
import org.bukkit.ChatColor;

import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 08/11/2021 / 12:20 AM
 * LBuddyBoy-Development / me.lbuddyboy.hub.rank.impl
 */
public class Default implements RankCore {
	@Override
	public String id() {
		return "None";
	}

	@Override
	public String getRankName(UUID player) {
		return "None";
	}

	@Override
	public String getRankDisplayName(UUID player) {
		return "None";
	}

	@Override
	public int getRankWeight(UUID player) {
		return 0;
	}

	@Override
	public ChatColor getRankColor(UUID player) {
		return ChatColor.GRAY;
	}

	@Override
	public String getPrefix(UUID player) {
		return "None";
	}

	@Override
	public String getSuffix(UUID player) {
		return "None";
	}
}
