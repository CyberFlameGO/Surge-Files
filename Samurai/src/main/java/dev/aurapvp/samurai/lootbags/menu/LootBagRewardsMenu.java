package dev.aurapvp.samurai.lootbags.menu;

import dev.aurapvp.samurai.lootbags.LootBag;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.ItemBuilder;
import dev.aurapvp.samurai.util.RewardItem;
import dev.aurapvp.samurai.util.menu.Button;
import dev.aurapvp.samurai.util.menu.paged.PagedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class LootBagRewardsMenu extends PagedMenu {

    private LootBag lootBag;

    @Override
    public String getPageTitle(Player player) {
        return "LootBag Rewards";
    }

    @Override
    public List<Button> getPageButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (RewardItem reward : lootBag.getRewards()) {
            buttons.add(new RewardButton(reward));
        }

        return buttons;
    }

    @AllArgsConstructor
    public class RewardButton extends Button {

        private RewardItem item;

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(item.getDisplayItem().getItemType())
                    .setName(item.getDisplayName())
                    .setLore(Arrays.asList(
                            CC.MENU_BAR,
                            "&eChance&7: &f" + item.getChance(),
                            CC.MENU_BAR
                    ))
                    .setDurability(item.getDisplayItem().getData())
                    .create();
        }
    }

}
