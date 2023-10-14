package dev.lbuddyboy.gkits.enchants;

import de.tr7zw.nbtapi.NBTItem;
import dev.lbuddyboy.gkits.armorsets.ArmorSet;
import dev.lbuddyboy.gkits.enchants.object.CustomEnchant;
import dev.lbuddyboy.gkits.enchants.types.*;
import dev.lbuddyboy.gkits.lGKits;
import dev.lbuddyboy.gkits.util.CC;
import dev.lbuddyboy.gkits.util.ItemUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 09/07/2021 / 8:06 PM
 * GKits / me.lbuddyboy.gkits.enchants
 */
public class CustomEnchantManager {

	@Getter private final List<CustomEnchant> enchants;

	public CustomEnchantManager() {
		enchants = new ArrayList<>();

		enchants.add(new Aquatic());
		enchants.add(new AutoSmelt());
		enchants.add(new Blast());
//		enchants.add(new Inferno());
		enchants.add(new DepthStrider());
//		enchants.add(new Invis());
		enchants.add(new Recover());
		enchants.add(new Repair());
		enchants.add(new Replenish());
		enchants.add(new Speed());
	}

	public void updateCE(Player player, ItemStack old, ItemStack newItem) {
		Map<CustomEnchant, Integer> oldEnchants = lGKits.getInstance().getCustomEnchantManager().getCustomEnchants(old);
		for (CustomEnchant enchant : oldEnchants.keySet()) {
			Bukkit.getScheduler().runTask(lGKits.getInstance(), () -> enchant.deactivate(player, old, oldEnchants.get(enchant)));
		}

		Map<CustomEnchant, Integer> newEnchants = lGKits.getInstance().getCustomEnchantManager().getCustomEnchants(newItem);
		for (CustomEnchant enchant : newEnchants.keySet()) {
			Bukkit.getScheduler().runTask(lGKits.getInstance(), () -> enchant.activate(player, newItem, newEnchants.get(enchant)));
		}
		Bukkit.getScheduler().runTaskAsynchronously(lGKits.getInstance(), () -> {
			for (ItemStack stack : player.getInventory().getArmorContents()) {
				if (stack == null || stack.getType() == Material.AIR) continue;
				if (stack.isSimilar(newItem)) continue;

				Map<CustomEnchant, Integer> enchants = lGKits.getInstance().getCustomEnchantManager().getCustomEnchants(stack);
				for (CustomEnchant enchant : enchants.keySet()) {
					Bukkit.getScheduler().runTask(lGKits.getInstance(), () -> enchant.activate(player, stack, enchants.get(enchant)));
				}
			}
		});
	}

	public List<CustomEnchant> listByArmorType(String armorType) {
		List<CustomEnchant> customEnchants = new ArrayList<>();
		for (CustomEnchant enchant : enchants) {
			for (String allowedType : enchant.allowedTypes()) {
				if (armorType.contains(allowedType)) {
					customEnchants.add(enchant);
				}
			}
		}
		return customEnchants;
	}

	public CustomEnchant byArmorType(String armorType) {
		for (CustomEnchant enchant : enchants) {
			for (String allowedType : enchant.allowedTypes()) {
				if (armorType.contains(allowedType)) {
					return enchant;
				}
			}
		}
		return null;
	}
	public CustomEnchant byName(String customEnchant) {
		for (CustomEnchant enchant : enchants) {
			if (enchant.displayName().equals(CC.translate(customEnchant)) || enchant.name().equals(customEnchant)) {
				return enchant;
			}
		}
		return null;
	}

	public List<CustomEnchant> getCustomEnchants(Player player) {
		List<CustomEnchant> enchants = new ArrayList<>();
		for (CustomEnchant enchant : this.enchants) {
			if (enchant.hasEnchant(player)) enchants.add(enchant);
		}
		return enchants;
	}

	public Map<CustomEnchant, Integer> getCustomEnchants(ItemStack stack) {
		if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) return new HashMap<>();

		NBTItem item = new NBTItem(stack);
		Map<CustomEnchant, Integer> enchants = new HashMap<>();

		for (String key : item.getKeys()) {
			if (!key.startsWith("lgkits-")) continue;

			enchants.put(byName(key.replaceAll("lgkits-", "")), item.getInteger(key));
		}

		return enchants;
	}

}
