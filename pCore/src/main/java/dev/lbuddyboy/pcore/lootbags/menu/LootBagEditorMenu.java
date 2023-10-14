package dev.lbuddyboy.pcore.lootbags.menu;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.lootbags.LootBag;
import dev.lbuddyboy.pcore.lootbags.menu.sub.LootBagEditChooseMenu;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LootBagEditorMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "LootBag Editor";
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        int i = 1;
        for (LootBag lootbag : pCore.getInstance().getLootBagHandler().getLootBags().values()) {
            buttons.add(new LootBagButton(i++, lootbag));
        }

        return buttons;
    }

    @AllArgsConstructor
    public class LootBagButton extends Button {

        private int slot;
        private LootBag lootBag;

        @Override
        public int getSlot() {
            return this.slot;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(lootBag.getDisplayItem())
                    .addLoreLines(
                            " ",
                            "&7Click to edit this lootbag"
                    )
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            new LootBagEditChooseMenu(lootBag).openMenu(player);
        }
    }

}
