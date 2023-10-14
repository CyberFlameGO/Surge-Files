package dev.lbuddyboy.gkits.command;

import dev.lbuddyboy.gkits.enchanter.object.EnchantBook;
import dev.lbuddyboy.gkits.enchants.object.CustomEnchant;
import dev.lbuddyboy.gkits.lGKits;
import dev.lbuddyboy.gkits.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 06/09/2021 / 10:24 PM
 * GKits / rip.orbit.gkits.command
 */
public class GiveGemCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

		if (!sender.hasPermission("gkitz.givegem")) {
			sender.sendMessage(CC.translate("&cNo permission"));
			return false;
		}

		if (args.length < 3) {
			sender.sendMessage(CC.translate("&cCorrect Usage: /" + s + " <player> <level> <book>"));
			return false;
		}

		Player target = Bukkit.getPlayer(args[0]);

		if (target == null) {
			sender.sendMessage(ChatColor.RED + "Could not find a player with that name.");
			return false;
		}

		int level = Integer.parseInt(args[1]);
		CustomEnchant enchant = lGKits.getInstance().getCustomEnchantManager().byName(args[2]);

		if (enchant == null) {
			sender.sendMessage(ChatColor.RED + "Could not find an enchant with that name.");
			return false;
		}

		EnchantBook book = new EnchantBook(enchant, level);
		target.getInventory().addItem(book.build());
		target.sendMessage(CC.translate("&aYou have just received a " + book.getEnchant().displayName() + " " + book.getLevel() + "&a book."));

		return false;
	}


	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String msg, String[] args) {

		List<String> completes = new ArrayList<>();

		if (args.length == 0) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (msg.toLowerCase().startsWith(player.getName().toLowerCase())) {
					completes.add(player.getName());
				}
			}
		} else {
			for (CustomEnchant enchant : lGKits.getInstance().getCustomEnchantManager().getEnchants()) {
				if (msg.toLowerCase().startsWith(enchant.name().toLowerCase())) {
					completes.add(enchant.name());
				}
			}
		}

		return completes;
	}
}
