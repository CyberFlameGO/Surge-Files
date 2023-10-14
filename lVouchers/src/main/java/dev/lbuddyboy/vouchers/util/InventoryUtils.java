package dev.lbuddyboy.vouchers.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public class InventoryUtils {

    public static String getLoreData(ItemStack item, int index) {
        List<String> lore = item.getItemMeta().getLore();

        if (lore != null && index < lore.size()) {
            String str = ChatColor.stripColor(lore.get(index));
            return (str.split("\\{")[1].replace("}", ""));
        }

        return ("");
    }

    public static boolean isSimilar(ItemStack item, String name){
        if (item.hasItemMeta()) {
            if (item.getItemMeta().hasDisplayName()) {
                return (item.getItemMeta().getDisplayName().equals(name));
            }
        }

        return (false);
    }

    public static void removeAmountFromInventory(Inventory inventory, ItemStack itemStack, int amount) {
        changeInventoryAmount(inventory, itemStack, amount, false);
    }

    public static boolean removeAmountFromInventoryBool(Inventory inventory, ItemStack itemStack, int amount) {
        return changeInventoryAmount(inventory, itemStack, amount, false);
    }

    public static boolean addAmountToInventory(Inventory inventory, ItemStack itemStack) {
        return changeInventoryAmount(inventory, itemStack, itemStack.getAmount(), true);
    }

    public static boolean addAmountToInventory(Inventory inventory, ItemStack itemStack, int amount) {
        return changeInventoryAmount(inventory, itemStack, amount, true);
    }

    private static boolean changeInventoryAmount(Inventory inventory, ItemStack itemStack, int amount, boolean add) {
        if (add) {
            tryFit(inventory, itemStack);
            return true;
        }
        boolean added = false;
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && item.isSimilar(itemStack)) {
                int itemAmount = item.getAmount();
                int sum = itemAmount - amount;
                item.setAmount(sum);
                inventory.setItem(i, sum > 0 ? item : null);
                break;
            }
        }

        return added;
    }

    public static boolean hasInventorySpace(Inventory inventory, ItemStack item) {
        int free = 0;
        for (ItemStack itemStack : inventory.getStorageContents()) {
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                free += item.getMaxStackSize();
            } else if (itemStack.isSimilar(item)) {
                free += item.getMaxStackSize() - itemStack.getAmount();
            }
        }
        return free >= item.getAmount();
    }

    public static void tryFit(Inventory inventory, ItemStack item) {
        if (!hasInventorySpace(inventory, item)) {
            inventory.getLocation().getWorld().dropItemNaturally(inventory.getLocation(), item);
        } else {
            inventory.addItem(item);
        }
    }

    public static final long RESET_DELAY_TICKS = 2L;

    public static void resetInventoryNow(Player player) {
        player.getInventory().setHeldItemSlot(0);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setFireTicks(0);

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        player.updateInventory();
    }

}