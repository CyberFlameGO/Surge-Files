package dev.lbuddyboy.hub.menu;

import dev.lbuddyboy.hub.lHub;
import dev.lbuddyboy.hub.util.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 22/09/2021 / 1:50 AM
 * LBuddyBoy-Development / me.lbuddyboy.hub.menu
 */

@AllArgsConstructor
public class CustomButton extends Button {

    private int slot;
    private String id;
    private ItemStack displayItem;
    private Map<String, String> actions;

    @Override
    public ItemStack getItem() {
        return this.displayItem;
    }

    @Override
    public int getSlot() {
        return this.slot;
    }

    @Override
    public void action(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();

        for (Map.Entry<String, String> entry : this.actions.entrySet()) {
            lHub.getInstance().getCustomMenuHandler().performAction(entry.getKey(), player, entry.getValue());
        }
    }

    public void save(FileConfiguration config) {
        config.set("buttons." + this.id + ".material", this.displayItem.getType().name());
        config.set("buttons." + this.id + ".name", this.displayItem.getItemMeta().getDisplayName());
        config.set("buttons." + this.id + ".lore", this.displayItem.getItemMeta().getLore());
        config.set("buttons." + this.id + ".amount", this.displayItem.getAmount());
        config.set("buttons." + this.id + ".data", this.displayItem.getDurability());
        config.set("buttons." + this.id + ".slot", this.slot);
        config.set("buttons." + this.id + ".item-flags", Arrays.asList("HIDE_ATTRIBUTES", "HIDE_ENCHANTS"));
        config.set("buttons." + this.id + ".glowing", true);
        config.set("buttons." + this.id + ".actions.ADD_QUEUE", "Dev");
        config.set("buttons." + this.id + ".action.SEND_MESSAGE", "&aYou have joined the Dev queue!");
    }

}
