package dev.lbuddyboy.bunkers.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.util.Arrays;

public class InventoryUtils {

    public static final ItemStack ANTIDOTE = ItemBuilder.of(Material.POTION).potion(PotionType.POISON, false, false).name("&aAntidote &7(Drink)").addToLore(ChatColor.GRAY + "Drink to relieve yourself of potion debuffs.").build();

    public static boolean isNaked(Player player) {
        return player.getInventory().getHelmet() == null && player.getInventory().getChestplate() == null && player.getInventory().getLeggings() == null && player.getInventory().getBoots() == null;
    }

    public static void fillBucket(Player player) {
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);

            if (item != null && item.getType() == Material.BUCKET) {
                item.setType(Material.WATER_BUCKET);
                player.updateInventory();
                break;
            }
        }
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
                if (!add) {
                    int sum = itemAmount - amount;
                    item.setAmount(sum);
                    inventory.setItem(i, sum > 0 ? item : null);
                } else {
                    int total = itemAmount + amount;
                    if (total <= item.getType().getMaxStackSize()) {
                        item.setAmount(total);
                        inventory.setItem(i, item);
                        added = true;
                    }
                }
                break;
            }
        }

        if (add && !added) {
            if (inventory.firstEmpty() != -1) {
                itemStack.setAmount(amount);
                inventory.setItem(inventory.firstEmpty(), itemStack);
                added = true;
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

    public static int getOpenSlots(Inventory inventory) {
        return (int) Arrays.stream(inventory.getContents()).filter(stack -> stack == null || stack.getType() == Material.AIR).count();
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