package dev.lbuddyboy.pcore.robots;

import de.tr7zw.nbtapi.NBTItem;
import dev.lbuddyboy.pcore.util.Config;
import dev.lbuddyboy.pcore.util.ItemUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.Map;
import java.util.UUID;

@Data
public class RobotType {

    private Config config;
    private String name, displayName;
    private ItemStack helmet, chestplate, leggings, boots, heldItem, displayItem;
    private long produceTime, maxLevel, tokensPerProduce;
    private MaterialData blockData;

    public RobotType(Config config) {
        this.config = config;
        this.name = config.getFileName().replaceAll(".yml", "");
        this.displayName = config.getString("display-name");
        this.helmet = ItemUtils.getRobotItem(config, "helmet");
        this.chestplate = ItemUtils.getRobotItem(config, "chestplate");
        this.leggings = ItemUtils.getRobotItem(config, "leggings");
        this.boots = ItemUtils.getRobotItem(config, "boots");
        this.heldItem = ItemUtils.getRobotItem(config, "held-item");
        this.displayItem = ItemUtils.itemStackFromConfigSect("display-item", config);
        this.produceTime = config.getLong("produce-time") * 1000;
        this.maxLevel = config.getInt("max-level");
        this.tokensPerProduce = config.getLong("tokens-per-produce");

        String[] parts = config.getString("block-data").split(":");
        this.blockData = new MaterialData(Material.getMaterial(parts[0].toUpperCase()), Byte.parseByte(parts[1]));
    }

    public NBTItem getItem() {
        NBTItem item = new NBTItem(this.displayItem.clone());

        item.setString("robot-type", this.name);
        item.setUUID("id", UUID.randomUUID());

        return item;
    }

}
