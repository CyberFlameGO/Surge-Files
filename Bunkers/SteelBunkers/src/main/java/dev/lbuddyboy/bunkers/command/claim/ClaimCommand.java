package dev.lbuddyboy.bunkers.command.claim;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import dev.lbuddyboy.bunkers.Bunkers;
import dev.lbuddyboy.bunkers.util.bukkit.CC;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 19/03/2022 / 8:54 PM
 * SteelBunkers / com.steelpvp.bunkers.command.claim
 */

@CommandAlias("claim")
@CommandPermission("op")
public class ClaimCommand extends BaseCommand {

	public static Map<String, Location> pos1 = new ConcurrentHashMap<>();
	public static Map<String, Location> pos2 = new ConcurrentHashMap<>();

	@Subcommand("pos1|setpos1")
	public static void pos1(Player sender) {
		pos1.put(sender.getName(), sender.getLocation());

		sender.sendMessage(CC.translate(Bunkers.PREFIX + "Position 1 set at your current location."));

	}

	@Subcommand("pos2|setpos2")
	public static void pos2(Player sender) {
		Location location = pos2.put(sender.getName(), sender.getLocation());

		sender.sendMessage(CC.translate(Bunkers.PREFIX + "Position 2 set at your current location."));

	}

}
