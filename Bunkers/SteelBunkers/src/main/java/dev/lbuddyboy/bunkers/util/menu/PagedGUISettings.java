package dev.lbuddyboy.bunkers.util.menu;

import dev.lbuddyboy.bunkers.util.StringUtils;
import dev.lbuddyboy.bunkers.util.bukkit.ItemUtils;
import dev.lbuddyboy.bunkers.util.bukkit.CompMaterial;
import dev.lbuddyboy.bunkers.util.bukkit.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Data
public class PagedGUISettings {

    private boolean autoFill;
    private ItemStack autoFillItem;
    private String title;
    private int size;
    private int[] buttonSlots;
    private int previousSlot, nextSlot;
    private Map<Integer, ItemStack> placeholderItems = new HashMap<>();

    public PagedGUISettings(FileConfiguration config) {
        this.title = config.getString("menu-settings.title");
        this.size = config.getInt("menu-settings.size");
        this.autoFill = config.getBoolean("menu-settings.auto-fill.enabled");
        this.autoFillItem = ItemUtils.itemStackFromConfigSect("menu-settings.auto-fill.display-item", config);
        this.previousSlot = config.getInt("menu-settings.page.previous-slot");
        this.nextSlot = config.getInt("menu-settings.page.next-slot");

        String[] args = config.getString("menu-settings.page.button-slots").split(",");
        int[] buttonSlots = new int[args.length];
        int i = 0;
        for (String s : args) {
            buttonSlots[i++] = Integer.parseInt(s);
        }

        this.buttonSlots = buttonSlots;

        if (config.contains("placeholder-items")) {
            for (String s : config.getStringList("filler-buttons")) {
                String[] parts = s.split(";");
                ItemBuilder builder = new ItemBuilder(CompMaterial.fromString(parts[0]).toItem());
                int slot = Integer.parseInt(parts[1]);
                String name = parts[2];
                boolean glowing = Boolean.parseBoolean(parts[3]);
                boolean hideAll = Boolean.parseBoolean(parts[4]);

                builder.setName(name);
                if (glowing) builder.addEnchant(Enchantment.DURABILITY, 1);
                if (hideAll) builder.addItemFlag(ItemFlag.values());

                placeholderItems.put(slot, builder.create());
            }
        }
    }

    public PagedGUISettings(FileConfiguration config, String key) {
        this.title = config.getString(key + ".title");
        this.size = config.getInt(key + ".size");
        this.autoFill = config.getBoolean(key + ".auto-fill.enabled");
        this.autoFillItem = ItemUtils.itemStackFromConfigSect(key + ".auto-fill.display-item", config);
        this.previousSlot = config.getInt(key + ".page.previous-slot");
        this.nextSlot = config.getInt(key + ".page.next-slot");

        String[] args = config.getString(key + ".page.button-slots").split(",");
        int[] buttonSlots = new int[args.length];
        int i = 0;
        for (String s : args) {
            buttonSlots[i++] = Integer.parseInt(s);
        }

        this.buttonSlots = buttonSlots;

        if (config.contains(key + ".filler-buttons")) {
            for (String s : config.getStringList(key + ".filler-buttons")) {
                String[] parts = s.split(";");
                ItemBuilder builder = new ItemBuilder(CompMaterial.fromString(parts[0]).toItem());
                int slot = Integer.parseInt(parts[1]);
                String name = parts[2];
                boolean glowing = Boolean.parseBoolean(parts[3]);
                boolean hideAll = Boolean.parseBoolean(parts[4]);

                builder.setName(name);
                if (glowing) builder.addEnchant(Enchantment.DURABILITY, 1);
                if (hideAll) builder.addItemFlag(ItemFlag.values());

                placeholderItems.put(slot, builder.create());
            }
        }
    }

    public void save(FileConfiguration config) {
        config.set("menu-settings.title", this.title);
        config.set("menu-settings.size", this.size);
        config.set("menu-settings.auto-fill.enabled", this.autoFill);
        ItemUtils.itemStackToConfigSect(this.autoFillItem, -1, "menu-settings.auto-fill.display-item", config);
        config.set("menu-settings.page.previous-slot", this.previousSlot);
        config.set("menu-settings.page.next-slot", this.nextSlot);

        String[] args = new String[this.buttonSlots.length];
        int i = 0;
        for (int slot : this.buttonSlots) {
            args[i++] = String.valueOf(slot);
        }

        config.set("menu-settings.page.button-slots", StringUtils.join(Arrays.asList(args), ", "));
    }

}
