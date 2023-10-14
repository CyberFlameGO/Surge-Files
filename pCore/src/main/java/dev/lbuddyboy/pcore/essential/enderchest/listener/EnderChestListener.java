package dev.lbuddyboy.pcore.essential.enderchest.listener;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.user.MineUser;
import dev.lbuddyboy.pcore.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

public class EnderChestListener implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null) {
            if (event.getClickedBlock().getType() == Material.ENDER_CHEST) {
                Player player = event.getPlayer();
                event.setCancelled(true);
                pCore.getInstance().getEnderchestHandler().openEnderChest(player, player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inventory = event.getView().getTopInventory();
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (inventory == null) return;
        if (inventory.getTitle() == null) return;

        if (inventory.getTitle().contains("'s EnderChest")) {
            String name = inventory.getTitle().replaceAll("'s EnderChest", "");
            OfflinePlayer target = Bukkit.getOfflinePlayer(name);
            MineUser player = pCore.getInstance().getMineUserHandler().tryMineUserAsync(target.getUniqueId());

            player.setEnderChest(inventory.getContents());
            player.flagUpdate();
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        Inventory inventory = event.getView().getTopInventory();
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (inventory == null) return;
        if (inventory.getTitle() == null) return;

        if (inventory.getTitle().contains("'s EnderChest")) {
            String name = inventory.getTitle().replaceAll("'s EnderChest", "");
            OfflinePlayer target = Bukkit.getOfflinePlayer(name);
            MineUser player = pCore.getInstance().getMineUserHandler().tryMineUserAsync(target.getUniqueId());

            player.setEnderChest(inventory.getContents());
            player.flagUpdate();
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Inventory inventory = event.getView().getTopInventory();
        if (!(event.getPlayer() instanceof Player)) return;
        if (inventory == null) return;
        if (inventory.getTitle() == null) return;

        if (inventory.getTitle().contains("'s EnderChest")) {
            String name = inventory.getTitle().replaceAll("'s EnderChest", "");
            OfflinePlayer target = Bukkit.getOfflinePlayer(name);
            MineUser player = pCore.getInstance().getMineUserHandler().tryMineUserAsync(target.getUniqueId());

            player.setEnderChest(inventory.getContents());
            player.flagUpdate();
        }
    }

}
