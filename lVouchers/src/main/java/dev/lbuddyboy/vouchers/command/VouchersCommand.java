package dev.lbuddyboy.vouchers.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import de.tr7zw.nbtapi.NBTItem;
import dev.lbuddyboy.vouchers.lVouchers;
import dev.lbuddyboy.vouchers.object.Voucher;
import dev.lbuddyboy.vouchers.util.CC;
import dev.lbuddyboy.vouchers.util.ItemBuilder;
import dev.lbuddyboy.vouchers.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 09/07/2021 / 5:08 PM
 * GKits / me.lbuddyboy.gkits.command
 */

@CommandAlias("lvoucher|voucher")
@CommandPermission("vouchers.admin")
public class VouchersCommand extends BaseCommand {

	@Subcommand("create")
	public static void gkit(CommandSender sender, @Name("name") String name) {
		if (lVouchers.getInstance().getVoucher().containsKey(name)) {
			sender.sendMessage(CC.translate("&cThat kit already exists."));
			return;
		}

		Voucher voucher = new Voucher(name);

		voucher.setDisplayName(name);
		voucher.setDisplayItem(new ItemBuilder(Material.PAPER, 1).setName(CC.GOLD + name + " Voucher").create());
		voucher.setCommands(new ArrayList<>());

		lVouchers.getInstance().getVoucher().put(name, voucher);
		voucher.save();
		sender.sendMessage(CC.translate("&aYou have created the '" + name + "' voucher!"));
	}

	@Subcommand("delete")
	@CommandCompletion("@vouchers")
	public static void delete(CommandSender sender, @Name("name") Voucher voucher) {
		voucher.delete();
		sender.sendMessage(CC.translate("&a" + voucher.getName() + " DELETED!"));
	}

	@Subcommand("reload")
	@CommandCompletion("@vouchers")
	public static void reload(CommandSender sender, @Name("name") @Optional Voucher voucher) {
		if (voucher == null) {
			lVouchers.getInstance().getVoucher().values().forEach(Voucher::reload);
			lVouchers.getInstance().getMessageConfig().save();
			lVouchers.getInstance().reloadConfig();
			sender.sendMessage(CC.translate("&aReloaded all voucher modules!"));
			return;
		}
		voucher.reload();
		sender.sendMessage(CC.translate("&a" + voucher.getName() + " new config loaded in game!"));
	}

	@Subcommand("addcommand")
	@CommandCompletion("@vouchers")
	public static void setArmor(CommandSender sender, @Name("name") Voucher voucher, @Name("command") String command) {
		voucher.getCommands().add(command);
		voucher.save();
		sender.sendMessage(CC.translate("&a" + voucher.getName() + " voucher command added!"));
	}

	@Subcommand("give")
	@CommandCompletion("@players @vouchers")
	public static void voucher(CommandSender sender, @Name("player") @Optional OnlinePlayer player, @Name("name") Voucher voucher, @Name("amount") int amount) {
		if (player == null && sender instanceof Player) player = new OnlinePlayer((Player) sender);

		for (int i = 0; i < amount; i++) {
			NBTItem item = new NBTItem(voucher.getDisplayItem());

			item.setString("voucher", voucher.getName());

			ItemUtils.tryFit(player.getPlayer(), item.getItem());
		}
	}

}
