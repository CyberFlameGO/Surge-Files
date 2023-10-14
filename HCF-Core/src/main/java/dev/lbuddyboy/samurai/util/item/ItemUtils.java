//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dev.lbuddyboy.samurai.util.item;

import dev.lbuddyboy.samurai.util.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.beans.ConstructorProperties;
import java.util.*;
import java.util.Map.Entry;

import static co.aikar.commands.apachecommonslang.ApacheCommonsLangUtil.isDelimiter;

public class ItemUtils {
    private static final Map<String, ItemData> NAME_MAP = new HashMap();

    public ItemUtils() {
    }

    public static void load() {
        NAME_MAP.clear();
        List<String> lines = new ArrayList<>();
        Iterator var1 = lines.iterator();

        while(var1.hasNext()) {
            String line = (String)var1.next();
            String[] parts = line.split(",");
            NAME_MAP.put(parts[0], new ItemData(Material.getMaterial(parts[1]), Short.parseShort(parts[2])));
        }

    }

    public static void setDisplayName(ItemStack itemStack, String name) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);
    }

    public static ItemBuilder builder(Material type) {
        return new ItemBuilder(type);
    }

    public static ItemStack get(String input, int amount) {
        ItemStack item = get(input);
        if (item != null) {
            item.setAmount(amount);
        }

        return item;
    }

    public static ItemStack get(String input) {
        if (NumberUtils.isInteger(input = input.toLowerCase().replace(" ", ""))) {
            return new ItemStack(Material.getMaterial(input));
        } else if (input.contains(":")) {
            if (NumberUtils.isShort(input.split(":")[1])) {
                if (NumberUtils.isInteger(input.split(":")[0])) {
                    return new ItemStack(Material.getMaterial(input.split(":")[0]), 1, Short.parseShort(input.split(":")[1]));
                } else if (!NAME_MAP.containsKey(input.split(":")[0].toLowerCase())) {
                    return null;
                } else {
                    ItemData data = (ItemData)NAME_MAP.get(input.split(":")[0].toLowerCase());
                    return new ItemStack(data.getMaterial(), 1, Short.parseShort(input.split(":")[1]));
                }
            } else {
                return null;
            }
        } else {
            return !NAME_MAP.containsKey(input) ? null : ((ItemData)NAME_MAP.get(input)).toItemStack();
        }
    }

    public static String getName(ItemStack item) {
        return capitalize(item.getType().toString().toLowerCase().replace("_", " "));
    }

    public static String capitalize(String str) {
        return capitalize(str, (char[])null);
    }

    public static String capitalize(String str, char[] delimiters) {
        int delimLen = delimiters == null ? -1 : delimiters.length;
        if (str != null && str.length() != 0 && delimLen != 0) {
            int strLen = str.length();
            StringBuffer buffer = new StringBuffer(strLen);
            boolean capitalizeNext = true;

            for(int i = 0; i < strLen; ++i) {
                char ch = str.charAt(i);
                if (isDelimiter(ch, delimiters)) {
                    buffer.append(ch);
                    capitalizeNext = true;
                } else if (capitalizeNext) {
                    buffer.append(Character.toTitleCase(ch));
                    capitalizeNext = false;
                } else {
                    buffer.append(ch);
                }
            }

            return buffer.toString();
        } else {
            return str;
        }
    }


    public static class ItemData {
        private final Material material;
        private final short data;

        public String getName() {
            return ItemUtils.getName(this.toItemStack());
        }

        public boolean matches(ItemStack item) {
            return item != null && item.getType() == this.material && item.getDurability() == this.data;
        }

        public ItemStack toItemStack() {
            return new ItemStack(this.material, 1, this.data);
        }

        public Material getMaterial() {
            return this.material;
        }

        public short getData() {
            return this.data;
        }

        @ConstructorProperties({"material", "data"})
        public ItemData(Material material, short data) {
            this.material = material;
            this.data = data;
        }
    }

    public static final class ItemBuilder {
        private Material type;
        private int amount;
        private short data;
        private String name;
        private List<String> lore;
        private final Map<Enchantment, Integer> enchantments;

        private ItemBuilder(Material type) {
            this.amount = 1;
            this.data = 0;
            this.lore = new ArrayList();
            this.enchantments = new HashMap();
            this.type = type;
        }

        public ItemBuilder type(Material type) {
            this.type = type;
            return this;
        }

        public ItemBuilder amount(int amount) {
            this.amount = amount;
            return this;
        }

        public ItemBuilder data(short data) {
            this.data = data;
            return this;
        }

        public ItemBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ItemBuilder addLore(String... lore) {
            this.lore.addAll(Arrays.asList(lore));
            return this;
        }

        public ItemBuilder addLore(int index, String lore) {
            this.lore.set(index, lore);
            return this;
        }

        public ItemBuilder setLore(List<String> lore) {
            this.lore = lore;
            return this;
        }

        public ItemBuilder enchant(Enchantment enchantment, int level) {
            this.enchantments.put(enchantment, level);
            return this;
        }

        public ItemBuilder unenchant(Enchantment enchantment) {
            this.enchantments.remove(enchantment);
            return this;
        }

        public ItemStack build() {
            ItemStack item = new ItemStack(this.type, this.amount, this.data);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.name));
            ArrayList<String> finalLore = new ArrayList();

            for(int index = 0; index < this.lore.size(); ++index) {
                if (this.lore.get(index) == null) {
                    finalLore.set(index, "");
                } else {
                    finalLore.set(index, ChatColor.translateAlternateColorCodes('&', (String)this.lore.get(index)));
                }
            }

            meta.setLore(finalLore);
            Iterator var6 = this.enchantments.entrySet().iterator();

            while(var6.hasNext()) {
                Entry<Enchantment, Integer> entry = (Entry)var6.next();
                item.addUnsafeEnchantment((Enchantment)entry.getKey(), (Integer)entry.getValue());
            }

            item.setItemMeta(meta);
            return item;
        }
    }
}
