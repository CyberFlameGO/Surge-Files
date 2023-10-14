package dev.lbuddyboy.pcore.shop;

import dev.lbuddyboy.pcore.pCore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.material.MaterialData;

import java.util.List;

@AllArgsConstructor
@Builder
@Data
public class ShopItem implements Cloneable {

    private ShopCategory category;
    private String name;

    public ShopItem(ShopCategory category, String name) {
        this.category = category;
        this.name = name;

        load();
    }

    private String displayName;
    private List<String> description;
    private MaterialData data;
    private double buyPrice, sellPrice;

    public double getPrice(int amount, boolean sell) {
        return sell ? amount * sellPrice : amount * buyPrice;
    }

    public void save() {
        FileConfiguration config = this.category.getFile();

        config.set("items." + this.name + ".display-name", this.displayName);
        config.set("items." + this.name + ".description", this.description);
        config.set("items." + this.name + ".material", this.data.getItemType().name());
        config.set("items." + this.name + ".data", this.data.getData());
        config.set("items." + this.name + ".buy-price", this.buyPrice);
        config.set("items." + this.name + ".sell-price", this.sellPrice);
    }

    public void load() {
        FileConfiguration config = this.category.getFile();

        this.displayName = config.getString("items." + this.name + ".display-name");
        this.description = config.getStringList("items." + this.name + ".description");
        this.data = new MaterialData(Material.getMaterial(config.getString("items." + this.name + ".material")),
                (byte) config.getInt("items." + this.name + ".data"));
        this.buyPrice = config.getDouble("items." + this.name + ".buy-price");
        this.sellPrice = config.getDouble("items." + this.name + ".sell-price");
    }

    @Override
    public ShopItem clone() {
        try {
            ShopItem clone = (ShopItem) super.clone();

            clone.setName(clone.getName() + "-" + category.getItems().size());

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
