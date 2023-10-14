package dev.aurapvp.samurai.essential.kit;

import dev.aurapvp.samurai.util.Config;
import dev.aurapvp.samurai.util.ItemUtils;
import dev.aurapvp.samurai.util.menu.GUISettings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

@Data
@AllArgsConstructor
@Builder
public class KitCategory {

    @Setter
    private Config file;
    private String name, displayName;
    private int slot;
    private GUISettings guiSettings;
    private ItemStack displayItem;

    public KitCategory(Config file) {
        this.file = file;

        load();
    }

    public void load() {
        FileConfiguration config = this.file;

        this.name = this.file.getFileName();
        this.displayName = config.getString("display-name");
        this.displayItem = ItemUtils.itemStackFromConfigSect("display-item", config);
        this.slot = config.getInt("display-item.slot");
        this.guiSettings = new GUISettings(config);
    }

    public void save() {
        FileConfiguration config = this.file;

        config.set("display-name", this.displayName);
        this.guiSettings.save(config);
        ItemUtils.itemStackToConfigSect(this.displayItem, this.slot, "display-item", config);

        this.file.save();
    }
}