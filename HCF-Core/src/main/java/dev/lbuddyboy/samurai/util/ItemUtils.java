package dev.lbuddyboy.samurai.util;

import dev.lbuddyboy.flash.util.bukkit.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ItemUtils {

    public static ItemStack healthPotionII;

    public static List<String> getLore(ItemStack stack) {
        if (stack.getItemMeta() != null && stack.getItemMeta().hasLore()) {
            return stack.getItemMeta().getLore();
        }
        return new ArrayList<>();
    }

    /**
     * Checks if a {@link ItemStack} is an instant heal potion (if its type is {@link PotionType#INSTANT_HEAL})
     */
    public static final Predicate<ItemStack> INSTANT_HEAL_POTION_PREDICATE = item -> {
        if (item.getType() == Material.SPLASH_POTION) {
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            if (meta != null) {
                return meta.getBasePotionData().getType() == PotionType.INSTANT_HEAL;
            }
        }
        return false;
    };

    /**
     */
    public static final Predicate<ItemStack> SOUP_PREDICATE = item -> item.getType() == Material.LEGACY_MUSHROOM_SOUP;

    /**
     * Checks if a {@link ItemStack} is a debuff potion
     */
    public static final Predicate<ItemStack> DEBUFF_POTION_PREDICATE = item -> {
        if (item.getType() == Material.POTION) {
            PotionType type = Potion.fromItemStack(item).getType();
            return type == PotionType.WEAKNESS || type == PotionType.SLOWNESS
                    || type == PotionType.POISON || type == PotionType.INSTANT_DAMAGE;
        } else {
            return false;
        }
    };

    /**
     * Checks if a {@link ItemStack} is edible (if its type passes {@link Material#isEdible()})
     */
    public static final Predicate<ItemStack> EDIBLE_PREDICATE = item -> item.getType().isEdible();

    /**
     * Returns the number of stacks of items matching the predicate provided.
     *
     * @param items ItemStack array to scan
     * @param predicate The predicate which will be applied to each non-null temStack.
     * @return The amount of ItemStacks which matched the predicate, or 0 if {@code items} was null.
     */
    public static int countStacksMatching(ItemStack[] items, Predicate<ItemStack> predicate) {
        if (items == null) {
            return 0;
        }

        int amountMatching = 0;

        for (ItemStack item : items) {
            if (item != null && predicate.test(item)) {
                amountMatching++;
            }
        }

        return amountMatching;
    }

    public static boolean hasEmptyInventory(Player player) {
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                return false;
            }
        }

        for (ItemStack itemStack : player.getInventory().getArmorContents()) {
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                return false;
            }
        }

        return true;
    }

    public static void setDisplayName(ItemStack stack, String name) {
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        stack.setItemMeta(meta);
    }

    public static void itemStackToConfigSect(ItemStack stack, int slot, String key, FileConfiguration config) {

        if (slot > 0) config.set(key + ".slot", slot);
        config.set(key + ".material", stack.getType().name());

        ItemMeta meta = stack.getItemMeta();

        if (meta == null) {
            config.set(key + ".name", "");
            config.set(key + ".lore", Collections.singletonList(""));
            config.set(key + ".item-flags", Collections.singletonList("HIDE_ENCHANTS"));
        }

        if (meta != null && meta.hasDisplayName()) config.set(key + ".name", meta.getDisplayName().replaceAll("§", "&"));
        else config.set(key + ".name", "");

        if (meta != null && meta.hasLore()) {
            List<String> lore = new ArrayList<>();
            meta.getLore().forEach(s -> lore.add(s.replaceAll("§", "&")));
            config.set(key + ".lore", lore);
        }
        else config.set(key + ".lore", Collections.singletonList(""));

        try {
            ItemMeta.class.getMethod("getItemFlags");

            if (meta != null && !meta.getItemFlags().isEmpty())
                config.set(key + ".item-flags", meta.getItemFlags().stream().map(ItemFlag::name).collect(Collectors.toList()));
            else config.set(key + ".item-flags", Collections.singletonList("HIDE_ENCHANTS"));

        } catch (NoSuchMethodException ignored) {

        }

        config.set(key + ".data", stack.getDurability());
        config.set(key + ".amount", stack.getAmount());

        List<String> enchants = new ArrayList<>();

        for (Map.Entry<Enchantment, Integer> entry : stack.getEnchantments().entrySet()) enchants.add(entry.getKey().getName() + ":" + entry.getValue());
        if (!enchants.isEmpty()) config.set(key + ".enchants", enchants);
    }

    public static ItemStack itemStackFromConfigSect(String key, ConfigurationSection section) {
        ItemBuilder builder = new ItemBuilder(Material.getMaterial(section.getString(key + ".material")), section.getInt(key + ".amount"));

        String name = section.getString(key + ".name");
        List<String> lore = section.getStringList(key + ".lore");
        try {
            ItemMeta.class.getMethod("getItemFlags");

            List<ItemFlag> flags = section.getStringList(key + ".item-flags").stream().map(ItemFlag::valueOf).collect(Collectors.toList());
            if (!flags.isEmpty()) builder.addItemFlag(flags.toArray(new ItemFlag[0]));

        } catch (NoSuchMethodException ignored) {

        }
        List<String> enchants = section.getStringList(key + ".enchants");

        if (!name.isEmpty()) builder.setName(name);
        if (!lore.isEmpty()) builder.setLore(lore);
        if (!enchants.isEmpty()) {
            for (String s : enchants) {
                String[] parts = s.split(":");
                builder.addUnsafeEnchantment(Enchantment.getByName(parts[0].toUpperCase()), Integer.parseInt(parts[1]));
            }
        }

        builder.setDurability(section.getInt(key + ".data"));

        return builder.create();
    }

    /**
     * Converts the player inventory to a String array of Base64 strings. First string is the content and second string is the armor.
     *
     * @param playerInventory to turn into an array of strings.
     * @return Array of strings: [ main content, armor content ]
     * @throws IllegalStateException
     */
    public static String[] playerInventoryToBase64(PlayerInventory playerInventory) throws IllegalStateException {
        //get the main content part, this doesn't return the armor
        String content = toBase64(playerInventory);
        String armor = itemStackArrayToBase64(playerInventory.getArmorContents());

        return new String[]{content, armor};
    }

    /**
     * A method to serialize an {@link ItemStack} array to Base64 String.
     * <p>
     * <p/>
     * <p>
     * Based off of {@link #toBase64(Inventory)}.
     *
     * @param items to turn into a Base64 String.
     * @return Base64 string of the items.
     * @throws IllegalStateException
     */
    public static String itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(items.length);

            // Save every element in the list
            for (int i = 0; i < items.length; i++) {
                dataOutput.writeObject(items[i]);
            }

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    /**
     * A method to serialize an inventory to Base64 string.
     * <p>
     * <p/>
     * <p>
     * Special thanks to Comphenix in the Bukkit forums or also known
     * as aadnk on GitHub.
     *
     * <a href="https://gist.github.com/aadnk/8138186">Original Source</a>
     *
     * @param inventory to serialize
     * @return Base64 string of the provided inventory
     * @throws IllegalStateException
     */
    public static String toBase64(Inventory inventory) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(inventory.getSize());

            // Save every element in the list
            for (int i = 0; i < inventory.getSize(); i++) {
                dataOutput.writeObject(inventory.getItem(i));
            }

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    /**
     * A method to get an {@link Inventory} from an encoded, Base64, string.
     * <p>
     * <p/>
     * <p>
     * Special thanks to Comphenix in the Bukkit forums or also known
     * as aadnk on GitHub.
     *
     * <a href="https://gist.github.com/aadnk/8138186">Original Source</a>
     *
     * @param data Base64 string of data containing an inventory.
     * @return Inventory created from the Base64 string.
     * @throws IOException
     */
    public static Inventory fromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            Inventory inventory = Bukkit.getServer().createInventory(null, dataInput.readInt());

            // Read the serialized inventory
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, (ItemStack) dataInput.readObject());
            }

            dataInput.close();
            return inventory;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

    /**
     * Gets an array of ItemStacks from Base64 string.
     * <p>
     * <p/>
     * <p>
     * Base off of {@link #fromBase64(String)}.
     *
     * @param data Base64 string to convert to ItemStack array.
     * @return ItemStack array created from the Base64 string.
     * @throws IOException
     */
    public static ItemStack[] itemStackArrayFromBase64(String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            // Read the serialized inventory
            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (ClassNotFoundException | IOException ignored) {
            return null;
        }
    }

    public static void tryFit(Player player, ItemStack stack, boolean armor) {
        PlayerInventory inventory = player.getInventory();
        if (stack == null) return;

        if (armor) {
            String material = stack.getType().name();
            EquipmentSlot slot = material.endsWith("_HELMET") ? EquipmentSlot.HEAD :
                    material.endsWith("_CHESTPLATE") ? EquipmentSlot.CHEST :
                            material.endsWith("_LEGGINGS") ? EquipmentSlot.LEGS :
                                    material.endsWith("_BOOTS") ? EquipmentSlot.FEET : null;

            if (slot == null) {
                System.out.println("Error equipping armor. Could not find a valid spot.");
                return;
            }

            if (slot == EquipmentSlot.HEAD && player.getInventory().getHelmet() == null) {
                inventory.setHelmet(stack);
                return;
            }

            if (slot == EquipmentSlot.CHEST && player.getInventory().getChestplate() == null) {
                inventory.setChestplate(stack);
                return;
            }

            if (slot == EquipmentSlot.LEGS && player.getInventory().getLeggings() == null) {
                inventory.setLeggings(stack);
                return;
            }

            if (slot == EquipmentSlot.FEET && player.getInventory().getBoots() == null) {
                inventory.setBoots(stack);
                return;
            }

            tryFit(player, stack, false);
            return;
        }

        int first = inventory.firstEmpty();

        if (first == -1) {
            player.getWorld().dropItemNaturally(player.getLocation(), stack);
            return;
        }

        player.getInventory().addItem(stack);
    }

    public static void removeAmount(Inventory inventory, ItemStack stack, int amount) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && item.isSimilar(stack)) {
                int itemAmount = item.getAmount();
                int sum = itemAmount - amount;
                item.setAmount(sum);
                inventory.setItem(i, sum > 0 ? item : null);
                break;
            }
        }
    }

    public static String getName(ItemStack stack) {
        if (stack.getItemMeta() != null && stack.getItemMeta().hasDisplayName()) {
            return stack.getItemMeta().getDisplayName();
        }
        return getName(stack.getType());
    }

    public static String getName(Material material) {
        String type = material.name().toLowerCase();
        String[] args = type.split("_");
        String name = "";
        boolean first = false;
        for (String arg : args) {
            name += (first ? " " : "") + arg.replaceFirst("" + arg.charAt(0), String.valueOf(arg.charAt(0)).toUpperCase());
            first = true;
        }

        return name;
    }

    public static ItemStack takeItem(ItemStack stack) {
        if (stack.getAmount() > 1) {
            stack.setAmount(stack.getAmount() - 1);
            return stack;
        }
        return null;
    }

}
