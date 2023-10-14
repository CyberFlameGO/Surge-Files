package dev.lbuddyboy.samurai.util;

import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class ItemStackSerializer {
    public static String serialize(ItemStack item) {
        StringBuilder builder = new StringBuilder();
        builder.append(item.getType().toString());
        builder.append(" " + item.getAmount());
        for (Enchantment enchant : item.getEnchantments().keySet())
            builder.append(" " + enchant.getName() + ":" + item.getEnchantments().get(enchant));
        if (item.hasItemMeta()) {
            if (item.getItemMeta() instanceof PotionMeta) {
                PotionMeta meta = (PotionMeta) item.getItemMeta();
                builder.append(" meta-type:Potion");
                builder.append(" potion-type:" + meta.getBasePotionData().getType().name());
                builder.append(" extended:" + meta.getBasePotionData().isExtended());
                builder.append(" upgraded:" + meta.getBasePotionData().isUpgraded());
            } else {
                builder.append(" meta-type:Normal");
            }
            try {
                List<String> flags = item.getItemMeta().getItemFlags().stream().map(Enum::name).collect(Collectors.toList());
                builder.append(" item-flags:" + StringUtils.join(flags, ","));
            } catch (Exception ignored) {

            }
        }

        String name = getName(item);
        if (name != null) builder.append(" name:" + name);
        String lore = getLore(item);
        if (lore != null) builder.append(" lore:" + lore);
        Color color = getArmorColor(item);
        if (color != null) builder.append(" rgb:" + color.getRed() + "|" + color.getGreen() + "|" + color.getBlue());
        String owner = getOwner(item);
        if (owner != null) builder.append(" owner:" + owner);
        return builder.toString();
    }

    public static ItemStack deserialize(String serializedItem) {
        String[] strings = serializedItem.split(" ");
        Map<Enchantment, Integer> enchants = new HashMap<>();
        String[] args;
        ItemStack item = new ItemStack(Material.AIR);
        for (String str : strings) {
            args = str.split(":");
            if (Material.matchMaterial(args[0]) != null && item.getType() == Material.AIR) {
                item.setType(Material.matchMaterial(args[0]));
            }
            if (item.getType() == Material.AIR) {
                Bukkit.getLogger().info("Could not find a valid material for the item '" + args[0] + "' in \"" + serializedItem + "\"");
                return null;
            }
        }
        boolean ispotionmeta = false;
        PotionData potionData;
        for (String str : strings) {
            args = str.split(":", 2);

            if (isNumber(args[0])) item.setAmount(Integer.parseInt(args[0]));
            if (args.length == 1) continue;
            if (args[0].equalsIgnoreCase("meta-type")) {
                if (args[1].equals("Potion")) {
                    ispotionmeta = true;
                }
                continue;
            }
            if (ispotionmeta) {
                if (args[0].equalsIgnoreCase("potion-type")) {
                    PotionMeta meta = (PotionMeta) item.getItemMeta();
                    potionData = new PotionData(PotionType.valueOf(args[1]));
                    meta.setBasePotionData(potionData);
                    item.setItemMeta(meta);
                    continue;
                } else if (args[0].equalsIgnoreCase("extended")) {
                    PotionMeta meta = (PotionMeta) item.getItemMeta();
                    PotionData data = meta.getBasePotionData();

                    meta.setBasePotionData(new PotionData(data.getType(), Boolean.parseBoolean(args[1]), false));
                    item.setItemMeta(meta);
                    continue;
                } else if (args[0].equalsIgnoreCase("upgraded")) {
                    PotionMeta meta = (PotionMeta) item.getItemMeta();
                    PotionData data = meta.getBasePotionData();

                    meta.setBasePotionData(new PotionData(data.getType(), data.isExtended(), Boolean.parseBoolean(args[1])));
                    item.setItemMeta(meta);
                    continue;
                }
            }
            if (args[0].equalsIgnoreCase("item-flags")) {
                try {
                    String[] split = args[1].split(",");
                    ItemMeta meta = item.getItemMeta();
                    for (String s : split) {
                        meta.addItemFlags(ItemFlag.valueOf(s));
                    }
                    item.setItemMeta(meta);
                } catch (Exception ignored) {

                }
                continue;
            }
            if (args[0].equalsIgnoreCase("name")) {
                setName(item, ChatColor.translateAlternateColorCodes('&', args[1]));
                continue;
            }
            if (args[0].equalsIgnoreCase("lore")) {
                setLore(item, ChatColor.translateAlternateColorCodes('&', args[1]));
                continue;
            }
            if (args[0].equalsIgnoreCase("rgb")) {
                setArmorColor(item, args[1]);
                continue;
            }
            if (args[0].equalsIgnoreCase("owner")) {
                setOwner(item, args[1]);
                continue;
            }
            if (Enchantment.getByName(args[0].toUpperCase()) != null) {
                enchants.put(Enchantment.getByName(args[0].toUpperCase()), Integer.parseInt(args[1]));
                continue;
            }
        }
        item.addUnsafeEnchantments(enchants);
        return item.getType().equals(Material.AIR) ? null : item;
    }

    public static String serializeWithCommas(ItemStack[] stacks) {
        List<String> strings = new ArrayList<>();
        for (ItemStack stack : stacks) {
            if (stack == null || stack.getType().equals(Material.AIR)) continue;
            strings.add(serialize(stack));
        }
        return StringUtils.join(strings, ",");
    }

    public static List<ItemStack> deserializeWithCommas(String commafiedString) {
        String[] args = commafiedString.split(",");
        List<ItemStack> items = new ArrayList<>();
        for (String s : args) {
            ItemStack stack = deserialize(s);
            if (stack == null || stack.getType() == Material.AIR) continue;
            items.add(stack);
        }
        return items;
    }

    public static List<String> serializeList(ItemStack[] stacks) {
        List<String> strings = new ArrayList<>();
        for (ItemStack stack : stacks) {
            if (stack == null || stack.getType().equals(Material.AIR)) continue;
            strings.add(serialize(stack));
        }
        return strings;
    }

    public static List<String> serializeList(List<ItemStack> stacks) {
        List<String> strings = new ArrayList<>();
        for (ItemStack stack : stacks) {
            if (stack == null || stack.getType().equals(Material.AIR)) continue;
            strings.add(serialize(stack));
        }
        return strings;
    }

    public static List<ItemStack> deserializeConfig(List<String> strings) {
        List<ItemStack> stacks = new ArrayList<>();
        for (String key : strings) {
            try {
                stacks.add(deserialize(key));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return stacks;
    }

    private static String getOwner(ItemStack item) {
        if (!(item.getItemMeta() instanceof SkullMeta)) return null;
        return ((SkullMeta) item.getItemMeta()).getOwner();
    }

    private static void setOwner(ItemStack item, String owner) {
        try {
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            meta.setOwner(owner);
            item.setItemMeta(meta);
        } catch (Exception exception) {
            return;
        }
    }

    private static String getName(ItemStack item) {
        if (!item.hasItemMeta()) return null;
        if (!item.getItemMeta().hasDisplayName()) return null;
        return item.getItemMeta().getDisplayName().replace(" ", "_").replace(ChatColor.COLOR_CHAR, '&');
    }

    private static void setName(ItemStack item, String name) {
        name = name.replace("_", " ");
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
    }

    private static String getLore(ItemStack item) {
        if (!item.hasItemMeta()) return null;
        if (!item.getItemMeta().hasLore()) return null;
        StringBuilder builder = new StringBuilder();
        List<String> lore = item.getItemMeta().getLore();
        for (int ind = 0; ind < lore.size(); ind++) {
            builder.append((ind > 0 ? "|" : "") + lore.get(ind).replace(" ", "_").replace(ChatColor.COLOR_CHAR, '&'));
        }
        return builder.toString();
    }

    private static void setLore(ItemStack item, String lore) {
        lore = lore.replace("_", " ");
        ItemMeta meta = item.getItemMeta();
        meta.setLore(Arrays.asList(lore.split("\\|")));
        item.setItemMeta(meta);
    }

    private static Color getArmorColor(ItemStack item) {
        if (!(item.getItemMeta() instanceof LeatherArmorMeta)) return null;
        return ((LeatherArmorMeta) item.getItemMeta()).getColor();
    }

    private static void setArmorColor(ItemStack item, String str) {
        try {
            String[] colors = str.split("\\|");
            int red = Integer.parseInt(colors[0]);
            int green = Integer.parseInt(colors[1]);
            int blue = Integer.parseInt(colors[2]);
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            meta.setColor(Color.fromRGB(red, green, blue));
            item.setItemMeta(meta);
        } catch (Exception exception) {
            return;
        }
    }

    public static String toBase64(ItemStack item) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutput = new DataOutputStream(outputStream);

        NBTTagCompound nbtTagCompoundItem = new NBTTagCompound();

        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);

        nmsItem.c(nbtTagCompoundItem);

        try {
            NBTCompressedStreamTools.a(nbtTagCompoundItem, (DataOutput) dataOutput);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return new BigInteger(1, outputStream.toByteArray()).toString(32);
    }

    public static ItemStack fromBase64(String data) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new BigInteger(data, 32).toByteArray());

        NBTTagCompound nbtTagCompoundRoot;
        try {
            nbtTagCompoundRoot = NBTCompressedStreamTools.a((DataInput) new DataInputStream(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        net.minecraft.world.item.ItemStack nmsItem = net.minecraft.world.item.ItemStack.a(nbtTagCompoundRoot); // .createStack(nbtTagCompoundRoot);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

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

    public static List<String> toBase64List(List<ItemStack> stacks) {
        try {
            return stacks.stream().map(ItemStackSerializer::toBase64).collect(Collectors.toList());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static List<ItemStack> fromBase64List(List<String> strings) {
        try {
            return strings.stream().map(ItemStackSerializer::fromBase64).collect(Collectors.toList());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private static boolean isNumber(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException exception) {
            return false;
        }
        return true;
    }
}