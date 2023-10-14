package dev.lbuddyboy.bunkers.util.menu.impl;

import dev.lbuddyboy.bunkers.util.Callable;
import dev.lbuddyboy.bunkers.util.bukkit.CompMaterial;
import dev.lbuddyboy.bunkers.util.bukkit.CompatibleMaterial;
import dev.lbuddyboy.bunkers.util.bukkit.ItemBuilder;
import dev.lbuddyboy.bunkers.util.menu.Button;
import dev.lbuddyboy.bunkers.util.menu.Menu;
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

    @Override
    public void close(Player player) {
        super.close(player);
        denyAction.call();
    }

    @AllArgsConstructor
    public class ConfirmButton extends Button {

        @Override
        public int getSlot() {
            return 4;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(CompMaterial.GREEN_WOOL.toItem())
                    .setName(confirmName)
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) {
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
            return new ItemBuilder(CompatibleMaterial.getMaterial("WOOL"))
                    .setName(denyName)
                    .setDurability(14)
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) {
            denyAction.call();
        }
    }

}
