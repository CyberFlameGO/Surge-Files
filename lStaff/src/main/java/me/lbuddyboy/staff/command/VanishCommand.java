package me.lbuddyboy.staff.command;


import me.lbuddyboy.staff.lStaff;
import me.lbuddyboy.staff.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 19/07/2021 / 12:15 AM
 * oStaff / rip.orbit.ostaff.command
 */
public class VanishCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("lstaff.vanish")) {
			sender.sendMessage(CC.chat("&cNo permission."));
			return false;
		}

		if (args.length > 0) {
			Player target = Bukkit.getPlayer(args[0]);
			if (target == null) {
				sender.sendMessage(CC.chat("&cCould not locate that player."));
				return false;
			}
			if (lStaff.getInstance().getStaffModeHandler().inStaffMode(target)) {
				lStaff.getInstance().getStaffModeHandler().unloadVanish((Player) sender);
				sender.sendMessage(CC.chat("&fYou have just &cdisabled&f " + target.getName() + " vanish."));
			} else {
				lStaff.getInstance().getStaffModeHandler().loadVanish((Player) sender);
				sender.sendMessage(CC.chat("&fYou have just &genabled&f " + target.getName() + " vanish."));
			}
			return false;
		}
		if (lStaff.getInstance().getStaffModeHandler().isVanished((Player) sender)) {
			lStaff.getInstance().getStaffModeHandler().unloadVanish((Player) sender);
			sender.sendMessage(CC.chat("&eVanish: &cDisabled"));
		} else {
			lStaff.getInstance().getStaffModeHandler().loadVanish((Player) sender);
			sender.sendMessage(CC.chat("&eVanish: &aEnabled"));
		}
		return false;
	}
}