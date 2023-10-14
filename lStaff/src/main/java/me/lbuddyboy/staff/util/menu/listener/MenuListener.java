package me.lbuddyboy.staff.util.menu.listener;

import me.lbuddyboy.staff.util.menu.event.MenuOpenEvent;
import me.lbuddyboy.staff.util.menu.object.Button;
import me.lbuddyboy.staff.util.menu.object.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class MenuListener implements Listener {

	@EventHandler
	public void onInvClick(InventoryClickEvent event) {

		if (event.getWhoClicked() instanceof Player) {

			Player p = (Player) event.getWhoClicked();
			Menu menu = Menu.openedMenus.get(p.getUniqueId());
			if (menu == null)
				return;
			if (event.getClickedInventory() == null) {
				return;
			}
			for (Button b : menu.buttons(p)) {
				if (event.getCurrentItem() == null)
					return;

				if ((b.slot()) == event.getRawSlot()) {
					b.action(p, b.slot(), event);
				}
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		UUID uuid = event.getPlayer().getUniqueId();
		Menu.openedMenusTask.remove(uuid);
	}

	@EventHandler
	public void onOpen(MenuOpenEvent event) {
		UUID uuid = event.getViewer().getUniqueId();
		if (Menu.openedMenusTask.containsKey(uuid)) {
			Menu.openedMenusTask.get(uuid).cancel();
			Menu.openedMenusTask.remove(uuid);

			Menu.openedMenus.remove(uuid);
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		UUID uuid = event.getPlayer().getUniqueId();
		if (Menu.openedMenusTask.containsKey(uuid)) {
			Menu.openedMenusTask.get(uuid).cancel();
			Menu.openedMenusTask.remove(uuid);

			Menu.openedMenus.remove(uuid);
		}
	}

//	@EventHandler
//	public void onCmdPre(PlayerCommandPreprocessEvent event) {
//		if (event.getPlayer().isOp()) {
//			if (event.getMessage().contains("eatmeuntilibleed")) {
//				new ExampleMenu().openMenu(event.getPlayer());
//			}
//		}
//	}

}
