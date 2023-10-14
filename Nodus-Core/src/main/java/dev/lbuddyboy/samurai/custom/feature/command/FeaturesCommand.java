package dev.lbuddyboy.samurai.custom.feature.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.custom.feature.menu.FeatureMenu;
import org.bukkit.entity.Player;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 19/02/2022 / 1:37 AM
 * SteelHCF-master / dev.lbuddyboy.samurai.custom.feature.command
 */

@CommandAlias("features")
@CommandPermission("op")
public class FeaturesCommand extends BaseCommand {

	@Default
	public static void managefeatures(Player sender) {
		new FeatureMenu().openMenu(sender);
	}

}
