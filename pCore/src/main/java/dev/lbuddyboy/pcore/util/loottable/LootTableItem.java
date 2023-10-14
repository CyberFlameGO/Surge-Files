package dev.lbuddyboy.pcore.util.loottable;

import dev.lbuddyboy.pcore.util.ItemUtils;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Data
public class LootTableItem {

    private final LootTable parent;

    private int slot, amount;
    private String id, displayName;
    private ItemStack item;
    private double chance;
    private boolean removed = false, giveItem = true;
    private List<String> commands;

    public LootTableItem(LootTable parent, int slot, String id, ItemStack item, String displayName, double chance, List<String> commands, boolean giveItem) {
        this.parent = parent;
        this.slot = slot <= -1 ? parent.getItems().size() + 1 : slot;
        this.id = id;
        this.item = item;
        this.displayName = displayName == null ? ItemUtils.getName(this.item) : displayName;
        this.chance = chance;
        this.amount = item.getAmount();
        this.commands = commands;
        this.giveItem = giveItem;
    }

    public ItemStack getItem() {
        this.item.setAmount(amount);
        return this.item;
    }

}
