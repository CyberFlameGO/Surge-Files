package dev.lbuddyboy.hub.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import dev.lbuddyboy.hub.lHub;
import dev.lbuddyboy.hub.lModule;
import dev.lbuddyboy.hub.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 22/09/2021 / 1:46 AM
 * LBuddyBoy-Development / me.lbuddyboy.hub.command
 */

@CommandAlias("lhub|hubcore|lhubcore")
@CommandPermission("lhub.admin")
public class lHubCommand extends BaseCommand {

	@Subcommand("setspawn")
	public void setspawn(Player sender) {
		lHub.getInstance().getSettingsHandler().setSpawnLocation(sender.getLocation());
		Bukkit.getOnlinePlayers().forEach(player -> player.teleport(lHub.getInstance().getSettingsHandler().getSpawnLocation()));

		sender.sendMessage(CC.translate("&aSuccessfully set the new world spawn."));
	}

	@Subcommand("reload|rl")
	@CommandPermission("lhub.admin")
	public void reload(CommandSender sender) throws IOException {

		lHub.getInstance().getModules().forEach(lModule::save);
		lHub.getInstance().getModules().forEach(lModule::reload);

		sender.sendMessage(CC.translate("&aSuccessfully reloaded all the configs."));
	}


	@Subcommand("build|buildmode")
	@CommandPermission("lhub.admin")
	public void buildmode(Player sender) {
		if (lHub.getInstance().getSettingsHandler().getBuildModes().contains(sender)) {
			sender.sendMessage(CC.translate("&cYou have just %status% your build mode.".replaceAll("%status%", "disabled")));
			lHub.getInstance().getSettingsHandler().getBuildModes().remove(sender);
			return;
		}
		sender.sendMessage(CC.translate("&aYou have just %status% your build mode.".replaceAll("%status%", "enabled")));
		lHub.getInstance().getSettingsHandler().getBuildModes().add(sender);
	}
}
