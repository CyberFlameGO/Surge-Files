package dev.lbuddyboy.samurai.custom.vaults.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.lbuddyboy.samurai.custom.supplydrops.SupplyDropHandler;
import dev.lbuddyboy.samurai.custom.vaults.listener.VaultListener;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.events.Event;
import dev.lbuddyboy.samurai.events.koth.KOTH;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.claims.Claim;
import dev.lbuddyboy.samurai.util.InventoryUtils;
import dev.lbuddyboy.samurai.util.object.CuboidRegion;
import dev.lbuddyboy.samurai.custom.vaults.VaultHandler;
import dev.lbuddyboy.samurai.custom.vaults.VaultStage;
import dev.lbuddyboy.samurai.custom.vaults.menu.VaultMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 16/01/2022 / 6:46 PM
 * SteelHCF-master / dev.lbuddyboy.samurai.custom.vaults.command
 */

@CommandAlias("vaultevent")
public class VaultCommand extends BaseCommand {

	@Subcommand("start")
	@CommandPermission("foxtrot.vaultevent")
	public static void startVault(CommandSender sender) {
		for (Event otherKoth : Samurai.getInstance().getEventHandler().getEvents()) {
			if (otherKoth.isActive()) {
				sender.sendMessage(ChatColor.RED + otherKoth.getName() + " is currently active.");
				return;
			}
		}
		Samurai.getInstance().getVaultHandler().getCap().activate();
		sender.sendMessage(CC.translate("&aVault started!"));
	}

	@Subcommand("reload")
	@CommandPermission("foxtrot.vaultevent")
	public static void reload(CommandSender sender) {
		Samurai.getInstance().getVaultHandler().reload();
	}

	@Subcommand("forceact")
	@CommandPermission("foxtrot.vaultevent")
	public static void forceAct(CommandSender sender) {

		Location location = ((KOTH) Samurai.getInstance().getVaultHandler().getCap()).getCapLocation().toLocation(Bukkit.getWorld("world"));
		VaultListener.paste(VaultStage.LOOT_3.getSchematicName(), location);

		Team vaultTeam = Samurai.getInstance().getTeamHandler().getTeam(VaultHandler.TEAM_NAME);
		for (Claim claim : vaultTeam.getClaims()) {
			for (Location loc : new CuboidRegion(VaultHandler.TEAM_NAME, claim.getMinimumPoint(), claim.getMaximumPoint())) {
				if (loc.getBlock().getType() == Material.LIME_SHULKER_BOX) {
					loc.getBlock().setType(Material.CHEST);
				}
				if (loc.getBlock().getType() == Material.CHEST) {
					Chest chest = (Chest) loc.getBlock().getState();
					for (int i = 0; i < 4; i++) {
						ItemStack chosen = Samurai.getInstance().getVaultHandler().getLoottable().get(ThreadLocalRandom.current().nextInt(0, Samurai.getInstance().getVaultHandler().getLoottable().size()));
						chest.getBlockInventory().addItem(chosen);
					}
				}
			}
		}

		Bukkit.getScheduler().runTaskLater(Samurai.getInstance(), () -> {
			Samurai.getInstance().getVaultHandler().setCapping(null);
			Samurai.getInstance().getVaultHandler().setVaultStage(VaultStage.CLOSED);
			VaultListener.paste(VaultStage.CLOSED.getSchematicName(), location);
		}, 20 * 60 * 2);
	}

	@Subcommand("stop")
	@CommandPermission("foxtrot.vaultevent")
	public static void stopVault(CommandSender sender) {
		for (Event otherKoth : Samurai.getInstance().getEventHandler().getEvents()) {
			if (otherKoth.getName().equals(VaultHandler.TEAM_NAME)) {
				if (!otherKoth.isActive()) {
					sender.sendMessage(ChatColor.RED + VaultHandler.TEAM_NAME + " is not active.");
					return;
				}
			}
		}
		VaultListener.paste("vaultclosed", Samurai.getInstance().getVaultHandler().getCap().getCapLocation().toLocation(Bukkit.getWorld("world")));
		Samurai.getInstance().getVaultHandler().getCap().deactivate();
		sender.sendMessage(CC.translate("&cVault ended!"));
	}

	@Subcommand("setloot")
	@CommandPermission("foxtrot.vaultevent")
	public static void vaultSave(Player sender) {

		Samurai.getInstance().getVaultHandler().saveLoot(Arrays.asList(sender.getInventory().getStorageContents()));

		sender.sendMessage(CC.translate(SupplyDropHandler.PREFIX + " &aSuccessfully saved your inventory as the vault loot."));
	}

	@Subcommand("addhandloot")
	@CommandPermission("foxtrot.vaultevent")
	public static void vaultAdd(Player sender) {

		if (sender.getItemInHand().getType() == Material.AIR) {
			sender.sendMessage(CC.translate("&cYou need to have an item in your hand."));
			return;
		}

		Samurai.getInstance().getVaultHandler().addLoot(sender.getItemInHand());

		sender.sendMessage(CC.translate(SupplyDropHandler.PREFIX + " &aSuccessfully added your hand item to the vault loot."));
	}

	@Subcommand("loot|rewards")
	public static void loottable(Player sender) {
		new VaultMenu().openMenu(sender);
	}

	@Subcommand("givekey")
	@CommandCompletion("@players")
	@CommandPermission("foxtrot.vaultevent")
	public static void givekey(CommandSender sender, @Name("player") OnlinePlayer target, @Name("amount") int amount) {
		InventoryUtils.addAmountToInventory(target.getPlayer().getInventory(), Samurai.getInstance().getVaultHandler().getKey(), amount);
	}

	@Subcommand("addinvloot")
	@CommandPermission("foxtrot.vaultevent")
	public static void vaultAddInv(Player sender) {

		Samurai.getInstance().getVaultHandler().addLoot(Arrays.asList(sender.getInventory().getStorageContents()));

		sender.sendMessage(CC.translate(SupplyDropHandler.PREFIX + " &aSuccessfully added your inventory to the vault loot."));
	}

}
