package dev.lbuddyboy.samurai.commands.donator;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;

@CommandAlias("condense")
@CommandPermission("foxtrot.condense")
public class CondenseCommand extends BaseCommand {

    public static final HashSet<Material> TRANSPARENT_BLOCKS = new HashSet<>();

    @Default
    public void invis(Player sender) {
        Inventory inventory = getTargetInventory(sender, 5);
        if (inventory == null) {
            inventory = sender.getInventory();
        }

        ArrayList<ItemStack> contents = new ArrayList<>(inventory.getSize());

        do {
            ItemStack[] var5 = inventory.getContents();
            int var6 = var5.length;

            contents.addAll(Arrays.asList(var5).subList(0, var6));

            inventory.clear();
            sortItems(contents);

            for (ItemStack item : contents) {
                if (item != null) {
                    inventory.addItem(item);
                }
            }

            contents.clear();
        } while (this.condenseInventory(sender, inventory));

        sender.sendMessage(ChatColor.GREEN + "Inventory condensed");
    }

    public boolean canBeDisabled() {
        return true;
    }

    private double getConversionRate(Material type) {
        switch (type) {
            case LAPIS_LAZULI:
            case IRON_INGOT:
            case GOLD_INGOT:
            case DIAMOND:
            case EMERALD:
            case REDSTONE:
            case COAL:
                return 0.1111111111111111;
            case GLOWSTONE_DUST:
            case CLAY_BALL:
            case SNOWBALL:
                return 0.25;
            default:
                return 1.0;
        }
    }

    private Material getConversionType(Material type) {
        switch (type) {
            case LAPIS_LAZULI:
                return Material.LAPIS_BLOCK;
            default:
                return type;
            case IRON_INGOT:
                return Material.IRON_BLOCK;
            case GOLD_INGOT:
                return Material.GOLD_BLOCK;
            case DIAMOND:
                return Material.DIAMOND_BLOCK;
            case EMERALD:
                return Material.EMERALD_BLOCK;
            case REDSTONE:
                return Material.REDSTONE_BLOCK;
            case COAL:
                return Material.COAL_BLOCK;
            case GLOWSTONE_DUST:
                return Material.GLOWSTONE;
            case CLAY_BALL:
                return Material.CLAY;
            case SNOWBALL:
                return Material.SNOW_BLOCK;
        }
    }

    private boolean condenseInventory(Entity entity, Inventory inventory) {
        boolean condensed = false;
        ArrayList<ItemStack> overflow = new ArrayList<>();
        ArrayList<ItemStack> to_remove = new ArrayList<>(inventory.getSize());
        ItemStack[] var6 = inventory.getContents();

        for (ItemStack item : var6) {
            if (item != null) {
                double conversion = this.getConversionRate(item.getType());
                if (conversion != 1.0) {
                    int amount = item.getAmount();
                    int new_item_amount = (int) ((double) amount * conversion);
                    if (new_item_amount != 0) {
                        condensed = true;
                        int old_item_amount = (int) ((double) amount % (1.0 / conversion));
                        ItemStack new_item = new ItemStack(this.getConversionType(item.getType()), new_item_amount, item.getDurability());
                        new_item.setItemMeta(item.getItemMeta().clone());
                        overflow.addAll(inventory.addItem(new ItemStack[]{new_item}).values());
                        if (old_item_amount <= 0) {
                            to_remove.add(item);
                        } else {
                            item.setAmount(old_item_amount);
                        }
                    }
                }
            }
        }

        Iterator<ItemStack> overflow_iter = to_remove.iterator();

        ItemStack item;
        while (overflow_iter.hasNext()) {
            item = overflow_iter.next();
            inventory.removeItem(item);
        }

        overflow_iter = overflow.iterator();

        while (overflow_iter.hasNext()) {
            item = (ItemStack) overflow_iter.next();
            if (!hasRoomForItem(inventory, item)) {
                entity.getWorld().dropItem(entity.getLocation(), item).setVelocity(new Vector(0, 0, 0));
            } else {
                inventory.addItem(item);
                overflow_iter.remove();
            }
        }

        return condensed;
    }

    public static boolean hasRoomForItem(Inventory inventory, ItemStack item) {
        int space_available = 0;
        ItemStack[] var3 = inventory.getContents();
        int var4 = var3.length;

        for (ItemStack compare : var3) {
            if (compare == null) {
                space_available += item.getMaxStackSize();
            } else if (item.isSimilar(compare)) {
                space_available += compare.getMaxStackSize() - compare.getAmount();
            }
        }

        return space_available >= item.getAmount();
    }

    public static Inventory getTargetInventory(Player player, int max_distance) {
        Block block = player.getTargetBlock(TRANSPARENT_BLOCKS, max_distance);
        if (block.getType() != Material.CHEST && block.getType() != Material.TRAPPED_CHEST) {
            if (block.getType() == Material.ENDER_CHEST) {
                return player.getEnderChest();
            }
        } else {
            if (block.getState() instanceof Chest) {
                return ((Chest) block.getState()).getInventory();
            }

            if (block.getState() instanceof DoubleChest) {
                return ((DoubleChest) block.getState()).getInventory();
            }
        }
        return null;
    }

    public static void sortItems(List<ItemStack> items) {
        items.sort(ItemStackComparator.INSTANCE);
    }

    static {
        Material[] var0 = Material.values();
        int var1 = var0.length;

        for (Material material : var0) {
            if (!material.isSolid()) {
                TRANSPARENT_BLOCKS.add(material);
            }
        }

    }

    public static class ItemStackComparator implements Comparator<ItemStack> {

        public static final ItemStackComparator INSTANCE = new ItemStackComparator();

        public int compare(ItemStack i1, ItemStack i2) {
            if (i1 == null && i2 != null) {
                return 1;
            } else if (i1 != null && i2 == null) {
                return -1;
            } else if (i1 == null && i2 == null) {
                return 0;
            } else if (i1.getType().getId() < i2.getType().getId()) {
                return -1;
            } else if (i1.getType().getId() > i2.getType().getId()) {
                return 1;
            } else if (i1.getDurability() < i2.getDurability()) {
                return -1;
            } else {
                return i1.getDurability() > i2.getDurability() ? 1 : 0;
            }
        }
    }

}
