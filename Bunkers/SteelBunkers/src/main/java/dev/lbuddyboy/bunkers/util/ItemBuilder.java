//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dev.lbuddyboy.bunkers.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class ItemBuilder {
    public static final ItemFlag[] VALUES = ItemFlag.values();
    private final ItemStack item;

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
        Preconditions.checkArgument(amount > 0, "Amount cannot be lower than 0.");
        this.item = new ItemStack(material, amount);
    }

    private ItemBuilder(ItemStack item) {
        this.item = item;
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

    public ItemBuilder unenchant(Enchantment enchantment) {
        this.item.removeEnchantment(enchantment);
        return this;
    }

    public ItemBuilder name(String displayName) {
        ItemMeta meta = this.item.getItemMeta();
        meta.setDisplayName(displayName == null ? null : ChatColor.translateAlternateColorCodes('&', displayName));
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addToLore(String... parts) {
        ItemMeta meta = this.item.getItemMeta();
        if (meta == null) {
            meta = Bukkit.getItemFactory().getItemMeta(this.item.getType());
        }

        Object lore;
        if ((lore = meta.getLore()) == null) {
            lore = Lists.newArrayList();
        }

        ((List)lore).addAll((Collection)Arrays.stream(parts).map((part) -> {
            return ChatColor.translateAlternateColorCodes('&', part);
        }).collect(Collectors.toList()));
        meta.setLore((List)lore);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore(Collection<String> l) {
        ArrayList lore = new ArrayList();
        ItemMeta meta = this.item.getItemMeta();
        lore.addAll((Collection)l.stream().map((part) -> {
            return ChatColor.translateAlternateColorCodes('&', part);
        }).collect(Collectors.toList()));
        meta.setLore(lore);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder color(Color color) {
        ItemMeta meta = this.item.getItemMeta();
        if (!(meta instanceof LeatherArmorMeta)) {
            throw new UnsupportedOperationException("Cannot set color of a non-leather armor item.");
        } else {
            ((LeatherArmorMeta)meta).setColor(color);
            this.item.setItemMeta(meta);
            return this;
        }
    }

    public ItemBuilder modelData(int data) {
        ItemMeta meta = this.item.getItemMeta();
        meta.setCustomModelData(data);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        ItemMeta meta = this.item.getItemMeta();
        meta.setUnbreakable(unbreakable);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder potion(PotionType type, boolean extended, boolean upgraded) {
        PotionMeta meta = (PotionMeta) this.item.getItemMeta();
        PotionData potionData = new PotionData(type, extended, upgraded);
        meta.setBasePotionData(potionData);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder hideAttributes() {
        ItemMeta meta = this.item.getItemMeta();

        if (meta != null) {
            meta.addItemFlags(VALUES);
        }

        this.item.setItemMeta(meta);

        return this;
    }

    public ItemStack build() {
        return this.item.clone();
    }
}
