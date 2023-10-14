package dev.lbuddyboy.pcore.shop;

import dev.lbuddyboy.pcore.util.Config;
import dev.lbuddyboy.pcore.util.ItemUtils;
import dev.lbuddyboy.pcore.util.menu.PagedGUISettings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ShopCategory {

    @Setter private Config file;

    public ShopCategory(Config file) {
        this.file = file;

        load();
    }

    private String name, displayName;
    private ItemStack displayItem;
    private int slot;
    private PagedGUISettings guiSettings;
    private List<ShopItem> items = new ArrayList<>();

    public void load() {
        FileConfiguration config = this.file;
        ConfigurationSection items = config.getConfigurationSection("items");

        this.name = this.file.getFileName();
        this.displayName = config.getString("display-name");
        this.displayItem = ItemUtils.itemStackFromConfigSect("display-item", config);
        this.slot = config.getInt("display-item.slot");
        this.guiSettings = new PagedGUISettings(config);

        for (String key : items.getKeys(false)) {
            this.items.add(new ShopItem(this, key));
        }
    }

    public void save() {
        FileConfiguration config = this.file;

        config.set("display-name", this.displayName);
        this.guiSettings.save(config, "menu-settings");
        ItemUtils.itemStackToConfigSect(this.displayItem, this.slot, "display-item", config);
        this.items.forEach(ShopItem::save);

        this.file.save();
    }

}
