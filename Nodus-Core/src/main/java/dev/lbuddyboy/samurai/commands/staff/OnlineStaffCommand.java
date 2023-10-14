package dev.lbuddyboy.samurai.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.commands.menu.onlinestaff.OnlineStaffMenu;
import org.bukkit.entity.Player;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 27/02/2022 / 12:03 PM
 * SteelHCF-master / dev.lbuddyboy.samurai.commands.staff
 */

@CommandAlias("onlinestaff")
@CommandPermission("foxtrot.staff")
public class OnlineStaffCommand extends BaseCommand {

	@Default
	public static void onlineStaff(Player sender) {
		new OnlineStaffMenu().openMenu(sender);
	}

}
