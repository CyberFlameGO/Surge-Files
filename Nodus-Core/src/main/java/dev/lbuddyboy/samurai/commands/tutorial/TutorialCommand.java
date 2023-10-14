package dev.lbuddyboy.samurai.commands.tutorial;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import dev.lbuddyboy.samurai.listener.EndListener;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.entity.Player;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 13/03/2022 / 12:04 PM
 * SteelHCF-master / dev.lbuddyboy.samurai.commands.tutorial
 */

@CommandAlias("tutorial")
public class TutorialCommand extends BaseCommand {

	@Default
	public static void tutorial(Player sender) {
		if (!DTRBitmask.SAFE_ZONE.appliesAt(sender.getLocation())) return;

		sender.teleport(EndListener.getTutorialLoc());
	}

	@Subcommand("setlocation")
	@CommandPermission("foxtrot.tutorial")
	public static void tutorialSetLoc(Player sender) {
		sender.sendMessage(CC.translate("&aSet the tutorial location at your current location."));
		EndListener.setTutorialLoc(sender.getLocation());
		EndListener.saveTutorialLoc();
	}

}
