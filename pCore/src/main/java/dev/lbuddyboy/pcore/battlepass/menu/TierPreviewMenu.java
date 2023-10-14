package dev.lbuddyboy.pcore.battlepass.menu;

import dev.lbuddyboy.pcore.battlepass.BattlePassReward;
import dev.lbuddyboy.pcore.battlepass.Tier;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.button.BackButton;
import dev.lbuddyboy.pcore.util.menu.paged.PagedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class TierPreviewMenu extends PagedMenu {

    private Tier tier;
    private boolean free;

    @Override
    public String getPageTitle(Player player) {
        return "Tier " + tier.getNumber();
    }

    @Override
    public List<Button> getPageButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        if (free) {
            for (BattlePassReward reward : tier.getFreeRewards()) {
                buttons.add(Button.fromItem(reward.getDisplayItem(), 0));
            }
        } else {
            for (BattlePassReward reward : tier.getBoughtRewards()) {
                buttons.add(Button.fromItem(reward.getDisplayItem(), 0));
            }
        }

        return buttons;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        ItemStack display = free ? new ItemBuilder(Material.LEATHER)
                .setName("&6&lFree Rewards")
                .create() : new ItemBuilder(Material.DIAMOND)
                .setName("&6&lPremium Rewards")
                .create();

        buttons.add(new Button() {
            @Override
            public int getSlot() {
                return 5;
            }

            @Override
            public ItemStack getItem() {
                return new ItemBuilder(free ? Material.LEATHER : Material.DIAMOND)
                        .setName(free ? "&eViewing&7: &fFree Rewards" : "&eViewing&7: &fPremium Rewards")
                        .addLoreLine("&7Click to view the " + (free ? "premium" : "free") + " rewards.")
                        .create();
            }

            @Override
            public void action(InventoryClickEvent event) throws Exception {
                free = !free;
            }

            @Override
            public boolean clickUpdate() {
                return true;
            }
        });

        buttons.add(new BackButton(32, new RewardsMenu()));

        buttons.add(Button.fromItem(display, 10));
        buttons.add(Button.fromItem(display, 18));
        buttons.add(Button.fromItem(display, 19));
        buttons.add(Button.fromItem(display, 27));

        for (int i = 1; i <= 9; i++) {
            if (i == 5) continue;
            buttons.add(Button.fromItem(new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).create(), i));
        }

        buttons.add(Button.fromItem(new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).create(), 29));
        buttons.add(Button.fromItem(new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).create(), 30));
        buttons.add(Button.fromItem(new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).create(), 31));

        buttons.add(Button.fromItem(new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).create(), 33));
        buttons.add(Button.fromItem(new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).create(), 34));
        buttons.add(Button.fromItem(new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).create(), 35));

        return buttons;
    }

    @Override
    public int[] getButtonSlots() {
        return new int[]{
                11, 12, 13, 14, 15, 16, 17,
                20, 21, 22, 23, 24, 25, 26
        };
    }

    @Override
    public int getPreviousButtonSlot() {
        return 28;
    }

    @Override
    public int getNextPageButtonSlot() {
        return 36;
    }

    @Override
    public int getSize(Player player) {
        return 36;
    }

}
