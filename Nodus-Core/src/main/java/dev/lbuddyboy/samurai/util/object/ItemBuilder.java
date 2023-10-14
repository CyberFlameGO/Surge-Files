package dev.lbuddyboy.samurai.util.object;

import com.google.common.base.Preconditions;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemBuilder {
    private ItemStack stack;
    private ItemMeta meta;

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(Material material, final int amount) {
        this(material, amount, (byte) 0);
    }

    public ItemBuilder(ItemStack stack) {
        Preconditions.checkNotNull(stack, "ItemStack cannot be null");
        this.stack = stack;
    }

    public ItemBuilder(ItemStack stack, ItemMeta meta) {
        Preconditions.checkNotNull(stack, "ItemStack cannot be null");
        this.stack = stack;
        this.meta = meta;
    }

    public ItemBuilder(Material material, int amount, byte data) {
        Preconditions.checkNotNull((Object) material, "Material cannot be null");
        Preconditions.checkArgument(amount > 0, "Amount must be positive");
        this.stack = new ItemStack(material, amount, (short) data);
    }

    public ItemBuilder displayName(final String name) {
        if (this.meta == null) {
            this.meta = this.stack.getItemMeta();
        }
        this.meta.setDisplayName(CC.translate(name));
        return this;
    }

    public ItemBuilder loreLine(final String line) {
        if (this.meta == null) {
            this.meta = this.stack.getItemMeta();
        }
        final boolean hasLore = this.meta.hasLore();
        final List<String> lore = hasLore ? this.meta.getLore() : new ArrayList<>();
        lore.add(hasLore ? lore.size() : 0, line);
        this.lore(line);
        return this;
    }

    public ItemBuilder lore(final String... lore) {
        if (this.meta == null) {
            this.meta = this.stack.getItemMeta();
        }
        this.meta.setLore(CC.translate(Arrays.asList(lore)));
        return this;
    }

    public ItemBuilder lore(final List<String> lore) {
        if (this.meta == null) {
            this.meta = this.stack.getItemMeta();
        }
        this.meta.setLore(lore);
        return this;
    }
    public ItemBuilder enchant(final Enchantment enchantment, final int level, boolean bool) {
        if (this.meta == null) {
            this.meta = this.stack.getItemMeta();
        }
        this.meta.addEnchant(enchantment, level, true);
        return this;
    }
    public ItemBuilder enchant(final Enchantment enchantment, final int level) {
        if (this.meta == null) {
            this.meta = this.stack.getItemMeta();
        }
        this.meta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder data(final short data) {
        this.stack.setDurability(data);
        return this;
    }

    public ItemStack build() {
        if (this.meta != null) {
            this.stack.setItemMeta(this.meta);
        }
        return this.stack;
    }

    public ItemStack getStack() {
        return stack;
    }

    public ItemBuilder setStack(ItemStack stack) {
        this.stack = stack;
        return this;
    }

    public ItemBuilder hideAttributes() {
        if (this.meta == null) {
            this.meta = this.stack.getItemMeta();
        }
        this.meta.addItemFlags(ItemFlag.values());
        return this;
    }

    public ItemBuilder modelData(int data) {
        if (this.meta == null) {
            this.meta = this.stack.getItemMeta();
        }
        this.meta.setCustomModelData(data);
        return this;
    }

    public ItemBuilder skullOwner(String owner) {
        if (this.meta == null) {
            this.meta = this.stack.getItemMeta();
        }
        SkullMeta skullMeta = (SkullMeta) this.meta;
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
        return this;
    }

    public ItemMeta getMeta() {
        return meta;
    }

    public ItemBuilder setMeta(ItemMeta meta) {
        this.meta = meta;
        return this;
    }

    @Override
    public ItemBuilder clone(){
        return new ItemBuilder(stack.clone(), meta.clone());
    }
}
