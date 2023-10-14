package dev.lbuddyboy.gkits.util.menu.button;

import dev.lbuddyboy.gkits.util.menu.paged.PagedMenu;
import dev.lbuddyboy.gkits.util.ItemBuilder;
import dev.lbuddyboy.gkits.util.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class NextPageButton extends Button {

    public PagedMenu menu;
    public int slot;
    public Player player;

    @Override
    public int getSlot() {
        return slot;
    }

    @Override
    public ItemStack getItem() {

        Material material = Material.LEGACY_CARPET;
        int data = menu.page < menu.getMaxPages(player) ? 1 : 0;
        String name = menu.page < menu.getMaxPages(player) ? "&c&lNext Page" : "&c&lNo Next Page";

        return new ItemBuilder(material).setDurability(data).setName(name).create();
    }

    @Override
    public void action(InventoryClickEvent event) {
        if (event.getClick().isLeftClick() && menu.page < menu.getMaxPages(player)) {
            menu.page += 1;
            menu.openMenu((Player) event.getWhoClicked());
        }
    }
}
