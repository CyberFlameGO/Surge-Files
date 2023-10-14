package dev.lbuddyboy.samurai.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.commands.menu.EditConfigurationMenu;
import org.bukkit.entity.Player;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 14/01/2022 / 11:29 PM
 * SteelHCF-master / dev.lbuddyboy.samurai.commands.staff
 */

@CommandAlias("editconfig")
@CommandPermission("op")
public class EditConfigurationCommand extends BaseCommand {

	@Default
	public static void editConfig(Player sender) {
		new EditConfigurationMenu().openMenu(sender);
	}

}
