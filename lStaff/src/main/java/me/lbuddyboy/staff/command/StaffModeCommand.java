package me.lbuddyboy.staff.command;


import me.lbuddyboy.staff.lStaff;
import me.lbuddyboy.staff.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffModeCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("ostaff.staff")) {
			sender.sendMessage(CC.chat("&cNo permission."));
			return false;
		}
		if (args.length > 0) {
			if (!sender.hasPermission("staff.others")) {
				sender.sendMessage(CC.chat("&cNo permission."));
				return false;
			}
			Player target = Bukkit.getPlayer(args[0]);
			if (target == null) {
				sender.sendMessage(CC.chat("&cCould not locate that player."));
				return false;
			}
			if (lStaff.getInstance().getStaffModeHandler().inStaffMode(target)) {
				lStaff.getInstance().getStaffModeHandler().unloadStaffMode(target);
				sender.sendMessage(CC.chat("&fYou have just &cdisabled&f " + target.getName() + " mod mode."));
			} else {
				lStaff.getInstance().getStaffModeHandler().loadStaffMode(target);
				sender.sendMessage(CC.chat("&fYou have just &genabled&f " + target.getName() + " mod mode."));
			}
			return false;
		}
		if (((Player) sender).getGameMode() == GameMode.SPECTATOR) {
			lStaff.getInstance().getStaffModeHandler().loadStaffMode((Player) sender);
			sender.sendMessage(CC.chat("&fYou have just &genabled&f your mod mode."));
			sender.sendMessage(CC.chat("&7&oYou can edit your staff mode item's slots & availability by running /editstaff"));
			return true;
		}
		if (lStaff.getInstance().getStaffModeHandler().inStaffMode((Player) sender)) {
			lStaff.getInstance().getStaffModeHandler().unloadStaffMode((Player) sender);

		} else {
			lStaff.getInstance().getStaffModeHandler().loadStaffMode((Player) sender);
			sender.sendMessage(CC.chat("&fYou have just &genabled&f your mod mode."));
			sender.sendMessage(CC.chat("&7&oYou can edit your staff mode item's slots & availability by running /editstaff"));

		}
		return false;
	}
}
