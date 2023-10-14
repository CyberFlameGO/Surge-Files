package dev.lbuddyboy.hub.rank;

import dev.lbuddyboy.hub.lHub;
import org.bukkit.ChatColor;

import java.util.UUID;

public interface RankCore {

	String id();
	String getRankName(UUID player);
	String getRankDisplayName(UUID player);
	int getRankWeight(UUID player);
	ChatColor getRankColor(UUID player);
	String getPrefix(UUID player);
	String getSuffix(UUID player);

	default RankCoreHandler getHandler() {
		return lHub.getInstance().getRankCoreHandler();
	}

}
