package dev.lbuddyboy.jailcorder.util.menu.button;

import dev.lbuddyboy.jailcorder.util.CompatibleMaterial;
import dev.lbuddyboy.jailcorder.util.ItemBuilder;
import dev.lbuddyboy.jailcorder.util.menu.Button;
import dev.lbuddyboy.jailcorder.util.menu.paged.PagedMenu;
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

        Material material = CompatibleMaterial.getMaterial("CARPET");
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
