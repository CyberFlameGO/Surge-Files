package dev.lbuddyboy.crates.util.menu.impl;

import dev.lbuddyboy.crates.util.Callable;
import dev.lbuddyboy.crates.util.CompMaterial;
import dev.lbuddyboy.crates.util.ItemBuilder;
import dev.lbuddyboy.crates.util.menu.Button;
import dev.lbuddyboy.crates.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ConfirmMenu extends Menu {

    private String title;
    private String confirmName;
    private String denyName;
    private Callable confirmAction, denyAction;

    @Override
    public String getTitle(Player player) {
        return this.title;
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        buttons.add(new ConfirmButton());
        buttons.add(new DenyButton());

        return buttons;
    }

    @AllArgsConstructor
    public class ConfirmButton extends Button {

        @Override
        public int getSlot() {
            return 4;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(CompMaterial.WHITE_WOOL.getMaterial())
                    .setName(confirmName)
                    .setDurability(1)
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            confirmAction.call();
        }
    }

    @AllArgsConstructor
    public class DenyButton extends Button {

        @Override
        public int getSlot() {
            return 6;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(CompMaterial.WHITE_WOOL.getMaterial())
                    .setName(denyName)
                    .setDurability(14)
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            denyAction.call();
        }
    }

}
