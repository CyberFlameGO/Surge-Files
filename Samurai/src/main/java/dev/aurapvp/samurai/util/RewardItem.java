package dev.aurapvp.samurai.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Data
public class RewardItem {

    private int id;
    private String displayName;
    private MaterialData displayItem;
    private List<ItemStack> items;
    private List<String> commands;
    private double chance;

    public RewardItem(FileConfiguration config, String path, int id) {
        this.id = id;
        this.displayName = config.getString(path + "." + id + ".display-name");
        this.displayItem = new MaterialData(Material.getMaterial(config.getString(path + "." + id + ".display-material")), (byte) config.getInt(path + "." + id + ".display-data"));
        this.items = new ArrayList<>(Arrays.asList(ItemUtils.itemStackArrayFromBase64(config.getString(path + "." + id + ".items"))));
        this.commands = new ArrayList<>(config.getStringList(path + "." + id + ".commands"));
        this.chance = config.getDouble(path + "." + id + ".chance");
    }

    public void save(FileConfiguration config, String path) {
        config.set(path + "." + id + ".display-name", this.displayName);
        config.set(path + "." + id + ".display-material", this.displayItem.getItemType().name());
        config.set(path + "." + id + ".display-data", this.displayItem.getData());
        config.set(path + "." + id + ".items", ItemUtils.itemStackArrayToBase64(items.toArray(new ItemStack[0])));
        config.set(path + "." + id + ".commands", this.commands);
        config.set(path + "." + id + ".chance", this.chance);
    }

    public void reward(Player player) {
        for (ItemStack item : this.items) ItemUtils.tryFit(player, item, false);

        this.commands.forEach(s -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s
                .replaceAll("%player%", player.getName())
                .replaceAll("%reward%", getDisplayName())
                .replaceAll("%chance%", "" + getChance())
        ));
    }

    public static RewardItem DEFAULT_ITEM;

    static {
        DEFAULT_ITEM = new RewardItem(1, "Default Reward", new MaterialData(Material.DIAMOND), new ArrayList<>(Collections.singletonList(new ItemStack(Material.DIAMOND))), new ArrayList<String>() {{
            add("broadcast Default Reward");
        }}, 50);
    }

}
