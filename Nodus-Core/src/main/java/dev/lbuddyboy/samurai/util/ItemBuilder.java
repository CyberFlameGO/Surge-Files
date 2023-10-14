package dev.lbuddyboy.samurai.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class ItemBuilder {
    private ItemStack item;

    public static ItemBuilder of(Material material) {
        return new ItemBuilder(material, 1);
    }

    public static ItemBuilder of(Material material, int amount) {
        return new ItemBuilder(material, amount);
    }

    public static ItemBuilder copyOf(ItemBuilder builder) {
        return new ItemBuilder(builder.build());
    }

    public static ItemBuilder copyOf(ItemStack item) {
        return new ItemBuilder(item);
    }

    private ItemBuilder(Material material, int amount) {
        Preconditions.checkArgument((amount > 0 ? 1 : 0) != 0, "Amount cannot be lower than 0.");
        this.item=new ItemStack(material, amount);
    }

    private ItemBuilder(ItemStack item) {
        this.item=item;
    }

    public ItemBuilder amount(int amount) {
        this.item.setAmount(amount);
        return this;
    }

    public ItemBuilder data(short data) {
        this.item.setDurability(data);
        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        this.item.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder nbtString(String key, String string) {
        NBTItem item = new NBTItem(this.item);

        item.setString(key, string);
        this.item = item.getItem();

        return this;
    }

    public ItemBuilder nbtStringList(String key, List<String> strings) {
        NBTItem item = new NBTItem(this.item);
        StringBuilder builder = new StringBuilder();

        strings.forEach(s -> builder.append(s).append("\n"));

        item.setString(key, builder.toString());
        this.item = item.getItem();

        return this;
    }

    public ItemBuilder delNbtString(String key, String string) {
        NBTItem item = new NBTItem(this.item);

        item.removeKey(key);
        this.item = item.getItem();

        return this;
    }

    public ItemBuilder nbtInt(String key, Integer integer) {
        NBTItem item = new NBTItem(this.item);

        item.setInteger(key, integer);
        this.item = item.getItem();

        return this;
    }

    public ItemBuilder nbtDouble(String key, Double doub) {
        NBTItem item = new NBTItem(this.item);

        item.setDouble(key, doub);
        this.item = item.getItem();

        return this;
    }

    public ItemBuilder nbtBoolean(String key, boolean string) {
        NBTItem item = new NBTItem(this.item);

        item.setBoolean(key, string);
        this.item = item.getItem();

        return this;
    }

    public ItemBuilder unenchant(Enchantment enchantment) {
        this.item.removeEnchantment(enchantment);
        return this;
    }

    public ItemBuilder name(String displayName) {
        ItemMeta meta=this.item.getItemMeta();
        meta.setDisplayName(displayName == null ? null : ChatColor.translateAlternateColorCodes('&', displayName));
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder flags(ItemFlag... flags) {
        ItemMeta meta=this.item.getItemMeta();
        meta.addItemFlags(flags);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder trim(TrimMaterial material, TrimPattern pattern) {
        ItemMeta meta=this.item.getItemMeta();

        if (!(meta instanceof ArmorMeta armorMeta)) return this;

        armorMeta.setTrim(new ArmorTrim(TrimMaterial.AMETHYST, TrimPattern.EYE));

        this.item.setItemMeta(armorMeta);
        return this;
    }

    public ItemBuilder addToLore(String... parts) {
        List lore;
        ItemMeta meta=this.item.getItemMeta();
        if (meta == null) {
            meta=Bukkit.getItemFactory().getItemMeta(this.item.getType());
        }
        if ((lore=meta.getLore()) == null) {
            lore=Lists.newArrayList();
        }
        lore.addAll(Arrays.stream(parts).map(part -> ChatColor.translateAlternateColorCodes('&', part)).collect(Collectors.toList()));
        meta.setLore(lore);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore(Collection<String> l) {
        ArrayList lore=new ArrayList();
        ItemMeta meta=this.item.getItemMeta();
        lore.addAll(l.stream().map(part -> ChatColor.translateAlternateColorCodes('&', part)).collect(Collectors.toList()));
        meta.setLore(lore);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder color(Color color) {
        ItemMeta meta=this.item.getItemMeta();
        if (!(meta instanceof LeatherArmorMeta)) {
            throw new UnsupportedOperationException("Cannot set color of a non-leather armor item.");
        }
        ((LeatherArmorMeta) meta).setColor(color);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder modelData(int data) {
        ItemMeta meta=this.item.getItemMeta();
        meta.setCustomModelData(data);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        ItemMeta meta=this.item.getItemMeta();
        meta.setUnbreakable(unbreakable);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemStack build() {
        return this.item.clone();
    }
}

