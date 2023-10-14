package dev.lbuddyboy.bunkers.util.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class ButtonListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        if (!Menu.openedMenus.containsKey(player.getName())) return;

        Menu menu = Menu.openedMenus.get(player.getName());

        for (Button button : menu.getButtons(player)) {
            if (button.getSlot() - 1 == event.getSlot()) {
                button.action(event);
                event.setCancelled(button.cancels());

                if (button.clickUpdate()) menu.update(player);
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        if (!Menu.openedMenus.containsKey(player.getName())) return;

        Menu.openedMenus.remove(player.getName()).close(player);
    }

}
