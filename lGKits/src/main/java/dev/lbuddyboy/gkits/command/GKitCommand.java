package dev.lbuddyboy.gkits.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.lbuddyboy.gkits.lGKits;
import dev.lbuddyboy.gkits.menu.GKitMenu;
import dev.lbuddyboy.gkits.object.kit.GKit;
import dev.lbuddyboy.gkits.util.CC;
import dev.lbuddyboy.gkits.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 09/07/2021 / 5:08 PM
 * GKits / me.lbuddyboy.gkits.command
 */

@CommandAlias("gkit|kit|kits|gkits|gkitz|kitz")
public class GKitCommand extends BaseCommand {

	@Subcommand("create")
	@CommandPermission("gkitz.admin")
	public static void gkit(Player sender, @Name("name") String name) {
		if (lGKits.getInstance().getGKits().containsKey(name)) {
			sender.sendMessage(CC.translate("&cThat kit already exists."));
			return;
		}

		GKit kit = new GKit(name);

		kit.setArmor(sender.getInventory().getArmorContents());
		kit.setItems(sender.getInventory().getStorageContents());
		kit.setFakeItems(new ItemStack[0]);
		kit.setDisplayName(name);
		kit.setAutoequip(true);
		kit.setDisplayItem(new ItemBuilder(Material.GOLD_INGOT, 1).setName(CC.GOLD + name + " Kit").create());
		kit.setCommands(new ArrayList<>());
		kit.setFormattedCooldown("24h");
		kit.setSlot(lGKits.getInstance().getGKits().size() + 1);

		lGKits.getInstance().getGKits().put(name, kit);
		kit.save();
		sender.sendMessage(CC.translate("&aYou have created the '" + name + "' kit!"));
	}

	@Subcommand("delete")
	@CommandPermission("gkitz.admin")
	@CommandCompletion("@kits")
	public static void delete(Player sender, @Name("name") GKit kit) {
		kit.delete();
		sender.sendMessage(CC.translate("&a" + kit.getName() + " DELETED!"));
	}

	@Subcommand("reload")
	@CommandPermission("gkitz.admin")
	@CommandCompletion("@kits")
	public static void reload(Player sender, @Name("name") @Optional GKit kit) {
		if (kit == null) {
			lGKits.getInstance().getGKits().values().forEach(GKit::reload);
			lGKits.getInstance().getMessageConfig().save();
			try {
				lGKits.getInstance().getEnchantsYML().reloadConfig();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			lGKits.getInstance().reloadConfig();
			sender.sendMessage(CC.translate("&aReloaded all gkit modules!"));
			return;
		}
		kit.reload();
		sender.sendMessage(CC.translate("&a" + kit.getName() + " new config loaded in game!"));
	}

	@Subcommand("setarmor")
	@CommandPermission("gkitz.admin")
	@CommandCompletion("@kits")
	public static void setArmor(Player sender, @Name("name") GKit kit) {
		kit.setArmor(sender.getInventory().getArmorContents());
		kit.save();
		sender.sendMessage(CC.translate("&a" + kit.getName() + " armor updated!"));
	}

	@Subcommand("setitems")
	@CommandPermission("gkitz.admin")
	@CommandCompletion("@kits")
	public static void setItems(Player sender, @Name("name") GKit kit) {
		kit.setItems(sender.getInventory().getStorageContents());
		kit.save();
		sender.sendMessage(CC.translate("&a" + kit.getName() + " contents updated!"));
	}

	@Subcommand("setfakeitems")
	@CommandPermission("gkitz.admin")
	@CommandCompletion("@kits")
	public static void fakeItems(Player sender, @Name("name") GKit kit) {
		kit.setFakeItems(sender.getInventory().getStorageContents());
		kit.save();
		sender.sendMessage(CC.translate("&a" + kit.getName() + " fake items updated!"));
	}

	@Subcommand("loadfakeitems")
	@CommandPermission("gkitz.admin")
	@CommandCompletion("@kits")
	public static void getFakeItems(Player sender, @Name("name") GKit kit) {
		sender.getInventory().setContents(kit.getFakeItems());
		sender.sendMessage(CC.translate("&a" + kit.getName() + " fake items loaded!"));
	}

	@Subcommand("givegkit")
	@CommandPermission("gkitz.admin")
	@CommandCompletion("@kits @players")
	public static void gkit(CommandSender sender, @Name("name") GKit gKit, @Name("player") @Optional OnlinePlayer player) {
		if (player == null && sender instanceof Player) player = new OnlinePlayer((Player) sender);

		gKit.giveGkit(player.getPlayer(), true);
	}

	@Default
	public static void gkit(Player sender, @Name("name") @Optional GKit gKit) {
		if (gKit == null) {
			new GKitMenu().openMenu(sender);
			return;
		}
		if (!sender.hasPermission("gkitz." + gKit.getName())) {
			sender.sendMessage(CC.translate("&cNo permission."));
			return;
		}

		gKit.giveGkit(sender, sender.hasPermission("gkitz.bypass"));
	}

}
