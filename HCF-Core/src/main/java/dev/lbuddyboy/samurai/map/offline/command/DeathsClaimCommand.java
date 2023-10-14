package dev.lbuddyboy.samurai.map.offline.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.feature.Feature;
import dev.lbuddyboy.samurai.map.offline.OfflineHandler;
import dev.lbuddyboy.samurai.map.offline.menu.DeathsClaimMenu;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 01/03/2022 / 10:59 AM
 * SteelHCF-master / dev.lbuddyboy.samurai.map.offline.command
 */

@CommandAlias("refundclaim|deathsclaim")
public class DeathsClaimCommand extends BaseCommand {

	@Default
	public static void deaths(Player sender, @Name("player") @Optional UUID target) {
		if (target != null && target != sender.getUniqueId() && !sender.hasPermission("foxtrot.staff")) {
			sender.sendMessage(CC.translate("&cYou don't have permission to open someone else's death claim!"));
			return;
		}

		if (target == null) target = sender.getUniqueId();

		new DeathsClaimMenu(target).openMenu(sender);
	}

}
