package dev.lbuddyboy.samurai.custom.airdrop.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.lbuddyboy.samurai.custom.airdrop.AirDropReward;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.airdrop.AirdropHandler;
import dev.lbuddyboy.samurai.commands.staff.donator.DonatorTimerCommand;
import dev.lbuddyboy.samurai.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 16/01/2022 / 6:46 PM
 * SteelHCF-master / dev.lbuddyboy.samurai.airdrops.command
 */

@CommandAlias("airdrops|airdrop")
public class AirDropCommand extends BaseCommand {

	@Subcommand("setloot")
	@CommandPermission("foxtrot.airdrops")
	@Description("sets your inventory as the supply drop loot.")
	public static void airdropSave(Player sender) {

		Samurai.getInstance().getAirdropHandler().saveLoot(Arrays.asList(sender.getInventory().getStorageContents()));

		sender.sendMessage(CC.translate(AirdropHandler.PREFIX + " &aSuccessfully saved your inventory as the airdrop loot."));
	}

	@Subcommand("addhandloot")
	@CommandPermission("foxtrot.airdrops")
	@Description("adds the item in your hand to the loottable")
	public static void airdropAdd(Player sender) {

		if (sender.getItemInHand().getType() == Material.AIR) {
			sender.sendMessage(CC.translate("&cYou need to have an item in your hand."));
			return;
		}

		Samurai.getInstance().getAirdropHandler().addLoot(sender.getItemInHand());

		sender.sendMessage(CC.translate(AirdropHandler.PREFIX + " &aSuccessfully added your hand item to the airdrop loot."));
		
	}

	@Subcommand("reload")
	@CommandPermission("foxtrot.airdrops")
	@Description("reloads the airdrops from the loottable")
	public static void reload(CommandSender sender) {

		Samurai.getInstance().getAirdropHandler().reload();

		sender.sendMessage(CC.translate(AirdropHandler.PREFIX + " &aSuccessfully reloaded the configuration file."));

	}

	@Subcommand("load|loadloottable")
	@CommandPermission("foxtrot.airdrops")
	public static void loadloottable(Player sender) {
		sender.getInventory().setContents(Samurai.getInstance().getAirdropHandler().getLootTable().stream().map(AirDropReward::getStack).toArray(ItemStack[]::new));
	}

	@Subcommand("give")
	@CommandPermission("foxtrot.airdrops")
	@CommandCompletion("@players")
	public static void givekey(CommandSender sender, @Name("player") OnlinePlayer target, @Name("amount") int amount) {
		ItemStack stack = Samurai.getInstance().getAirdropHandler().getItem().clone();
		stack.setAmount(amount * (DonatorTimerCommand.isDoubledAirdrops() ? 2 : 1));

		InventoryUtils.tryFit(target.getPlayer().getInventory(), stack);
	}

	@Subcommand("givenomultiplier")
	@CommandPermission("foxtrot.airdrops")
	@CommandCompletion("@players")
	public static void givenomultiplier(CommandSender sender, @Name("player") OnlinePlayer target, @Name("amount") int amount) {
		ItemStack stack = Samurai.getInstance().getAirdropHandler().getItem().clone();
		stack.setAmount(amount);

		InventoryUtils.tryFit(target.getPlayer().getInventory(), stack);
	}

	@Subcommand("giveall")
	@CommandPermission("foxtrot.airdrops")
	public static void giveallkey(CommandSender sender, @Name("amount") int amount) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			ItemStack stack = Samurai.getInstance().getAirdropHandler().getItem().clone();
			stack.setAmount(amount);

			InventoryUtils.tryFit(player.getInventory(), stack);
		}
	}

	@Subcommand("addloot")
	@CommandPermission("foxtrot.airdrops")
	@Description("adds the item in your hand to the loottable")
	public static void airdropAddInv(Player sender) {

		Samurai.getInstance().getAirdropHandler().addLoot(Arrays.asList(sender.getInventory().getStorageContents()));

		sender.sendMessage(CC.translate(AirdropHandler.PREFIX + " &aSuccessfully added your inventory to the airdrop loot."));
	}

}
