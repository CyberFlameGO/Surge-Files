package dev.lbuddyboy.crates.util.menu;

import dev.lbuddyboy.crates.util.ItemUtils;
import dev.lbuddyboy.crates.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@AllArgsConstructor
@Data
public class PagedGUISettings {

    private boolean autoFill;
    private ItemStack autoFillItem;
    private String title;
    private int size;
    private int[] buttonSlots;
    private int previousSlot, nextSlot;

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

        config.set("menu-settings.page.button-slots", StringUtils.join(Arrays.asList(args)));
    }

}
