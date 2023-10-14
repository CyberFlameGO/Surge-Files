package dev.lbuddyboy.samurai.map.offline.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Name;
import dev.lbuddyboy.samurai.custom.feature.Feature;
import dev.lbuddyboy.samurai.map.offline.OfflineInventory;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.offline.OfflineHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 01/03/2022 / 10:59 AM
 * SteelHCF-master / dev.lbuddyboy.samurai.map.offline.command
 */

@CommandAlias("oinvsee")
@CommandPermission("foxtrot.staff")
public class OfflineInvseeCommand extends BaseCommand {

	@Default
	public static void offlineInvsee(Player sender, @Name("player") UUID target) {

		if (Feature.OFFLINE_INVS.isDisabled()) {
			sender.sendMessage(CC.translate("&cThis feature is currently disabled."));
			return;
		}

		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(target);

		if (offlinePlayer.isOnline()) {
			sender.sendMessage(CC.translate("&cThat player is online. (Running /invsee...)"));
			sender.chat("/invsee " + offlinePlayer.getName());
			return;
		}

		OfflineHandler handler = Samurai.getInstance().getOfflineHandler();
		OfflineInventory offlineInventory = handler.getOfflineInventories().get(offlinePlayer.getUniqueId());
		Inventory inventory = offlineInventory.createInventory();

		sender.openInventory(inventory);
		handler.getEditMap().put(sender.getUniqueId(), inventory);

	}

}
