package dev.aurapvp.samurai.util.menu.impl;

import dev.aurapvp.samurai.util.ItemBuilder;
import dev.aurapvp.samurai.util.Tasks;
import dev.aurapvp.samurai.util.menu.Button;
import dev.aurapvp.samurai.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
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
    private Tasks.Callable confirmAction, denyAction;

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
            return new ItemBuilder(Material.GREEN_WOOL)
                    .setName(confirmName)
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
            return new ItemBuilder(Material.RED_WOOL)
                    .setName(denyName)
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            denyAction.call();
        }
    }

}
