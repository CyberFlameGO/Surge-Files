package dev.lbuddyboy.pcore.enchants;

import de.tr7zw.nbtapi.NBTItem;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.enchants.rarity.Rarity;
import dev.lbuddyboy.pcore.util.*;
import dev.lbuddyboy.pcore.util.Config;
import dev.lbuddyboy.pcore.util.IntRange;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public abstract class CustomEnchant {

    public static ThreadLocalRandom RANDOM;

    static {
        RANDOM = ThreadLocalRandom.current();
    }

    public Config file;

    public CustomEnchant() {
        file = new Config(pCore.getInstance(), getName(), pCore.getInstance().getEnchantHandler().getEnchantDirectory());

        if (getConfig().contains("displayName")) {
            Tasks.run(this::init);
            return;
        }

        getConfig().set("displayName", getDisplayName());
        getConfig().set("description", getDescription());
        getConfig().set("color", getColor());
        getConfig().set("rarity", getRarity().getName());
        getConfig().set("chance", getChance());
        getConfig().set("range", getRange().getMin() + "-" + getRange().getMax());
        getConfig().set("applicable", getApplicable());

        Tasks.run(() -> {
            init();
            this.file.save();
        });
    }

    public void init() {}

    public abstract String getName();
    public abstract List<String> getDescription();
    public abstract String getDisplayName();
    public abstract String getColor();
    public abstract IntRange getRange();
    public abstract Rarity getRarity();
    public abstract double getChance();
    public abstract List<String> getApplicable();

    public void apply(Player player, int level) {
        player.setMetadata(META_DATA(), new FixedMetadataValue(pCore.getInstance(), level));
    }

    public void apply(Player player) {
        player.setMetadata(META_DATA(), new FixedMetadataValue(pCore.getInstance(), true));
    }

    public void unApply(Player player) {
        player.removeMetadata(META_DATA(), pCore.getInstance());
    }

    public boolean hasEnchantApplied(Player player) {
        return player.hasMetadata(META_DATA());
    }

    public boolean hasEnchant(ItemStack stack) {
        return pCore.getInstance().getEnchantHandler().getCustomEnchants(stack).containsKey(this);
    }

    public boolean hasEnchantAppliedInHand(Player player) {
        return hasEnchant(player.getInventory().getItemInHand());
    }

    public String getColoredName() {
        return getColor() + getDisplayName();
    }

    public List<String> validateApplicable(List<String> strings) {
        return strings.stream().filter(pCore.getInstance().getEnchantHandler().getVALID_APPLICABLE()::contains).collect(Collectors.toList());
    }

    public ItemStack getBook(int level, int success, int destroy, boolean preview) {
        YamlConfiguration config = pCore.getInstance().getEnchantHandler().getConfigFile();
        ItemBuilder builder = new ItemBuilder(Material.getMaterial(config.getString("book.material")));

        builder.setName(config.getString("book.name").replaceAll("%display%", getDisplayName()).replaceAll("%color%", getColor()).replaceAll("%level%", preview ? getRange().getMin() + "-" + getRange().getMax() : String.valueOf(level)));

        List<String> lore = new ArrayList<>();

        for (String s : config.getStringList("book.lore")) {
            if (s.contains("%description%"))
                lore.addAll(getDescription());
            else lore.add(s);
        }

        builder.setLore(lore, "%success%", success, "%destroy%", destroy);

        for (String key : config.getStringList("book.enchants")) {
            String[] args = key.split(":");
            builder.addEnchantment(Enchantment.getByName(args[0].toUpperCase()), Integer.parseInt(args[1]));
        }

        for (String key : config.getStringList("book.item-flags")) {
            builder.addItemFlag(ItemFlag.valueOf(key));
        }

        NBTItem item = new NBTItem(builder.create());

        item.setString("custom-enchant", getName());
        item.setInteger("level", level);
        item.setInteger("success", success);
        item.setInteger("destroy", destroy);

        return item.getItem();
    }

    public ItemStack getBook(int level, int success, int destroy) {
        return getBook(level, success, destroy, false);
    }

    public String META_DATA() {
        return "PCORE_ENCHANT_" + getName().toUpperCase();
    }

    public YamlConfiguration getConfig() {
        return this.file;
    }

    public void registerListener(Listener listener) {
        pCore.getInstance().getServer().getPluginManager().registerEvents(listener, pCore.getInstance());
    }
}
