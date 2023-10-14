package dev.lbuddyboy.crates.model;

import dev.lbuddyboy.crates.lCrates;
import dev.lbuddyboy.crates.util.Config;
import dev.lbuddyboy.crates.util.ItemBuilder;
import dev.lbuddyboy.crates.util.ItemUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
public class CrateMenuSettings {

    private Crate crate;
    @Getter private int slot;
    @Getter private ItemStack displayItem;
    @Getter private List<String> lore;

    public CrateMenuSettings(Crate crate) {
        this.crate = crate;
        Config config = this.crate.getConfig();

        if (config.contains("menu-settings.slot")) {
            this.slot = config.getInt("menu-settings.slot");
            this.displayItem = ItemUtils.itemStackArrayFromBase64(config.getString("menu-settings.display-item"))[0];
            this.lore = config.getStringList("menu-settings.lore");
        }
    }

    public CrateMenuSettings loadDefault(Crate crate) {
        this.crate = crate;
        this.slot = (int) lCrates.getInstance().getCrates().values().stream().filter(Crate::isInCrateMenu).count();
        this.displayItem = new ItemBuilder(Material.CHEST).setName(crate.getDisplayName() + " Crate").create();
        this.lore = Arrays.asList("&7Left click to preview crate", "&7Right click to open crate");

        return this;
    }

    public void save() {
        Config config = this.crate.getConfig();

        config.set("menu-settings.slot", this.slot);
        config.set("menu-settings.display-item", ItemUtils.itemStackArrayToBase64(Collections.singletonList(this.displayItem).toArray(new ItemStack[0])));
        config.set("menu-settings.lore", this.lore);

        config.save();
    }

    public void setDisplayItem(ItemStack itemInHand) {
        this.displayItem = itemInHand;
    }
}
