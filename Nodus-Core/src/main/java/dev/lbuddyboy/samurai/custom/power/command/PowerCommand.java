package dev.lbuddyboy.samurai.custom.power.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Subcommand;
import dev.lbuddyboy.samurai.custom.feature.Feature;
import dev.lbuddyboy.samurai.custom.power.listener.PowerListener;
import dev.lbuddyboy.samurai.custom.power.menu.PowerMenu;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("power")
public class PowerCommand extends BaseCommand {

	@Subcommand("resetcd|resetcooldown")
	@CommandPermission("foxtrot.admin")
	public static void ex(CommandSender sender, @Name("target") Player target) {
		PowerListener.powerCooldown.removeCooldown(target);
	}

	@Subcommand("resetselected|resetselectedpower|resetpower")
	@CommandPermission("foxtrot.admin")
	public static void reset(CommandSender sender, @Name("target") UUID target) {
		Samurai.getInstance().getPowerHandler().getPowerMap().setPower(target, "None");
	}

	@Subcommand("selector|powers|selection")
	public static void exec(Player sender) {
		if (Feature.POWER.isDisabled()) {
			sender.sendMessage(CC.translate("&cThis feature is currently disabled."));
			return;
		}

		if (Samurai.getInstance().getPowerHandler().hasPower(sender)) {
			sender.sendMessage(CC.translate("&cYou cannot do this, as you've already selected a power."));
			return;
		}
		new PowerMenu().openMenu(sender);
	}

}
