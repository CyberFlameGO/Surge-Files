package dev.aurapvp.samurai.lootbags.menu.sub;

import dev.aurapvp.samurai.lootbags.LootBag;
import dev.aurapvp.samurai.util.ItemBuilder;
import dev.aurapvp.samurai.util.RewardItem;
import dev.aurapvp.samurai.util.menu.Button;
import dev.aurapvp.samurai.util.menu.button.BackButton;
import dev.aurapvp.samurai.util.menu.paged.PagedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class RemoveRewardsMenu extends PagedMenu {

    private LootBag lootBag;
    private RewardItem item;

    @Override
    public String getPageTitle(Player player) {
        return "Remove Items";
    }

    @Override
    public List<Button> getPageButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (ItemStack stack : item.getItems()) {
            buttons.add(new ItemButton(item.getItems().indexOf(stack), stack));
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
    public class ItemButton extends Button {

        private int index;
        private ItemStack stack;

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(stack)
                    .addLoreLines(
                            "",
                            "&7Click to remove this item."
                    )
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            item.getItems().remove(index);
            lootBag.save();
        }
    }

}
