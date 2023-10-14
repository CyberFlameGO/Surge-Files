package dev.aurapvp.samurai.essential.enderchest.listener;

import dev.aurapvp.samurai.essential.enderchest.EnderChestData;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.player.SamuraiPlayer;
import dev.aurapvp.samurai.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class EnderChestListener implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null) {
            if (event.getClickedBlock().getType() == Material.ENDER_CHEST) {
                Player player = event.getPlayer();
                event.setCancelled(true);
                Samurai.getInstance().getEnderchestHandler().openEnderChest(player, player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (inventory == null) return;
        if (inventory != event.getView().getTopInventory()) return;
        if (event.getView().getTitle() == null) return;

        if (event.getView().getTitle().contains("'s EnderChest")) {
            String name = event.getView().getTitle().replaceAll("'s EnderChest", "");
            OfflinePlayer target = Bukkit.getOfflinePlayer(name);
            SamuraiPlayer player = Samurai.getInstance().getPlayerHandler().loadPlayer(target.getUniqueId(), true);

            Tasks.run(() -> {
                player.setEnderChest(inventory.getContents());
                player.updated();

                for (Player viewer : Bukkit.getOnlinePlayers()) {

                    if (viewer.getOpenInventory() != null && viewer.getOpenInventory() != event.getView()) continue;

                    viewer.getOpenInventory().getTopInventory().setContents(inventory.getContents());
                }
            });
        }
    }

}
