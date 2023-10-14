package dev.lbuddyboy.gkits.command;

import dev.lbuddyboy.gkits.menu.ArmorSetsMenu;
import dev.lbuddyboy.gkits.util.CC;
import dev.lbuddyboy.gkits.util.ItemBuilder;
import dev.lbuddyboy.gkits.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 13/07/2021 / 9:49 PM
 * GKits / me.lbuddyboy.gkits.command.menu
 */
public class ArmorSetsCommand implements CommandExecutor {

	public static ItemStack randomArmorSet = new ItemBuilder(Material.MAGMA_CREAM)
			.setName("&c&lRandom Armor Set")
			.setLore(Arrays.asList(
					" ",
					" &7Receive a random of the following",
					" &7armor sets.",
					" &7" + CC.ARROWS_RIGHT + " &e&lDistanced Archer Set",
					" &7" + CC.ARROWS_RIGHT + " &c&lSamurai Armor Set",
					" &7" + CC.ARROWS_RIGHT + " &a&lShogun Armor Set",
					" "
			))
			.create();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length > 0) {
			if (!sender.hasPermission("gkits.admin")) return false;
			if (args[0].equalsIgnoreCase("give")) {
				Player target = Bukkit.getPlayer(args[1]);
				int amount = Integer.parseInt(args[2]);

				for (int i = 0; i < amount; i++) {
					ItemUtils.tryFit(target, randomArmorSet);
				}
				return false;
			}
		}
		if (!(sender instanceof Player)) {
			sender.sendMessage("Bad console");
			return false;
		}
		new ArmorSetsMenu().openMenu((Player) sender);
		return false;
	}
}
