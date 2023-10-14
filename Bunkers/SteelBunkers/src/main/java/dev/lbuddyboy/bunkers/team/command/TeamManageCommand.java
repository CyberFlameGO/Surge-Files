package dev.lbuddyboy.bunkers.team.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Subcommand;
import dev.lbuddyboy.bunkers.Bunkers;
import dev.lbuddyboy.bunkers.command.claim.ClaimCommand;
import dev.lbuddyboy.bunkers.team.Team;
import dev.lbuddyboy.bunkers.util.StringUtils;
import dev.lbuddyboy.bunkers.util.Cuboid;
import dev.lbuddyboy.bunkers.util.bukkit.CC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 19/03/2022 / 8:54 PM
 * SteelBunkers / com.steelpvp.bunkers.team.command
 */

@CommandAlias("manageteam")
@CommandPermission("op")
public class TeamManageCommand extends BaseCommand {

	@Subcommand("claimfor")
	public static void teamClaimFor(Player sender, @Name("team") String teamName) {

		Team team = Bunkers.getInstance().getTeamHandler().getTeam(teamName);

		if (team == null) {
			sender.sendMessage(CC.translate(Bunkers.PREFIX + "ERROR: Could not find that team name. Options:"));
			sender.sendMessage(CC.translate("> Red, Green, Yellow, & Blue"));
			return;
		}

		Location loc1 = ClaimCommand.pos1.get(sender.getName());
		Location loc2 = ClaimCommand.pos2.get(sender.getName());

		if (loc1 == null) {
			sender.sendMessage(CC.translate(Bunkers.PREFIX + "ERROR: Could not find a position 1. Options:"));
			sender.sendMessage(CC.translate("> /setpos1"));
			return;
		}

		if (loc2 == null) {
			sender.sendMessage(CC.translate(Bunkers.PREFIX + "ERROR: Could not find a position 2. Options:"));
			sender.sendMessage(CC.translate("> /setpos2"));
			return;
		}

		Cuboid cuboid = new Cuboid(loc1, loc2);
		team.setCuboid(cuboid);

		sender.sendMessage(CC.translate(Bunkers.PREFIX + "&aSuccessfully set the cuboid region for this teams claim."));

	}

	@Subcommand("sethome")
	public static void teamSetHome(Player sender, @Name("team") String teamName) {

		Team team = Bunkers.getInstance().getTeamHandler().getTeam(teamName);

		if (team == null) {
			sender.sendMessage(CC.translate(Bunkers.PREFIX + "ERROR: Could not find that team name. Options:"));
			sender.sendMessage(CC.translate("> Red, Green, Yellow, & Blue"));
			return;
		}

		team.setHome(sender.getLocation());
		team.save();

		sender.sendMessage(CC.translate(Bunkers.PREFIX + "&aSuccessfully set the home for this team."));

	}
	@Subcommand("setshop")
	public static void setenchantshop(Player sender, @Name("shop") String shop, @Name("team") String teamName) {

		Team team = Bunkers.getInstance().getTeamHandler().getTeam(teamName);

		if (team == null) {
			sender.sendMessage(CC.translate(Bunkers.PREFIX + "ERROR: Could not find that team name. Options:"));
			sender.sendMessage(CC.translate("> Red, Green, Yellow, Central, & Blue"));
			return;
		}

		List<String> strings = Arrays.asList("enchantment", "build", "sell", "combat");
		if (!strings.contains(shop.toLowerCase())) {
			sender.sendMessage(CC.translate("&cInvalid Type:"));
			sender.sendMessage(CC.translate("&7" + StringUtils.join(strings, ", ")));
			return;
		}

		if (shop.equalsIgnoreCase("enchantment")) {
			team.setEnchantShopSpawn(sender.getLocation());
		} else if (shop.equalsIgnoreCase("build")) {
			team.setBuildShopSpawn(sender.getLocation());
		} else if (shop.equalsIgnoreCase("sell")) {
			team.setSellShopSpawn(sender.getLocation());
		} else if (shop.equalsIgnoreCase("combat")) {
			team.setCombatShopSpawn(sender.getLocation());
		}
		team.save();

		sender.sendMessage(CC.translate(Bunkers.PREFIX + "&aSuccessfully set the " + shop + " shop for this team."));

	}

	@Subcommand("scanores")
	public static void scanOres(Player sender) {
		for (Team team : Bunkers.getInstance().getTeamHandler().getTeams().values()) {
			if (team.getCuboid() == null) continue;
			for (Block block : team.getCuboid()) {
				if (block.getType() == Material.IRON_ORE || block.getType() == Material.COAL_ORE || block.getType() == Material.DIAMOND_ORE || block.getType() == Material.GOLD_ORE) {
					if (team.getLocations().containsKey(block.getType())) {
						List<Location> locations = team.getLocations().get(block.getType());
						locations.add(block.getLocation());
						team.getLocations().put(block.getType(), locations);
					} else {
						List<Location> locations = new ArrayList<>();
						locations.add(block.getLocation());
						team.getLocations().put(block.getType(), locations);
					}
				}
			}
			team.save();
		}
	}

}
