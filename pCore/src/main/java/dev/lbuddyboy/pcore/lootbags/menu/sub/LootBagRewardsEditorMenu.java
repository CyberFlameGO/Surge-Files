package dev.lbuddyboy.pcore.lootbags.menu.sub;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.lootbags.LootBag;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import dev.lbuddyboy.pcore.util.RewardItem;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.button.BackButton;
import dev.lbuddyboy.pcore.util.menu.paged.PagedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class LootBagRewardsEditorMenu extends PagedMenu {

    private LootBag lootBag;

    @Override
    public String getPageTitle(Player player) {
        return "LootBag Rewards Editor";
    }

    @Override
    public List<Button> getPageButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (RewardItem reward : lootBag.getRewards()) {
            buttons.add(new RewardButton(reward));
        }

        return buttons;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        return new ArrayList<Button>() {{
            add(new CreateRewardButton());
            add(new BackButton(4, new LootBagEditChooseMenu(lootBag)));
        }};
    }

    @Override
    public boolean autoUpdate() {
        return true;
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
                            "",
                            "&7Left Click to edit this reward.",
                            "&7Right Click to edit this chance.",
                            CC.MENU_BAR
                    ))
                    .setDurability(item.getDisplayItem().getData())
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            if (event.getClick() == ClickType.RIGHT) {
                player.closeInventory();
                player.setMetadata("lootbag_editor_chance", new FixedMetadataValue(pCore.getInstance(), item));
                player.setMetadata("lootbag_editor", new FixedMetadataValue(pCore.getInstance(), lootBag));
                player.sendMessage(CC.translate("&aType what you would like the new chance of this lootbag to be. If you wish to stop this process, type 'cancel'."));
                return;
            }

            new LootBagRewardEditorMenu(lootBag, item).openMenu(player);
        }
    }

    public class CreateRewardButton extends Button {

        @Override
        public int getSlot() {
            return 6;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.WOOL)
                    .setName("&a&lCREATE NEW EMPTY REWARD &7(CLICK)")
                    .setDurability(5)
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            lootBag.getRewards().add(RewardItem.DEFAULT_ITEM);
            lootBag.save();
        }
    }

}
