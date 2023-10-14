package me.lbuddyboy.staff.util.menu.object;

import me.lbuddyboy.staff.lStaff;
import me.lbuddyboy.staff.util.CC;
import me.lbuddyboy.staff.util.ItemBuilder;
import me.lbuddyboy.staff.util.menu.event.MenuOpenEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class Menu {

	public static Map<UUID, BukkitTask> openedMenusTask = new HashMap<>();
	public static Map<UUID, Menu> openedMenus = new HashMap<>();

	private String title;
	private boolean autofill = false;
	private boolean autoupdate = false;

	public Menu() {

	}

	public Menu(String title) {
		this.title = title;
	}

	public abstract List<Button> buttons(Player player);

	public abstract int size(Player player);

	public void openMenu(Player player) {
		Inventory i = create(player);
		for (Button b : buttons(player)) {
			i.setItem(b.slot(), b.stack(player));
		}
		MenuOpenEvent event = new MenuOpenEvent(player);
		Bukkit.getPluginManager().callEvent(event);

		player.openInventory(i);
		if (isAutoUpdate(player)) {
			openedMenusTask.put(player.getUniqueId(), task(player));
		}
		openedMenus.put(player.getUniqueId(), this);
	}

	public Inventory create(Player player) {
		Inventory inv = Bukkit.createInventory(null, size(player), getTitle(player));
		for (Button b : buttons(player)) {
			inv.setItem(b.slot(), b.stack(player));
		}
		if (isAutoFill(player)) {
			for (int i = 0; i < size(player); ++i) {
				if (inv.getItem(i) == null || inv.getItem(i).getType() == Material.AIR) {
					inv.setItem(i, placeholderButton(i).stack(player));
				}
			}
		}
		return inv;
	}

	public BukkitTask task(Player player) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				if (!openedMenusTask.containsKey(player.getUniqueId())) {
					cancel();
					return;
				}
				if (!player.isOnline()) {
					cancel();
					return;
				}
				if (player.getOpenInventory().getTitle().equalsIgnoreCase(CC.chat(getTitle(player)))) {
					if (player.getOpenInventory().getTopInventory().getContents() != create(player).getContents()) {
						player.getOpenInventory().getTopInventory().setContents(create(player).getContents());
					}
				}
			}
		}.runTaskTimer(lStaff.getInstance(), 20, 20);
	}

	public Button placeholderButton(int slot) {
		return new Button() {
			@Override
			public ItemStack stack(Player player) {
				return new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("").create();
			}

			@Override
			public void action(Player player, int slot, InventoryClickEvent event) {
				event.setCancelled(true);
			}

			@Override
			public int slot() {
				return slot;
			}
		};
	}

	public String getTitle(Player player) {
		return this.title;
	}

	public boolean isAutoUpdate(Player player) {
		return this.autoupdate;
	}

	public boolean isAutoFill(Player player) {
		return this.autofill;
	}

	public int calculatedSize(List<Button> buttons) {
		int highest = 0;

		for (Button button : buttons) {
			int buttonValue = button.slot();
			if (buttonValue > highest) {
				highest = buttonValue;
			}
		}

		return (int)(Math.ceil((double)(highest + 1) / 9.0D) * 9.0D);
	}

}
