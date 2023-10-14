package dev.aurapvp.samurai.util;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ItemBuilder {

    private ItemStack is;

    public ItemBuilder(Material m) {
        this(m, 1);
    }

    public ItemBuilder(ItemStack is) {
        this.is = is.clone();
    }

    public ItemBuilder(ItemStack is, int amount) {
        this.is = is.clone();
        this.is.setAmount(amount);
    }

    public ItemBuilder(Material m, int amount) {
        is = new ItemStack(m, amount);
    }

    public ItemBuilder(Material m, int amount, byte durability) {
        is = new ItemStack(m, amount, durability);
    }

    public ItemBuilder copy() {
        return new ItemBuilder(is);
    }

    public ItemBuilder setDurability(short dur) {
        is.setDurability(dur);
        return this;
    }

    public ItemBuilder setDurability(int dur) {
        is.setDurability((short) dur);
        return this;
    }

    public ItemBuilder setName(String name) {
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(CC.translate(name));
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder setName(String name, Object... objects) {
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(CC.translate(name, objects));
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addUnsafeEnchantment(Enchantment ench, int level) {
        if (level < 1) {
            return this;
        }
        ItemMeta im = is.getItemMeta();
        im.addEnchant(ench, level, true);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder removeEnchantment(Enchantment ench) {
        is.removeEnchantment(ench);
        return this;
    }

    public ItemBuilder setOwner(String owner) {
        try {
            SkullMeta im = (SkullMeta) is.getItemMeta();
            im.setOwner(owner);
            is.setItemMeta(im);
        } catch (Exception ignored) {
        }
        return this;
    }

    public ItemBuilder addEnchant(Enchantment ench, int level) {
        if (level < 1) {
            return this;
        }
        ItemMeta im = is.getItemMeta();
        im.addEnchant(ench, level, true);
        is.setItemMeta(im);
        return this;
    }

/*    public ItemBuilder addEffect(PotionType type, boolean extended, boolean upgraded) {
        PotionMeta im = (PotionMeta) is.getItemMeta();
        im.setBasePotionData(new PotionData(type, extended, upgraded));
        is.setItemMeta(im);
        return this;
    }*/

    public ItemBuilder addEnchantment(Enchantment ench, int level) {
        if (level < 1) {
            return this;
        }
        is.addUnsafeEnchantment(ench, level);
        return this;
    }

    public ItemBuilder addItemFlag(ItemFlag... flags) {
        ItemMeta im = is.getItemMeta();
        im.addItemFlags(flags);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
        is.addUnsafeEnchantments(enchantments);
        return this;
    }

    public ItemBuilder setInfinityDurability() {
        is.setDurability(Short.MAX_VALUE);
        return this;
    }

    public ItemBuilder formatLore(Object... objects) {
        ItemMeta im = is.getItemMeta();
        if (im.hasLore()) {
            im.setLore(CC.translate(im.getLore(), objects));
        }
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        ItemMeta im = is.getItemMeta();
        im.setLore(CC.translate(Arrays.asList(lore)));
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        ItemMeta im = is.getItemMeta();
        im.setLore(CC.translate(lore));
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder setLore(List<String> lore, Object... objects) {
        ItemMeta im = is.getItemMeta();
        im.setLore(CC.translate(lore, objects));
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder removeLoreLine(String line) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        if (!lore.contains(line))
            return this;
        lore.remove(line);
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder removeLoreLine(int index) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        if (index < 0 || index > lore.size())
            return this;
        lore.remove(index);
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addLoreLine(String line) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (im.hasLore())
            lore = new ArrayList<>(im.getLore());
        lore.add(CC.translate(line));
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addLoreLines(String... lines) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (im.hasLore())
            lore = new ArrayList<>(im.getLore());
        for (String line : lines) {
            lore.add(CC.translate(line));
        }
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder addLoreLines(List<String> lines, Object... objects) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (im.hasLore()) lore = new ArrayList<>(im.getLore());

        for (String line : lines) {
            lore.add(CC.translate(line, objects));
        }
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder nbtString(String key, String string) {
        NBTItem item = new NBTItem(this.is);

        item.setString(key, string);
        this.is = item.getItem();

        return this;
    }

    public ItemBuilder nbtStringList(String key, List<String> strings) {
        NBTItem item = new NBTItem(this.is);
        StringBuilder builder = new StringBuilder();

        strings.forEach(s -> builder.append(s).append("\n"));

        item.setString(key, builder.toString());
        this.is = item.getItem();

        return this;
    }

    public ItemBuilder delNbtString(String key, String string) {
        NBTItem item = new NBTItem(this.is);

        item.removeKey(key);
        this.is = item.getItem();

        return this;
    }

    public ItemBuilder nbtInt(String key, Integer integer) {
        NBTItem item = new NBTItem(this.is);

        item.setInteger(key, integer);
        this.is = item.getItem();

        return this;
    }

    public ItemBuilder nbtDouble(String key, Double doub) {
        NBTItem item = new NBTItem(this.is);

        item.setDouble(key, doub);
        this.is = item.getItem();

        return this;
    }

    public ItemBuilder nbtBoolean(String key, boolean string) {
        NBTItem item = new NBTItem(this.is);

        item.setBoolean(key, string);
        this.is = item.getItem();

        return this;
    }

    public ItemBuilder insertLoreLine(String line, int pos) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        lore.set(pos, line);
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder setLeatherArmorColor(Color color) {
        try {
            LeatherArmorMeta im = (LeatherArmorMeta) is.getItemMeta();
            im.setColor(color);
            is.setItemMeta(im);
        } catch (ClassCastException expected) {
        }
        return this;
    }

    public ItemStack create() {
        return is;
    }
}