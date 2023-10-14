package dev.lbuddyboy.samurai.server.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.server.menu.LootTableMenu;
import org.bukkit.entity.Player;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 26/02/2022 / 2:46 AM
 * SteelHCF-master / dev.lbuddyboy.samurai.server.commands
 */

@CommandAlias("loottables")
public class LootTablesCommand extends BaseCommand {

	@Default
	public static void loottables(Player sender) {
		new LootTableMenu().openMenu(sender);
	}
}
