package dev.lbuddyboy.samurai.map.stats.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.google.common.collect.Lists;
import dev.lbuddyboy.samurai.team.Team;
import mkremins.fanciful.FancyMessage;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.killstreaks.Killstreak;
import dev.lbuddyboy.samurai.map.killstreaks.PersistentKillstreak;
import dev.lbuddyboy.samurai.map.stats.StatsEntry;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

@CommandAlias("killstreaks|ks|killstreak")
public class KillstreaksCommand extends BaseCommand {

	@Default
	public static void killstreaks(CommandSender sender) {
		if (!Samurai.getInstance().getMapHandler().isKitMap()) {
			sender.sendMessage(ChatColor.RED + "This is a KitMap only command.");
			return;
		}
		sender.sendMessage(ChatColor.RED.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("⎯", 45));

		List<Object> streaks = Lists.newArrayList(Samurai.getInstance().getMapHandler().getKillstreakHandler().getKillstreaks());
		streaks.addAll(Samurai.getInstance().getMapHandler().getKillstreakHandler().getPersistentKillstreaks());

		streaks.sort((first, second) -> {

			int firstNumber = first instanceof Killstreak ? ((Killstreak) first).getKills()[0] : ((PersistentKillstreak) first).getKillsRequired();
			int secondNumber = second instanceof Killstreak ? ((Killstreak) second).getKills()[0] : ((PersistentKillstreak) second).getKillsRequired();

			if (firstNumber < secondNumber) {
				return -1;
			}
			return 1;

		});

		for (Object ks : streaks) {
			String name = ks instanceof Killstreak ? ((Killstreak) ks).getName() : ((PersistentKillstreak) ks).getName();
			int kills = ks instanceof Killstreak ? ((Killstreak) ks).getKills()[0] : ((PersistentKillstreak) ks).getKillsRequired();

			if (name == null) {
				continue;
			}
			sender.sendMessage(ChatColor.YELLOW + name + ": " + ChatColor.RED + kills);
		}

		sender.sendMessage(ChatColor.RED.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("⎯", 45));
	}

	@Subcommand("active")
	public static void killstreaksActive(CommandSender sender) {
		Map<Player, Integer> sorted = getSortedPlayers();

		if (sorted.isEmpty()) {
			sender.sendMessage(ChatColor.GRAY + "There is no data to display.");
		} else {
			sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("⎯", 45));

			int index = 0;

			for (Map.Entry<Player, Integer> entry : getSortedPlayers().entrySet()) {
				if (index > 10) {
					break;
				}

				index++;

				Team team = Samurai.getInstance().getTeamHandler().getTeam(entry.getKey());

				FancyMessage playerMessage = new FancyMessage();
				playerMessage.text(index + ". ").color(ChatColor.GRAY).then();
				playerMessage.text(entry.getKey().getName()).color(sender instanceof Player && team != null && team.isMember(((Player) sender).getUniqueId()) ? ChatColor.GREEN : ChatColor.RED).then();
				playerMessage.text(" - ").color(ChatColor.YELLOW).then();
				playerMessage.text(entry.getValue() + " killstreak").color(ChatColor.GRAY);
				playerMessage.send(sender);
			}

			sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("⎯", 45));
		}
	}

	// Stole this from the /f top command - thanks to whoever wrote it!
	public static LinkedHashMap<Player, Integer> getSortedPlayers() {
		Map<Player, Integer> playerKillstreaks = new HashMap<>();

		for (Player player : Bukkit.getOnlinePlayers()) {
			StatsEntry stats = Samurai.getInstance().getMapHandler().getStatsHandler().getStats(player.getUniqueId());

			if (stats.getKillstreak() > 0) {
				playerKillstreaks.put(player, stats.getKillstreak());
			}
		}

		return sortByValues(playerKillstreaks);
	}

	public static LinkedHashMap<Player, Integer> sortByValues(Map<Player, Integer> map) {
		LinkedList<Map.Entry<Player, Integer>> list = new LinkedList<>(map.entrySet());

		Collections.sort(list, (o1, o2) -> (o2.getValue().compareTo(o1.getValue())));

		LinkedHashMap<Player, Integer> sortedHashMap = new LinkedHashMap<>();

		for (Map.Entry<Player, Integer> entry : list) {
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}

		return (sortedHashMap);
	}

}
