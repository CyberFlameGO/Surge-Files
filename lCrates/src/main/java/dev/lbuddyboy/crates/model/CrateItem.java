package dev.lbuddyboy.crates.model;

import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Data
public class CrateItem {

    private final Crate crate;

    private int slot, amount;
    private String id;
    private ItemStack item;
    private double chance;
    private boolean removed = false;
    private List<String> commands;

    public CrateItem(Crate crate, int slot, String id, ItemStack item, double chance) {
        this.crate = crate;
        this.slot = slot <= -1 ? crate.getCrateItems().size() + 1 : slot;
        this.id = id;
        this.item = item;
        this.chance = chance;
        this.amount = item.getAmount();
        this.commands = new ArrayList<>();
    }

}
