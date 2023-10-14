package dev.lbuddyboy.hub.util.menu;

import dev.lbuddyboy.hub.lHub;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class ButtonListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) throws Exception {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        if (!Menu.openedMenus.containsKey(player.getName())) return;

        Menu menu = Menu.openedMenus.get(player.getName());
        if (player.hasMetadata("button_cooldown")) {
            MetadataValue value = player.getMetadata("button_cooldown").get(0);
            long cooldown = value.asLong();
            if (cooldown - System.currentTimeMillis() > 0) {
                event.setCancelled(true);
                return;
            }
        }

        for (Button button : menu.getButtons(player)) {
            if (button.getSlot() - 1 == event.getSlot()) {
                player.setMetadata("button_cooldown", new FixedMetadataValue(lHub.getInstance(), System.currentTimeMillis() + 100));
                button.action(event);

                if (button.clickUpdate()) menu.update(player);
                if (button.cancels()) event.setCancelled(true);
                return;
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        if (!Menu.openedMenus.containsKey(event.getPlayer().getName())) return;
        Player player = (Player) event.getPlayer();

        Menu.openedMenus.get(event.getPlayer().getName()).close(player);
    }

}
