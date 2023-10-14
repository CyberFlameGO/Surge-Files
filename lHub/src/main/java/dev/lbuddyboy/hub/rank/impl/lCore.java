package dev.lbuddyboy.hub.rank.impl;

import me.lbuddyboy.core.api.lCoreAPI;
import dev.lbuddyboy.hub.rank.RankCore;
import org.bukkit.ChatColor;

import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 08/11/2021 / 12:20 AM
 * LBuddyBoy-Development / me.lbuddyboy.hub.rank.impl
 */
public class lCore implements RankCore {
	@Override
	public String id() {
		return "lCore";
	}

	@Override
	public String getRankName(UUID player) {
		return lCoreAPI.getProfileByUUID(player).getCurrentRank().getName();
	}

	@Override
	public String getRankDisplayName(UUID player) {
		return lCoreAPI.getProfileByUUID(player).getCurrentRank().getDisplayName();
	}

	@Override
	public int getRankWeight(UUID player) {
		return lCoreAPI.getProfileByUUID(player).getCurrentRank().getWeight();
	}

	@Override
	public ChatColor getRankColor(UUID player) {
		return lCoreAPI.getProfileByUUID(player).getCurrentRank().getColor();
	}

	@Override
	public String getPrefix(UUID player) {
		return lCoreAPI.getProfileByUUID(player).getCurrentRank().getPrefix();
	}

	@Override
	public String getSuffix(UUID player) {
		return "";
	}
}
