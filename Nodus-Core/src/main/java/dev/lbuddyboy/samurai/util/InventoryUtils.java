package dev.lbuddyboy.samurai.util;

import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

public class InventoryUtils {

    public static final SimpleDateFormat DEATH_TIME_FORMAT = new SimpleDateFormat("MM.dd.yy HH:mm");
    public static final String KILLS_LORE_IDENTIFIER = CC.MAIN + "Kills: " + ChatColor.WHITE.toString() + ChatColor.BOLD;

    public static final ItemStack CROWBAR;
    public static final ItemStack ANTIDOTE = ItemBuilder.of(Material.POTION).data((short) 8196).name(ChatColor.GREEN + "Antidote").setLore(new ArrayList<>()).addToLore(ChatColor.GRAY + "Drink to relieve yourself of potion debuffs.").build();

    public static final ItemStack ZOMBIE_SPAWNER = ItemBuilder.of(Material.SPAWNER).name(CC.GREEN + CC.BOLD + "Zombie Spawner").build();
    public static final ItemStack SKELETON_SPAWNER = ItemBuilder.of(Material.SPAWNER).name(CC.GREEN + CC.BOLD + "Skeleton Spawner").build();
    public static final ItemStack SPIDER_SPAWNER = ItemBuilder.of(Material.SPAWNER).name(CC.GREEN + CC.BOLD + "Spider Spawner").build();
    public static final ItemStack CAVE_SPIDER_SPAWNER = ItemBuilder.of(Material.SPAWNER).name(CC.GREEN + CC.BOLD + "Cave Spider Spawner").build();

    public static final String CROWBAR_NAME = ChatColor.RED + "Crowbar";

    public static final int CROWBAR_PORTALS = 6;
    public static final int CROWBAR_SPAWNERS = 1;

    public static boolean isNaked(Player player) {
        return player.getInventory().getHelmet() == null && player.getInventory().getChestplate() == null && player.getInventory().getLeggings() == null && player.getInventory().getBoots() == null;
    }

