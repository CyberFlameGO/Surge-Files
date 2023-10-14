package dev.lbuddyboy.hub.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.hub.games.GameProfileMenu;
import dev.lbuddyboy.hub.lHub;
import dev.lbuddyboy.hub.util.CC;
import dev.lbuddyboy.hub.util.CompMaterial;
import dev.lbuddyboy.hub.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 22/09/2021 / 1:46 AM
 * LBuddyBoy-Development / me.lbuddyboy.hub.command
 */

@CommandAlias("stats")
public class StatsCommand extends BaseCommand {

	@Default
	public static void enable(Player sender) {
		new GameProfileMenu().openMenu(sender);
	}

}
