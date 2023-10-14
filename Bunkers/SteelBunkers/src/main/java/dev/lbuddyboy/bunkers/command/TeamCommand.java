package dev.lbuddyboy.bunkers.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import dev.lbuddyboy.bunkers.Bunkers;
import dev.lbuddyboy.bunkers.game.user.GameUser;
import dev.lbuddyboy.bunkers.team.Team;
import dev.lbuddyboy.bunkers.util.PagedItem;
import dev.lbuddyboy.bunkers.util.StringUtils;
import dev.lbuddyboy.bunkers.util.bukkit.CC;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 19/03/2022 / 6:59 PM
 * SteelBunkers / com.steelpvp.bunkers.command
 */

@CommandPermission("team|t|f|faction|fac")
public class TeamCommand extends BaseCommand {

	@Default
	@HelpCommand
	@Subcommand("help")
	public static void defp(CommandSender sender, @Name("help") @Optional CommandHelp help) {
		int page = help.getPage();
		List<String> header = CC.translate(Arrays.asList(
				" ",
				"&6&lTeam Command Help"
		));
		java.util.List<String> entries = new ArrayList<>();

		for (HelpEntry entry : help.getHelpEntries()) {
			entries.add("&e/" + entry.getCommand() + " " + entry.getParameterSyntax() + " &7- " + entry.getDescription());
		}

		PagedItem pagedItem = new PagedItem(entries, header, 10);

		pagedItem.send(help.getIssuer().getIssuer(), page);

		help.getIssuer().sendMessage(" ");
		help.getIssuer().sendMessage("&7You can do /f help <page> - You're currently viewing on page #" + page + ".");
		help.getIssuer().sendMessage(" ");
	}

	@Default
	public static void pay(Player sender, @Name("target") UUID target, @Name("amount") double amount) {

		if (!Bunkers.getInstance().getGameHandler().getGameUser(sender.getUniqueId()).hasBalance(amount)) {
			sender.sendMessage(CC.translate("&cYou do not have funds for this."));
			return;
		}

		GameUser user = Bunkers.getInstance().getGameHandler().getGameUser(target);
		user.addBalance(amount);
		Bunkers.getInstance().getGameHandler().getGameUser(sender.getUniqueId()).takeBalance(amount);
	}

	@Subcommand("rally")
	@Description("show your location to lunar client members")
	public static void rally(Player sender) {
		Team team = Bunkers.getInstance().getTeamHandler().getTeam(sender);
		if (team == null) {
			sender.sendMessage(ChatColor.GRAY + "You must be on a team to run this command.");
			return;
		}

		if (team.getRallyTask() != null) {
			team.getRallyTask().cancel();
			team.setRallyTask(null);
		}

		if (team.getRally() != null) {
			LCWaypoint waypoint = new LCWaypoint("Rally", team.getRally(), Color.yellow.hashCode(), true);
			team.getOnlineMembers().forEach(m -> {
				LunarClientAPI.getInstance().removeWaypoint(m, waypoint);
			});
		}

		team.getOnlineMembers().forEach(p -> p.sendMessage(ChatColor.DARK_AQUA + sender.getName() + " has updated the team's rally point! This will last for 1 minute."));
		LCWaypoint waypoint = new LCWaypoint("Rally", sender.getLocation(), Color.yellow.hashCode(), true);
		team.getOnlineMembers().forEach(m -> {
			LunarClientAPI.getInstance().sendWaypoint(m, waypoint);
		});
		team.setRally(sender.getLocation());

		BukkitTask task = new BukkitRunnable() {
			@Override
			public void run() {
				LCWaypoint waypoint = new LCWaypoint("Rally", sender.getLocation(), Color.yellow.hashCode(), true);
				team.getOnlineMembers().forEach(m -> {
					LunarClientAPI.getInstance().removeWaypoint(m, waypoint);
					team.setRally(null);
				});
			}
		}.runTaskLater(Bunkers.getInstance(), 20 * 60);
		team.setRallyTask(task);
	}

	@Subcommand("chat|c")
	@Description("chat so only your team can see")
	public static void chat(Player sender, @Name("chat") @Optional String message) {
		if (message == null) message = "nothing";

		Team team = Bunkers.getInstance().getTeamHandler().getTeam(sender);

		if (team == null) {
			sender.sendMessage(CC.translate("&cYou do not have a team."));
			return;
		}

		if (message.equalsIgnoreCase("nothing")) {
			if (sender.hasMetadata("teamchat")) {
				sender.removeMetadata("teamchat", Bunkers.getInstance());
				sender.sendMessage(CC.translate("&3Team Chat&f: &cOff"));
			} else {
				sender.setMetadata("teamchat", new FixedMetadataValue(Bunkers.getInstance(), true));
				sender.sendMessage(CC.translate("&3Team Chat&f: &aOn"));
			}
		} else {
			for (Player player : team.getOnlineMembers()) {
				player.sendMessage(CC.translate("&g&l[TEAM CHAT] &g" + sender.getName() + "&7: &f" + message));
			}
		}
	}

	@Subcommand("info|i|who|w")
	@CommandCompletion("@team")
	@Description("show info of a team")
	public static void info(Player sender, @Name("team") @Optional Team team) {
		if (team == null) team = Bunkers.getInstance().getTeamHandler().getTeam(sender);

		if (team == null) {
			sender.sendMessage(CC.translate("&cNo team or player with that team could be found."));
			return;
		}

		sender.sendMessage(CC.translate(" "));
		sender.sendMessage(CC.translate("" + team.getName() + " ᴛᴇᴀᴍ"));
		sender.sendMessage(CC.translate("&fPlayers&7: " + team.getColor() + StringUtils.join(team.getOnlineMembers().stream().map(HumanEntity::getName).collect(Collectors.toList()), ", ")));
		sender.sendMessage(CC.translate("&fDeaths Until Raidable&7: &r" + team.getDTRFormatted()));
		sender.sendMessage(CC.translate("&fHead Quarters&7: " + team.getColor() + "" + team.getHome().getBlockX() + ", " + team.getHome().getBlockZ()));
		sender.sendMessage(CC.translate(" "));

	}
}
