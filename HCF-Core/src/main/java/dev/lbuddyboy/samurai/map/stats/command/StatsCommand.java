package dev.lbuddyboy.samurai.map.stats.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.samurai.persist.maps.PlaytimeMap;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import dev.lbuddyboy.samurai.util.TimeUtils;
import dev.lbuddyboy.samurai.util.UUIDUtils;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.stats.StatsEntry;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("stats")
public class StatsCommand extends BaseCommand {

	@Default
	@CommandCompletion("@players")
	public static void stats(Player sender, @Name("player") @Optional UUID uuid) {
		if (uuid == null) uuid = sender.getUniqueId();
		StatsEntry stats = Samurai.getInstance().getMapHandler().getStatsHandler().getStats(uuid);

		if (stats == null) {
			sender.sendMessage(ChatColor.RED + "Player not found.");
			return;
		}

		sender.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("⎯", 45));
		sender.sendMessage(CC.GOLD + UUIDUtils.name(uuid));

		sender.sendMessage(CC.GRAY + " " + SymbolUtil.UNICODE_ARROW_RIGHT + " " + CC.YELLOW + "Kills: " + ChatColor.WHITE + stats.getKills());
		sender.sendMessage(CC.GRAY + " " + SymbolUtil.UNICODE_ARROW_RIGHT + " " + CC.YELLOW + "Deaths: " + ChatColor.WHITE + stats.getDeaths());
		sender.sendMessage(CC.GRAY + " " + SymbolUtil.UNICODE_ARROW_RIGHT + " " + CC.YELLOW + "KDR: " + ChatColor.WHITE + (stats.getDeaths() == 0 ? "Infinity" : Team.DTR_FORMAT.format((double) stats.getKills() / (double) stats.getDeaths())));
		sender.sendMessage(CC.GRAY + " " + SymbolUtil.UNICODE_ARROW_RIGHT + " " + CC.YELLOW + "Shards: " + ChatColor.WHITE + Samurai.getInstance().getShardMap().getShards(uuid));

		PlaytimeMap playtime = Samurai.getInstance().getPlaytimeMap();
		int playtimeTime = (int) playtime.getPlaytime(uuid);
		Player bukkitPlayer = Samurai.getInstance().getServer().getPlayer(uuid);

		if (bukkitPlayer != null && sender.canSee(bukkitPlayer)) {
			playtimeTime += playtime.getCurrentSession(bukkitPlayer.getUniqueId()) / 1000;
		}

		sender.sendMessage(CC.GRAY + " " + SymbolUtil.UNICODE_ARROW_RIGHT + " " + CC.YELLOW + "Playtime: " + ChatColor.WHITE + TimeUtils.formatIntoDetailedString(playtimeTime));

		sender.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("⎯", 45));
	}


}
