package dev.lbuddyboy.pcore.lootbags.menu.sub;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.lootbags.LootBag;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import dev.lbuddyboy.pcore.util.RewardItem;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.Menu;
import dev.lbuddyboy.pcore.util.menu.button.BackButton;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class LootBagRewardEditorMenu extends Menu {

    private LootBag lootBag;
    private RewardItem item;

    @Override
    public String getTitle(Player player) {
        return "Reward Editor";
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        buttons.add(new AddCommandButton());
        buttons.add(new RemoveCommandButton());

        buttons.add(new ItemButton());

        buttons.add(new AddItemButton());
        buttons.add(new RemoveItemButton());

        buttons.add(new BackButton(1, new LootBagRewardsEditorMenu(lootBag)));

        return buttons;
    }

    @Override
    public boolean autoUpdate() {
        return true;
    }

    @Override
    public int getSize(Player player) {
        return 27;
    }

    public class ItemButton extends Button {

        @Override
        public int getSlot() {
            return 14;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(item.getDisplayItem().getItemType())
                    .setDurability(item.getDisplayItem().getData())
                    .addLoreLines(
                            " ",
                            "&7Click to set the display item to the item in your hand."
                    ).create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
                player.sendMessage(CC.translate("&cYou need to have an item in your hand to do this."));
                return;
            }

            item.setDisplayItem(player.getItemInHand().getData());
            lootBag.save();
        }
    }

    public class AddItemButton extends Button {

        @Override
        public int getSlot() {
            return 17;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.WOOL)
                    .setName("&a&lADD ITEM &7(CLICK) &f[" + item.getItems().size() + "]")
                    .setLore("&c&lWARNING! &7This uses your item in your hand.")
                    .setDurability(5).create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
                player.sendMessage(CC.translate("&cYou need to have an item in your hand to do this."));
                return;
            }

            item.getItems().add(player.getItemInHand());
            lootBag.save();
        }
    }

    public class RemoveItemButton extends Button {

        @Override
        public int getSlot() {
            return 16;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.WOOL)
                    .setName("&c&lREMOVE ITEM &7(CLICK)")
                    .setDurability(14).create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            new RemoveRewardsMenu(lootBag, item).openMenu(player);
        }
    }

    public class AddCommandButton extends Button {

        @Override
        public int getSlot() {
            return 11;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.WOOL)
                    .setName("&a&lADD COMMAND &7(CLICK)")
                    .setDurability(5).create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            player.setMetadata("lootbag_editor_command", new FixedMetadataValue(pCore.getInstance(), item));
            player.setMetadata("lootbag_editor", new FixedMetadataValue(pCore.getInstance(), lootBag));
            player.closeInventory();

            for (String s : new String[]{
                    CC.CHAT_BAR,
                    "&7&oType the command you would like to add to this reward. Type 'cancel' to cancel this process.",
                    "&ePlaceholders:",
                    "&7- %player% - replaces this with the player that opens the lootbag name",
                    CC.CHAT_BAR

            }) {
                player.sendMessage(CC.translate(s));
            }
        }
    }

    public class RemoveCommandButton extends Button {

        @Override
        public int getSlot() {
            return 12;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.WOOL)
                    .setName("&c&lREMOVE COMMAND &7(CLICK)")
                    .setDurability(14).create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            new RemoveCommandsMenu(lootBag, item).openMenu(player);
        }
    }

}
