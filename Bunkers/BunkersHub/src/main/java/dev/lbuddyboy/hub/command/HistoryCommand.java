package dev.lbuddyboy.hub.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.hub.games.GameHistoryMenu;
import dev.lbuddyboy.hub.games.GameProfileMenu;
import org.bukkit.entity.Player;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 22/09/2021 / 1:46 AM
 * LBuddyBoy-Development / me.lbuddyboy.hub.command
 */

@CommandAlias("gamehistory")
public class HistoryCommand extends BaseCommand {

	@Default
	public static void enable(Player sender) {
		new GameHistoryMenu().openMenu(sender);
	}

}
