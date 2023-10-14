package dev.lbuddyboy.samurai.commands.staff.donator;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 15/01/2022 / 1:15 AM
 * SteelHCF-master / dev.lbuddyboy.samurai.commands.staff.donator
 */

@CommandAlias("givemultiplied")
@CommandPermission("op")
public class DonatorTimerCommand extends BaseCommand {
	
	public static String X3_KEYS_DISPLAY = "Triple-Keys";
	public static String X2_AIRDROPS_DISPLAY = "Double-Airdrops";
	public static String MYSTERY_BOXES_DISPLAY = "Double-MysteryBox";

	@Subcommand("key")
	@CommandCompletion("@players")
	public static void givemultiplied(CommandSender sender, @Name("key") String key, @Name("player") OnlinePlayer target, @Name("amount") int amount) {
		int actualAmount = (isTripled() ? amount * 3 : amount);
		Bukkit.dispatchCommand(sender, "crate give " + key + " " + target.getPlayer().getName() + " " + actualAmount);
	}

	@Subcommand("mysterybox")
	@CommandCompletion("@players")
	public static void givemultipliedBox(CommandSender sender, @Name("player") OnlinePlayer target, @Name("key") String crate, @Name("amount") int amount) {
		int actualAmount = (isTripledBoxes() ? amount * 5 : amount);
		Bukkit.dispatchCommand(sender, "ac give " + target.getPlayer().getName() + " " + crate + " " + actualAmount);
	}

	public static boolean isTripled() {
		return Samurai.getInstance().getTimerHandler().getServerTimers().containsKey(X3_KEYS_DISPLAY);
	}
	public static boolean isTripledBoxes() {
		return Samurai.getInstance().getTimerHandler().getServerTimers().containsKey(MYSTERY_BOXES_DISPLAY);
	}
	public static boolean isDoubledAirdrops() {
		return Samurai.getInstance().getTimerHandler().getServerTimers().containsKey(X2_AIRDROPS_DISPLAY);
	}


}
