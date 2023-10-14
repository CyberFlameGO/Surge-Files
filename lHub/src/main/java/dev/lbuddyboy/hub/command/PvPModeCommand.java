package dev.lbuddyboy.hub.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
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

@CommandAlias("pvpmode")
public class PvPModeCommand extends BaseCommand {

	@Default
	public static void enable(Player sender) {
		if (sender.hasMetadata("pvpmode")) {
			sender.removeMetadata("pvpmode", lHub.getInstance());
			sender.sendMessage(CC.translate("&cPvP Mode is now disabled."));
			lHub.getInstance().getItemHandler().setItems(sender);
			sender.teleport(lHub.getInstance().getSettingsHandler().getSpawnLocation());

			for (Player online : Bukkit.getOnlinePlayers()) {
				if (!online.hasMetadata("pvpmode")) continue;

				online.hidePlayer(sender);
				sender.hidePlayer(online);
			}
			return;
		}
		sender.getInventory().clear();

		sender.setMetadata("pvpmode", new FixedMetadataValue(lHub.getInstance(), true));
		sender.sendMessage(CC.translate("&aPvP Mode is now enabled."));

		ItemStack[] armor = new ItemStack[4];

		armor[3] = new ItemStack(Material.DIAMOND_HELMET);
		armor[2] = new ItemStack(Material.LEATHER_CHESTPLATE);
		armor[1] = new ItemStack(Material.LEATHER_LEGGINGS);
		armor[0] = new ItemStack(Material.DIAMOND_BOOTS);

		sender.getInventory().setItem(0, new ItemStack(Material.DIAMOND_SWORD));
		sender.getInventory().setItem(8, new ItemBuilder(Material.LEGACY_INK_SACK).setAmount(1).setData(CompMaterial.RED_DYE.getData()).setDisplayName("&c&lLeave PvP").create());
		sender.getInventory().setArmorContents(armor);
		sender.getInventory().setHeldItemSlot(0);
		sender.updateInventory();
		sender.teleport(lHub.getInstance().getSettingsHandler().getSpawnLocation());

		for (Player online : Bukkit.getOnlinePlayers()) {
			if (!online.hasMetadata("pvpmode")) continue;

			online.showPlayer(sender);
			sender.showPlayer(online);
		}
	}

}
