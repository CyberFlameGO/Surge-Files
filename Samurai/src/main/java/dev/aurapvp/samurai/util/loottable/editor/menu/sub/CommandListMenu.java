package dev.aurapvp.samurai.util.loottable.editor.menu.sub;

import dev.aurapvp.samurai.util.menu.Button;
import dev.aurapvp.samurai.util.menu.Menu;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.ItemBuilder;
import dev.aurapvp.samurai.util.loottable.LootTableItem;
import dev.aurapvp.samurai.util.loottable.editor.menu.EditLootTableItemMenu;
import dev.aurapvp.samurai.util.menu.button.BackButton;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class CommandListMenu extends Menu {

    private LootTableItem item;

    @Override
    public String getTitle(Player player) {
        return "Command List";
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        int i = 10;
        for (String command : item.getCommands()) {
            buttons.add(new CommandButton(i++, command));
        }

        buttons.add(new BackButton(4, new EditLootTableItemMenu(item)));

        return buttons;
    }

    @Override
    public boolean autoUpdate() {
        return true;
    }

    @AllArgsConstructor
    public class CommandButton extends Button {

        private int slot;
        private String command;

        @Override
        public int getSlot() {
            return this.slot;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.PAPER)
                    .setName(CC.GREEN + this.command)
                    .setLore(Arrays.asList(
                            "&7Click to remove this command from the command list."
                    ))
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) {
            item.getCommands().remove(item.getCommands().lastIndexOf(command));
        }

        @Override
        public boolean clickUpdate() {
            return true;
        }
    }

}
