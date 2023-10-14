package dev.lbuddyboy.pcore.essential.enderchest;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

@RequiredArgsConstructor
public class EnderChest {

    private final EnderChestData cache;
    private Inventory inventory;

    public void createInventory() {
        OfflinePlayer player = Bukkit.getOfflinePlayer(this.cache.getOwner());
        this.inventory = Bukkit.createInventory(null, InventoryType.CHEST, player.getName() + "'s EnderChest");
        this.inventory.setContents(this.cache.getContents());
    }

}
