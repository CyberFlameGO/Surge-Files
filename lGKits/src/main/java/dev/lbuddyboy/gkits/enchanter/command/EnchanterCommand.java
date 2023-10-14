package dev.lbuddyboy.gkits.enchanter.command;

import dev.lbuddyboy.gkits.enchanter.menu.EnchanterMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 09/07/2021 / 10:59 PM
 * GKits / me.lbuddyboy.gkits.enchanter.command
 */
public class EnchanterCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		new EnchanterMenu().openMenu((Player) sender);

		return false;
	}
}
