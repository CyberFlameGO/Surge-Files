package dev.lbuddyboy.gkits.enchants.object;

import dev.lbuddyboy.gkits.lGKits;
import dev.lbuddyboy.gkits.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 09/07/2021 / 11:04 PM
 * GKits / me.lbuddyboy.gkits.enchants.object
 */

public abstract class CustomEnchant {

	public CustomEnchant() {
		Bukkit.getConsoleSender().sendMessage(CC.translate("&fLoaded the &g" + name() + "&f custom enchantment"));
	}

	public abstract String name();
	public abstract String displayName();
	public abstract List<Rarity> rarities();
	public abstract int maxLevel();
	public abstract int minLevel();
	public abstract List<String> allowedTypes();
	public abstract String description();

	public void activate(Player player, ItemStack piece, int level) {
		player.setMetadata("CUSTOM_ENCHANT_" + name(), new FixedMetadataValue(lGKits.getInstance(), true));
	}

	public void deactivate(Player player, ItemStack piece, int level) {
		player.removeMetadata("CUSTOM_ENCHANT_" + name(), lGKits.getInstance());
	}

	public void deactivate(Player player) {
		player.removeMetadata("CUSTOM_ENCHANT_" + name(), lGKits.getInstance());
	}

	public boolean hasEnchant(Player player) {
		return player.hasMetadata("CUSTOM_ENCHANT_" + name());
	}

	public ChatColor topRarityColor() {
		for (Rarity rarity : rarities()) {
			if (rarity == Rarity.LEGENDARY) {
				return rarity.getColor();
			}
			if (rarity == Rarity.RARE) {
				return rarity.getColor();
			}
			if (rarity == Rarity.COMMON) {
				return rarity.getColor();
			}
		}
		return ChatColor.GOLD;
	}

	public Rarity topRarity() {
		for (Rarity rarity : rarities()) {
			if (rarity == Rarity.LEGENDARY) {
				return rarity;
			}
			if (rarity == Rarity.RARE) {
				return rarity;
			}
			if (rarity == Rarity.COMMON) {
				return rarity;
			}
		}
		return Rarity.COMMON;
	}

}
