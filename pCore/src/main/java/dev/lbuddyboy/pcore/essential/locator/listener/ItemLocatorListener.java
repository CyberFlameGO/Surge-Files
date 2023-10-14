package dev.lbuddyboy.pcore.essential.locator.listener;

import de.tr7zw.nbtapi.NBTItem;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.essential.locator.ItemCache;
import dev.lbuddyboy.pcore.essential.locator.ItemLocation;
import dev.lbuddyboy.pcore.essential.locator.LocationType;
import dev.lbuddyboy.pcore.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class ItemLocatorListener implements Listener {

    @EventHandler
    public void onSpawn(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        NBTItem item = new NBTItem(event.getItem().getItemStack());

        if (!item.hasTag("id")) return;

        pCore.getInstance().getLocatorHandler().updateCache(item.getUUID("id"), new ItemLocation(null, player.getUniqueId(), LocationType.PLAYER_INVENTORY));
    }

    @EventHandler
    public void onClickNormal(InventoryClickEvent event) {
        if (Menu.openedMenus.containsKey(event.getWhoClicked().getName())) return;

        if (!(event.getWhoClicked() instanceof Player)) return;
        ItemStack stack = event.getCursor();
        if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) return;

        NBTItem item = new NBTItem(stack);

        if (!item.hasTag("id")) return;

        ItemCache cache = pCore.getInstance().getLocatorHandler().fetchCache(item.getUUID("id"));

        if (cache == null) return;
        if (event.getClick() != ClickType.RIGHT && event.getClick() != ClickType.LEFT) return;
        if (event.getClickedInventory() == null) return;

        if (event.getClickedInventory().getHolder() instanceof DoubleChest) {
            DoubleChest state = (DoubleChest) event.getView().getTopInventory().getHolder();
            LocationType type = LocationType.DOUBLE_CHEST;
            ItemLocation location = new ItemLocation(state.getLocation(), null, type);

            pCore.getInstance().getLocatorHandler().updateCache(item.getUUID("id"), location);
            return;
        }

        if (event.getClickedInventory().getHolder() instanceof Player) {
            pCore.getInstance().getLocatorHandler().updateCache(item.getUUID("id"), new ItemLocation(null, event.getWhoClicked().getUniqueId(), LocationType.PLAYER_INVENTORY));
            return;
        }

        BlockState state = (BlockState) event.getClickedInventory().getHolder();
        LocationType type = LocationType.BLOCK_INVENTORY;
        ItemLocation location = new ItemLocation(state == null ? null : state.getLocation(), null, type);

        if (state == null && event.getView().getTopInventory().getType() == InventoryType.ENDER_CHEST) {
            location.setHolderUUID(event.getWhoClicked().getUniqueId());
            location.setType(LocationType.ENDER_CHEST);
        }

        if (state != null) location.setLocation(state.getLocation());

        pCore.getInstance().getLocatorHandler().updateCache(item.getUUID("id"), location);
}

    @EventHandler
    public void onClickCursor(InventoryClickEvent event) {
        if (Menu.openedMenus.containsKey(event.getWhoClicked().getName())) return;

        if (!(event.getWhoClicked() instanceof Player)) return;
        ItemStack stack = event.getCurrentItem();
        if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) return;

        NBTItem item = new NBTItem(stack);

        if (!item.hasTag("id")) return;

        ItemCache cache = pCore.getInstance().getLocatorHandler().fetchCache(item.getUUID("id"));

        if (cache == null) return;
        if (event.getClick() != ClickType.RIGHT && event.getClick() != ClickType.LEFT) return;

        pCore.getInstance().getLocatorHandler().updateCache(item.getUUID("id"), new ItemLocation(null, event.getWhoClicked().getUniqueId(), LocationType.HOVERING));

    }

    @EventHandler
    public void onShiftClick(InventoryClickEvent event) {
        if (Menu.openedMenus.containsKey(event.getWhoClicked().getName())) return;

        if (!(event.getWhoClicked() instanceof Player)) return;
        ItemStack stack = event.getCurrentItem();
        if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) return;

        NBTItem item = new NBTItem(stack);

        if (!item.hasTag("id")) return;

        ItemCache cache = pCore.getInstance().getLocatorHandler().fetchCache(item.getUUID("id"));

        if (cache == null) return;
        if (!event.getClick().isShiftClick() && event.getClick() != ClickType.NUMBER_KEY && event.getClick() != ClickType.DOUBLE_CLICK) return;
        if (event.getClickedInventory() == event.getView().getTopInventory()) return;
        if (event.getView().getTopInventory().firstEmpty() == -1) return;

        if (event.getView().getTopInventory().getHolder() instanceof DoubleChest) {
            DoubleChest state = (DoubleChest) event.getView().getTopInventory().getHolder();
            LocationType type = LocationType.DOUBLE_CHEST;
            ItemLocation location = new ItemLocation(state.getLocation(), null, type);

            pCore.getInstance().getLocatorHandler().updateCache(item.getUUID("id"), location);
            return;
        }

        BlockState state = (BlockState) event.getView().getTopInventory().getHolder();
        LocationType type = LocationType.BLOCK_INVENTORY;
        ItemLocation location = new ItemLocation(state == null ? null : state.getLocation(), null, type);

        if (state == null && event.getView().getTopInventory().getType() == InventoryType.ENDER_CHEST) {
            location.setHolderUUID(event.getWhoClicked().getUniqueId());
            location.setType(LocationType.ENDER_CHEST);
        }

        if (state != null) location.setLocation(state.getLocation());

        pCore.getInstance().getLocatorHandler().updateCache(item.getUUID("id"), location);
    }

    @EventHandler
    public void onShiftClickTop(InventoryClickEvent event) {
        if (Menu.openedMenus.containsKey(event.getWhoClicked().getName())) return;

        if (!(event.getWhoClicked() instanceof Player)) return;
        ItemStack stack = event.getCurrentItem();
        if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) return;
        Player player = (Player) event.getWhoClicked();

        NBTItem item = new NBTItem(stack);

        if (!item.hasTag("id")) return;

        ItemCache cache = pCore.getInstance().getLocatorHandler().fetchCache(item.getUUID("id"));

        if (cache == null) return;
        if (!event.getClick().isShiftClick() && !event.getClick().isKeyboardClick()) return;
        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory() == event.getView().getBottomInventory()) return;
        if (event.getView().getBottomInventory().firstEmpty() == -1) return;

        pCore.getInstance().getLocatorHandler().updateCache(item.getUUID("id"), new ItemLocation(null, player.getUniqueId(), LocationType.PLAYER_INVENTORY));
    }

    @EventHandler
    public void onNumberClick(InventoryClickEvent event) {
        if (Menu.openedMenus.containsKey(event.getWhoClicked().getName())) return;

        if (!(event.getWhoClicked() instanceof Player)) return;
        if (event.getClick() != ClickType.NUMBER_KEY) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack stack = player.getInventory().getItem(event.getHotbarButton());
        System.out.println(event.getHotbarButton());

        if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) return;

        NBTItem item = new NBTItem(stack);

        if (!item.hasTag("id")) return;

        ItemCache cache = pCore.getInstance().getLocatorHandler().fetchCache(item.getUUID("id"));

        if (cache == null) return;

        if (event.getView().getTopInventory().getHolder() instanceof DoubleChest) {
            DoubleChest state = (DoubleChest) event.getView().getTopInventory().getHolder();
            LocationType type = LocationType.DOUBLE_CHEST;
            ItemLocation location = new ItemLocation(state.getLocation(), null, type);

            pCore.getInstance().getLocatorHandler().updateCache(item.getUUID("id"), location);
            return;
        }

        BlockState state = (BlockState) event.getView().getTopInventory().getHolder();
        LocationType type = LocationType.BLOCK_INVENTORY;
        ItemLocation location = new ItemLocation(state == null ? null : state.getLocation(), null, type);

        if (state == null && event.getView().getTopInventory().getType() == InventoryType.ENDER_CHEST) {
            location.setHolderUUID(event.getWhoClicked().getUniqueId());
            location.setType(LocationType.ENDER_CHEST);
        }

        if (state != null) location.setLocation(state.getLocation());

        pCore.getInstance().getLocatorHandler().updateCache(item.getUUID("id"), location);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (!(event.getBlock().getState() instanceof InventoryHolder)) return;

        InventoryHolder holder = (InventoryHolder) event.getBlock().getState();
        for (ItemStack stack : holder.getInventory()) {
            if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) continue;

            NBTItem item = new NBTItem(stack);

            if (!item.hasTag("id")) continue;

            pCore.getInstance().getLocatorHandler().updateCache(item.getUUID("id"), new ItemLocation(event.getBlock().getLocation(), null, LocationType.GROUND_ITEM));
        }
    }

    @EventHandler
    public void onExplode(BlockExplodeEvent event) {
        if (!(event.getBlock().getState() instanceof InventoryHolder)) return;

        InventoryHolder holder = (InventoryHolder) event.getBlock().getState();
        for (ItemStack stack : holder.getInventory()) {
            if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) continue;

            NBTItem item = new NBTItem(stack);

            if (!item.hasTag("id")) continue;

            pCore.getInstance().getLocatorHandler().updateCache(item.getUUID("id"), new ItemLocation(event.getBlock().getLocation(), null, LocationType.GROUND_ITEM));
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        ItemStack stack = event.getItemDrop().getItemStack();
        if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) return;

        NBTItem item = new NBTItem(stack);

        if (!item.hasTag("id")) return;

        pCore.getInstance().getLocatorHandler().updateCache(item.getUUID("id"), new ItemLocation(event.getItemDrop().getLocation(), null, LocationType.GROUND_ITEM));
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        ItemStack stack = event.getEntity().getItemStack();
        if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) return;

        NBTItem item = new NBTItem(stack);

        if (!item.hasTag("id")) return;

        pCore.getInstance().getLocatorHandler().updateCache(item.getUUID("id"), new ItemLocation(event.getEntity().getLocation(), null, LocationType.GROUND_ITEM));
    }

    @EventHandler
    public void onDespawn(ItemDespawnEvent event) {
        ItemStack stack = event.getEntity().getItemStack();
        if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) return;

        NBTItem item = new NBTItem(stack);

        if (!item.hasTag("id")) return;

        pCore.getInstance().getLocatorHandler().updateCache(item.getUUID("id"), new ItemLocation(null, null, LocationType.DESPAWNED));
    }

    @EventHandler
    public void onCombust(EntityCombustEvent event) {
        if (!(event.getEntity() instanceof Item)) return;

        Item item = (Item) event.getEntity();
        ItemStack stack = item.getItemStack();

        if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) return;

        NBTItem nbtItem = new NBTItem(stack);

        if (!nbtItem.hasTag("id")) return;

        pCore.getInstance().getLocatorHandler().updateCache(nbtItem.getUUID("id"), new ItemLocation(null, null, LocationType.BURNT));
    }

    @EventHandler
    public void onCombust(EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof Item)) return;

        Item item = (Item) event.getEntity();
        ItemStack stack = item.getItemStack();

        if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) return;

        NBTItem nbtItem = new NBTItem(stack);

        if (!nbtItem.hasTag("id")) return;

        pCore.getInstance().getStorageHandler().getStorage().wipeLocation(nbtItem.getUUID("id"), true);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        for (ItemStack stack : event.getDrops()) {
            if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) continue;

            NBTItem item = new NBTItem(stack);

            if (!item.hasTag("id")) continue;

            pCore.getInstance().getStorageHandler().getStorage().updateLocation(item.getUUID("id"), new ItemLocation(null, event.getEntity().getUniqueId(), LocationType.PLAYER_INVENTORY), true);
            pCore.getInstance().getLocatorHandler().updateCache(item.getUUID("id"), new ItemLocation(event.getEntity().getLocation(), null, LocationType.GROUND_ITEM));
        }
    }

}
