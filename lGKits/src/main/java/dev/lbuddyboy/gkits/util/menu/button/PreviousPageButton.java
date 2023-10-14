package dev.lbuddyboy.gkits.util.menu.button;

import dev.lbuddyboy.gkits.util.ItemBuilder;
import dev.lbuddyboy.gkits.util.menu.Button;
import dev.lbuddyboy.gkits.util.menu.paged.PagedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class PreviousPageButton extends Button {

    public PagedMenu menu;
    public int slot;

    @Override
    public int getSlot() {
        return slot;
    }

    @Override
    public ItemStack getItem() {

        Material material = Material.LEGACY_CARPET;
        int data = menu.page > 1 ? 1 : 0;
        String name = menu.page > 1 ? "&c&lPrevious Page" : "&c&lNo Previous Page";

        return new ItemBuilder(material).setDurability(data).setName(name).create();
    }

    @Override
    public void action(InventoryClickEvent event) {
        if (event.getClick().isLeftClick() && menu.page > 1) {
            menu.page -= 1;
            menu.openMenu((Player) event.getWhoClicked());
        }
    }
}
