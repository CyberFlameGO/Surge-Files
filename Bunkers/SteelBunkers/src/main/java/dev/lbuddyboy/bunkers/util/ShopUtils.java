package dev.lbuddyboy.bunkers.util;

import dev.lbuddyboy.bunkers.Bunkers;
import dev.lbuddyboy.bunkers.game.shop.Shop;
import dev.lbuddyboy.bunkers.game.user.GameUser;
import dev.lbuddyboy.bunkers.util.bukkit.CC;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 26/03/2022 / 9:53 AM
 * SteelBunkers / com.steelpvp.bunkers.util
 */
public class ShopUtils {

	public static void loadDefaults() {
		Shop.setSELL_SLOTS(new int[]{1, 3, 5, 7});
		Shop.setBUY_SLOTS(new int[]{0,1,2,3,4,5,6,7,48,50});
		Shop.setENCHANTMENT_SLOTS(new int[]{10,11,12,14,16});
		Shop.setCOMBAT_SLOTS(new int[]{10, 14, 15, 16, 18, 19, 20, 21, 23, 24, 25, 28, 34, 37, 39, 41});
	}

	public static double detect(Player player, Material material, double pricePer) {
		int count = 0;
		for (ItemStack item : player.getInventory().getContents()) {
			if (item == null || item.getType() == Material.AIR) continue;
			if (item.getType() == material) count += item.getAmount();
		}
		double total = count * pricePer;
		player.getInventory().remove(material);

		return total;
	}

	public static void buy(Player player, ItemStack stack, double price) {
		GameUser user = Bunkers.getInstance().getGameHandler().getGameUser(player.getUniqueId());

		if (!user.hasBalance(price)) {
			player.sendMessage(CC.translate("&cInsufficient funds..."));
			return;
		}

		user.takeBalance(price);
		InventoryUtils.addAmountToInventory(player.getInventory(), stack);

	}

	public static void buyInvFull(Player player, ItemStack stack, double price) {
		GameUser user = Bunkers.getInstance().getGameHandler().getGameUser(player.getUniqueId());

		int multiplier = (stack.getType() == Material.ENDER_PEARL ? 16 : InventoryUtils.getOpenSlots(player.getInventory()));
		if (!user.hasBalance(price * multiplier)) {
			player.sendMessage(CC.translate("&cInsufficient funds..."));
			return;
		}

		user.takeBalance(price * multiplier);
		for (int i = 0; i < multiplier; i++) {
			InventoryUtils.addAmountToInventory(player.getInventory(), stack);
		}

	}

	public static void enchant(Player player, ItemStack stack, Enchantment enchantment, int level, double price) {
		GameUser user = Bunkers.getInstance().getGameHandler().getGameUser(player.getUniqueId());

		if (!user.hasBalance(price)) {
			player.sendMessage(CC.translate("&cInsufficient funds..."));
			return;
		}

		if (!enchantment.canEnchantItem(stack)) {
			player.sendMessage(CC.translate("&cCannot enchant this item with that enchantment..."));
			return;
		}

		user.takeBalance(price);
		stack.addUnsafeEnchantment(enchantment, level);

	}

	public static void sell(Player player, ClickType clickType, Material material, double price) {
		GameUser user = Bunkers.getInstance().getGameHandler().getGameUser(player.getUniqueId());

		if (!player.getInventory().contains(material)) {
			player.sendMessage(CC.translate("&cYou don't have any of that item to sell!"));
			return;
		}

		if (clickType == ClickType.RIGHT) {
			double total = detect(player, material, price);
			user.addBalance(total);
			player.sendMessage(CC.translate("&aSuccessfully sold all of your " + material.name().toLowerCase() + "! Earned: $" + total));
			return;
		}

		user.addBalance(price);
		InventoryUtils.removeAmountFromInventory(player.getInventory(), new ItemStack(material), 1);
		player.sendMessage(CC.translate("&aSuccessfully sold a " + material.name().toLowerCase() + "! Earned: $" + price));
	}


}
