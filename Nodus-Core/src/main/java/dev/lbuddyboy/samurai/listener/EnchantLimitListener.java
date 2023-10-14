package dev.lbuddyboy.samurai.listener;

import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.Iterator;
import java.util.Map;

public class EnchantLimitListener implements Listener {

	public static int getLimit(Enchantment en) {
		return Samurai.getInstance().getConfig().contains("ENCHANTMENT_LIMIT." + en.getName()) ? Samurai.getInstance().getConfig().getInt("ENCHANTMENT_LIMIT." + en.getName()) : en.getMaxLevel();
	}

	@EventHandler
	public void onDeath(EntityDeathEvent e) {
		if (e.getEntity() instanceof Monster) {
			for (ItemStack stack : e.getDrops()) {
				if (stack.getEnchantments().size() > 0) {
					for (Map.Entry<Enchantment, Integer> en : stack.getEnchantments().entrySet()) {
						int limit = getLimit(en.getKey());
						if (getLimit(en.getKey()) < en.getValue()) {
							stack.removeEnchantment(en.getKey());
							if (limit < 1) {
								continue;
							}
							stack.addEnchantment(en.getKey(), limit);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onEnchant(EnchantItemEvent e) {
		Iterator<Map.Entry<Enchantment, Integer>> eIter = e.getEnchantsToAdd().entrySet().iterator();
		while (eIter.hasNext()) {
			Map.Entry<Enchantment, Integer> entry = eIter.next();
			int limit = getLimit(entry.getKey());
			if (entry.getValue() > limit) {
				if (limit > 0) {
					e.getEnchantsToAdd().put(entry.getKey(), limit);
				} else {
					eIter.remove();
				}
			}
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.getInventory() instanceof AnvilInventory) {
			if (e.getSlotType() == InventoryType.SlotType.RESULT) {
				ItemStack i = e.getCurrentItem();
				if (i.getType().equals(Material.ENCHANTED_BOOK)) {
					EnchantmentStorageMeta m = (EnchantmentStorageMeta) i.getItemMeta();
					for (Map.Entry<Enchantment, Integer> entry : m.getStoredEnchants().entrySet()) {
						int limit = getLimit(entry.getKey());
						if (entry.getValue() > limit) {
							if (limit == 0) {
								e.setCancelled(true);
								break;
							}
							m.removeStoredEnchant(entry.getKey());
							m.addStoredEnchant(entry.getKey(), limit, false);
							i.setItemMeta(m);
						}
					}
				}
				if (i.hasItemMeta() && i.getItemMeta().hasLore() && i.getItemMeta().getLore().contains("Not repairable")) {
					e.setCancelled(true);
					return;
				}
				for (Map.Entry<Enchantment, Integer> entry : i.getEnchantments().entrySet()) {
					int limit = getLimit(entry.getKey());
					if (entry.getValue() > limit) {
						i.removeEnchantment(entry.getKey());
						if (limit > 0) {
							i.addEnchantment(entry.getKey(), limit);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (e.getAction() == Action.LEFT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.ENCHANTING_TABLE && e.getAction() == Action.LEFT_CLICK_BLOCK && e.getItem() != null && e.getItem().getType() == Material.ENCHANTED_BOOK) {
			e.setCancelled(true);
			e.getPlayer().setItemInHand(new ItemStack(Material.BOOK, e.getItem().getAmount()));
		}
	}
}