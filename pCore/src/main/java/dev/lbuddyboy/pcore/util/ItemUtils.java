package dev.lbuddyboy.pcore.util;

import dev.drawethree.xprison.utils.compat.CompMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ItemUtils {

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

        if (meta != null && !meta.getItemFlags().isEmpty())
            config.set(key + ".item-flags", meta.getItemFlags().stream().map(ItemFlag::name).collect(Collectors.toList()));
        else config.set(key + ".item-flags", Collections.singletonList("HIDE_ENCHANTS"));

        config.set(key + ".data", stack.getDurability());
        config.set(key + ".amount", stack.getAmount());

        List<String> enchants = new ArrayList<>();

        for (Map.Entry<Enchantment, Integer> entry : stack.getEnchantments().entrySet()) enchants.add(entry.getKey().getName() + ":" + entry.getValue());
        if (!enchants.isEmpty()) config.set(key + ".enchants", enchants);
    }

    public static ItemStack itemStackFromConfigSect(String key, ConfigurationSection section) {
        ItemBuilder builder = new ItemBuilder(Material.getMaterial(section.getString(key + ".material")), section.getInt(key + ".amount", 1));

        String name = section.getString(key + ".name");
        List<String> lore = section.getStringList(key + ".lore");
        List<ItemFlag> flags = section.getStringList(key + ".item-flags").stream().map(ItemFlag::valueOf).collect(Collectors.toList());
        List<String> enchants = section.getStringList(key + ".enchants");

        if (!name.isEmpty()) builder.setName(name);
        if (!lore.isEmpty()) builder.setLore(lore);
        if (!flags.isEmpty()) builder.addItemFlag(flags.toArray(new ItemFlag[0]));
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
            return new ItemStack[0];
        }
    }

    public static void tryFit(Player player, ItemStack stack, boolean armor) {
        tryFitBool(player, stack, armor);
    }

    public static boolean tryFitBool(Player player, ItemStack stack, boolean armor) {
        PlayerInventory inventory = player.getInventory();

        if (armor) {
            String material = stack.getType().name();
            EquipmentSlot slot = material.endsWith("_HELMET") ? EquipmentSlot.HEAD :
                    material.endsWith("_CHESTPLATE") ? EquipmentSlot.CHEST :
                            material.endsWith("_LEGGINGS") ? EquipmentSlot.LEGS :
                                    material.endsWith("_BOOTS") ? EquipmentSlot.FEET : null;

            if (slot == null) {
                System.out.println("Error equipping armor. Could not find a valid spot.");
                return false;
            }

            if (slot == EquipmentSlot.HEAD && player.getInventory().getHelmet() == null) {
                inventory.setHelmet(stack);
                return true;
            }

            if (slot == EquipmentSlot.CHEST && player.getInventory().getChestplate() == null) {
                inventory.setChestplate(stack);
                return true;
            }

            if (slot == EquipmentSlot.LEGS && player.getInventory().getLeggings() == null) {
                inventory.setLeggings(stack);
                return true;
            }

            if (slot == EquipmentSlot.FEET && player.getInventory().getBoots() == null) {
                inventory.setBoots(stack);
                return true;
            }

            tryFit(player, stack, false);
            return true;
        }

        int first = inventory.firstEmpty();

        if (first == -1) {
            player.getWorld().dropItemNaturally(player.getLocation(), stack);
            return false;
        }

        player.getInventory().addItem(stack);
        return true;
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

    public static String stringify(ItemStack stack) {

        // MATERIAL~AMOUNT~NAME~LORE~ITEMFLAGS~ENCHANTS

        String args = "";

        if (stack == null || stack.getType() == Material.AIR) return "";

        args += stack.getType().name();
        args += "~" + stack.getAmount();
        if (stack.hasItemMeta()) {
            if (stack.getItemMeta().hasDisplayName()) args += "~" + stack.getItemMeta().getDisplayName();
            else args += "~";

            AtomicInteger count = new AtomicInteger();
            if (stack.getItemMeta().hasLore()) args += "~" + stack.getItemMeta().getLore().stream().map(s -> {
                if (count.get() == 0) {
                    count.getAndIncrement();
                    return s;
                } else return "|" + s;
            });
            else args += "~";
            if (!stack.getItemMeta().getItemFlags().isEmpty())
                args += "~" + stack.getItemMeta().getItemFlags().stream().map(s -> {
                    if (count.get() == 0) {
                        count.getAndIncrement();
                        return s.name();
                    } else return "|" + s.name();
                });
        } else {
            args += "~~~";
        }

        if (!stack.getEnchantments().isEmpty()) {
            args += "~" + StringUtils.join(stack.getEnchantments().entrySet().stream().map(entry -> entry.getKey().getName() + ";" + entry.getValue()).collect(Collectors.toList()));
        } else {
            args += "~";
        }

        return args;

    }

    public static ItemStack destringify(String string) {

        // MATERIAL~AMOUNT~NAME~LORE~ITEMFLAGS

        String[] args = string.split("~");

        ItemBuilder builder = new ItemBuilder(Material.getMaterial(args[0]), Integer.parseInt(args[1]));

        if (!args[2].isEmpty()) builder.setName(args[2]);
        if (!args[3].isEmpty()) builder.setLore(args[3].contains("|") ? args[3].split("\\|") : new String[]{args[3]});

        try {
            if (!args[4].isEmpty())
                builder.addItemFlag(Arrays.stream(args[4].contains("|") ? args[4].split("\\|") : new String[]{args[4]}).map(ItemFlag::valueOf).filter(s -> Arrays.stream(ItemFlag.values()).map(ItemFlag::name).collect(Collectors.toList()).contains(s.name())).collect(Collectors.toList()).toArray(new ItemFlag[0]));
        } catch (Exception ignored) {
            System.out.println("Error occured reading " + string);
        }

        try {
            if (!args[5].isEmpty()) {
                if (!args[5].contains(",")) {
                    String[] enchantPart = args[5].split(":");
                    String enchant = enchantPart[0];
                    Enchantment enchantment = Enchantment.getByName(enchant);

                    if (enchantment != null) {
                        int level = Integer.parseInt(enchantPart[1]);
                        builder.addEnchantment(enchantment, level);
                    } else {
                        System.out.println("Invalid Enchantment trying to read " + string);
                    }
                } else {
                    for (String s : args[5].split(",")) {
                        String[] enchantPart = s.split(":");
                        String enchant = enchantPart[0];
                        Enchantment enchantment = Enchantment.getByName(enchant);

                        if (enchantment == null) {
                            System.out.println("Invalid Enchantment trying to read " + string);
                            continue;
                        }

                        int level = Integer.parseInt(enchantPart[1]);
                        builder.addEnchantment(enchantment, level);
                    }
                }
            }
        } catch (Exception ignored) {
            System.out.println("Error occured reading " + string);
        }

        return builder.create();
    }

    public static String getName(ItemStack stack) {
        if (stack.getItemMeta() != null && stack.getItemMeta().hasDisplayName()) {
            return stack.getItemMeta().getDisplayName();
        }
        return getName(stack.getType());
    }

    public static List<String> getLore(ItemStack stack) {
        if (stack.getItemMeta() != null && stack.getItemMeta().hasLore()) {
            return stack.getItemMeta().getLore();
        }
        return new ArrayList<>();
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

    public static ItemStack getRobotItem(Config config, String key) {
        ItemStack stack = new ItemStack(CompMaterial.fromString(config.getString(key + ".material")).toItem());
        if (stack.getItemMeta() instanceof LeatherArmorMeta) {
            LeatherArmorMeta meta = (LeatherArmorMeta) stack.getItemMeta();

            meta.setColor(getColor(config.getString(key + ".color")));
            stack.setItemMeta(meta);
        }
        if (config.getBoolean(key + ".glowing", false)) {
            ItemMeta meta = stack.getItemMeta();

            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            stack.setItemMeta(meta);
        }

        return stack;
    }

    public static Color getColor(String color) {
        if (color.equalsIgnoreCase("black")) {
            return Color.BLACK;
        } else if (color.equalsIgnoreCase("blue")) {
            return Color.BLUE;
        } else if (color.equalsIgnoreCase("maroon")) {
            return Color.MAROON;
        } else if (color.equalsIgnoreCase("yellow")) {
            return Color.YELLOW;
        } else if (color.equalsIgnoreCase("green")) {
            return Color.GREEN;
        } else if (color.equalsIgnoreCase("gray")) {
            return Color.GRAY;
        } else if (color.equalsIgnoreCase("aqua")) {
            return Color.AQUA;
        } else if (color.equalsIgnoreCase("orange")) {
            return Color.ORANGE;
        } else if (color.equalsIgnoreCase("red")) {
            return Color.RED;
        } else if (color.equalsIgnoreCase("purple")) {
            return Color.PURPLE;
        } else if (color.equalsIgnoreCase("white")) {
            return Color.WHITE;
        } else if (color.equalsIgnoreCase("lime")) {
            return Color.LIME;
        } else {
            return null;
        }
    }

}