    static {
        CROWBAR = new ItemStack(Material.DIAMOND_HOE);
        ItemMeta meta = CROWBAR.getItemMeta();

        meta.setDisplayName(CROWBAR_NAME);
        meta.setLore(getCrowbarLore(CROWBAR_PORTALS, CROWBAR_SPAWNERS));

        CROWBAR.setItemMeta(meta);
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

//    public static boolean conformEnchants(ItemStack item) {
//        if (item == null) {
//            return (false);
//        }
//
//        if (item.hasItemMeta()) {
//            ItemMeta itemMeta = item.getItemMeta();
//
//            if (itemMeta.hasDisplayName() && itemMeta.getDisplayName().contains(ChatColor.AQUA.toString())) {
//                return (false);
//            }
//        }
//
//        boolean fixed = false;
//        Map<Enchantment, Integer> enchants = item.getEnchantments();
//
//        for (Map.Entry<Enchantment, Integer> enchantmentSet : enchants.entrySet()) {
//            if (enchantmentSet.getValue() > enchantmentSet.getKey().getMaxLevel()) {
//                item.addUnsafeEnchantment(enchantmentSet.getKey(), enchantmentSet.getKey().getMaxLevel());
//                fixed = true;
//            }
//        }
//
//        return (fixed);
//    }

    public static ItemStack addToPart(ItemStack item, String title, String key, int max) {
        ItemMeta meta = item.getItemMeta();

        if (meta.hasLore() && meta.getLore().size() != 0) {
            List<String> lore = meta.getLore();

            if (lore.contains(title)) {
                int titleIndex = lore.indexOf(title);
                int keys = 0;

                for (int i = titleIndex; i < lore.size(); i++) {
                    if (lore.get(i).equals("")) {
                        break;
                    }

                    keys += 1;
                }

                lore.add(titleIndex + 1, key);

                if (keys > max) {
                    lore.remove(titleIndex + keys);
                }
            } else {
                lore.add("");
                lore.add(title);
                lore.add(key);
            }

            meta.setLore(lore);
        } else {
            List<String> lore = new ArrayList<>();

            lore.add("");
            lore.add(title);
            lore.add(key);

            meta.setLore(lore);
        }

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack addDeath(ItemStack item, String key) {
        return (addToPart(item, ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Deaths:", key, 10));
    }

    public static ItemStack addKill(ItemStack item, String key) {
        int killsIndex = 1;
        int[] lastKills = {3, 4, 5};
        int currentKills = 1;
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();

        if (meta.hasLore()) {
            lore = meta.getLore();

            if (meta.getLore().size() > killsIndex) {
                String killStr = lore.get(killsIndex);
                if (killStr.contains(":"))
                    currentKills += Integer.parseInt(ChatColor.stripColor(killStr.split(":")[1]).trim());
            }

            for (int j : lastKills) {
                if (j == lastKills[lastKills.length - 1]) {
                    continue;
                }
                if (lore.size() > j) {
                    String atJ = meta.getLore().get(j);

                    if (lore.size() <= j + 1) {
                        lore.add(null);
                    }

                    lore.set(j + 1, atJ);
                }

            }
        }

        if (lore.size() <= killsIndex) {
            for (int i = lore.size(); i <= killsIndex + 1; i++) {
                lore.add("");
            }
        }
        lore.set(killsIndex, "§4§lKills:§f " + currentKills);

        int firsKill = lastKills[0];

        if (lore.size() <= firsKill) {
            for (int i = lore.size(); i <= firsKill + 1; i++) {
                lore.add("");
            }
        }

        lore.set(firsKill, key);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return (item);
    }

    public static List<String> getCrowbarLore(int portals, int spawners) {
        List<String> lore = new ArrayList<>();

        lore.add("");
        lore.add(ChatColor.GRAY + "Can Break:");
        lore.add(ChatColor.WHITE + " - " + ChatColor.AQUA + "End Portals: " + ChatColor.YELLOW + "{" + ChatColor.BLUE + portals + ChatColor.YELLOW + "}");
        lore.add(ChatColor.WHITE + " - " + ChatColor.AQUA + "Spawners: " + ChatColor.YELLOW + "{" + ChatColor.BLUE + spawners + ChatColor.YELLOW + "}");

        return (lore);
    }

    public static List<String> getKOTHRewardKeyLore(String koth) {
        List<String> lore = new ArrayList<>();
        DateFormat sdf = new SimpleDateFormat("M/d HH:mm:ss");

        lore.add("");
        lore.add(ChatColor.WHITE + " - " + ChatColor.AQUA + "Obtained from: " + ChatColor.YELLOW + "{" + ChatColor.BLUE + koth + ChatColor.YELLOW + "}");
        lore.add(ChatColor.WHITE + " - " + ChatColor.AQUA + "Time: " + ChatColor.YELLOW + "{" + ChatColor.BLUE + sdf.format(new Date()).replace(" AM", "").replace(" PM", "") + ChatColor.YELLOW + "}");
        return (lore);
    }

    public static ItemStack generateKOTHRewardKey(String koth) {
        ItemStack key = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta meta = key.getItemMeta();

        meta.setDisplayName(ChatColor.RED + "KOTH Reward Key");
        meta.setLore(getKOTHRewardKeyLore(koth));

        key.setItemMeta(meta);
        return (key);
    }

    public static int getCrowbarUsesPortal(ItemStack item) {
        return (Integer.parseInt(getLoreData(item, 2)));
    }

    public static int getCrowbarUsesSpawner(ItemStack item) {
        return (Integer.parseInt(getLoreData(item, 3)));
    }

    public static boolean isArmor(ItemStack item) {
        return item.getType().getId() > 297 && item.getType().getId() < 318;
    }

    public static boolean isBoots(ItemStack item) {
        return item.getType().getId() == 301 || item.getType().getId() == 305 || item.getType().getId() == 309 || item.getType().getId() == 313 || item.getType().getId() == 317;
    }

    public static String getLoreData(ItemStack item, int index) {
        List<String> lore = item.getItemMeta().getLore();

        if (lore != null && index < lore.size()) {
            String str = ChatColor.stripColor(lore.get(index));
            return (str.split("\\{")[1].replace("}", ""));
        }

        return ("");
    }

    public static boolean isSimilar(ItemStack item, String name) {
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

    public static void removeAmountFromInventory(Player player, ItemStack itemStack, int amount, Predicate<ItemStack> predicate) {
        changeInventoryAmount(player, itemStack, amount, false, predicate);
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

    public static boolean changeInventoryAmount(Inventory inventory, ItemStack itemStack, int amount, boolean add) {
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

    public static boolean changeInventoryAmount(Inventory inventory, ItemStack itemStack, int amount, boolean add, boolean similar) {
        if (add) {
            tryFit(inventory, itemStack);
            return true;
        }
        boolean added = false;
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);

            if (item != null && (similar ? item.isSimilar(itemStack) : item.getType() == itemStack.getType())) {
                int itemAmount = item.getAmount();
                int sum = itemAmount - amount;
                item.setAmount(sum);
                inventory.setItem(i, sum > 0 ? item : null);
                break;
            }
        }

        return added;
    }

    private static boolean changeInventoryAmount(Player player, ItemStack itemStack, int amount, boolean add, Predicate<ItemStack> predicate) {
        boolean added = false;
        if (add) {
            tryFit(player.getInventory(), itemStack);
            return true;
        }
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.isSimilar(itemStack)) {
                if (predicate.test(item)) continue;

                int itemAmount = item.getAmount();
                int sum = itemAmount - amount;
                item.setAmount(sum);
                player.getInventory().setItem(i, sum > 0 ? item : null);
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

    public static void resetInventoryDelayed(Player player) {
        Runnable task = () -> resetInventoryNow(player);
        Bukkit.getScheduler().runTaskLater(Samurai.getInstance(), task, RESET_DELAY_TICKS);
    }

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