package dev.lbuddyboy.jailcorder.util.menu;

import dev.lbuddyboy.jailcorder.util.menu.Button;
import dev.lbuddyboy.jailcorder.util.menu.Menu;
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

        if (!dev.lbuddyboy.jailcorder.util.menu.Menu.openedMenus.containsKey(player.getName())) return;

        dev.lbuddyboy.jailcorder.util.menu.Menu menu = dev.lbuddyboy.jailcorder.util.menu.Menu.openedMenus.get(player.getName());

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
        if (!dev.lbuddyboy.jailcorder.util.menu.Menu.openedMenus.containsKey(player.getName())) return;

        Menu.openedMenus.remove(player.getName()).close(player);
    }

}
