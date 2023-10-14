package dev.aurapvp.samurai.lootbags.menu.sub;

import dev.aurapvp.samurai.lootbags.LootBag;
import dev.aurapvp.samurai.util.ItemBuilder;
import dev.aurapvp.samurai.util.RewardItem;
import dev.aurapvp.samurai.util.menu.Button;
import dev.aurapvp.samurai.util.menu.button.BackButton;
import dev.aurapvp.samurai.util.menu.paged.PagedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class RemoveCommandsMenu extends PagedMenu {

    private LootBag lootBag;
    private RewardItem item;

    @Override
    public String getPageTitle(Player player) {
        return "Remove Commands";
    }

    @Override
    public List<Button> getPageButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (String command : item.getCommands()) {
            buttons.add(new CommandButton(item.getCommands().indexOf(command), command));
        }

        return buttons;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        return new ArrayList<Button>(){{
            add(new BackButton(5, new LootBagRewardEditorMenu(lootBag, item)));
        }};
    }

    @Override
    public boolean autoUpdate() {
        return true;
    }

    @AllArgsConstructor
    public class CommandButton extends Button {

        private int index;
        private String command;

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.PAPER)
                    .setName("&6&lCommand #" + index)
                    .setLore(
                            "&fCommand&7: &c" + this.command,
                            "",
                            "&7Click to remove this command."
                    )
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            item.getCommands().remove(index);
            lootBag.save();
        }
    }

}
