package dev.lbuddyboy.hub.rank.impl;

import dev.lbuddyboy.flash.Flash;
import dev.lbuddyboy.hub.rank.RankCore;
import org.bukkit.ChatColor;

import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 08/11/2021 / 12:20 AM
 * LBuddyBoy-Development / me.lbuddyboy.hub.rank.impl
 */
public class FlashCore implements RankCore {
	@Override
	public String id() {
		return "Flash";
	}

	@Override
	public String getRankName(UUID player) {
		return Flash.getInstance().getUserHandler().tryUser(player, true).getActiveRank().getName();
	}

	@Override
	public String getRankDisplayName(UUID player) {
		return Flash.getInstance().getUserHandler().tryUser(player, true).getActiveRank().getDisplayName();
	}

	@Override
	public int getRankWeight(UUID player) {
		return Flash.getInstance().getUserHandler().tryUser(player, true).getActiveRank().getWeight();
	}

	@Override
	public ChatColor getRankColor(UUID player) {
		return Flash.getInstance().getUserHandler().tryUser(player, true).getActiveRank().getColor();
	}

	@Override
	public String getPrefix(UUID player) {
		return Flash.getInstance().getUserHandler().tryUser(player, true).getActiveRank().getPrefix();
	}

	@Override
	public String getSuffix(UUID player) {
		return Flash.getInstance().getUserHandler().tryUser(player, true).getActiveRank().getSuffix();
	}
}
