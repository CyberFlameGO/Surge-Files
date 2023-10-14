package dev.aurapvp.samurai.lootbags.menu.sub;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.lootbags.LootBag;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.ItemBuilder;
import dev.aurapvp.samurai.util.menu.Button;
import dev.aurapvp.samurai.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class LootBagEditChooseMenu extends Menu {

    private LootBag lootBag;

    @Override
    public String getTitle(Player player) {
        return lootBag.getName();
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        buttons.add(new SetDisplayItemButton());
        buttons.add(new RenameButton());
        buttons.add(new RewardsButton());
        buttons.add(new SetDisplayNameButton());

        return buttons;
    }

    public class SetDisplayItemButton extends Button {

        @Override
        public int getSlot() {
            return 14;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(lootBag.getDisplayItem())
                    .addLoreLines(
                            "",
                            "&7Click to set this lootbags display item to your held item."
                    )
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
                player.sendMessage(CC.translate("&cYou do not have an item in your hand."));
                return;
            }

            lootBag.setDisplayItem(player.getItemInHand());
            lootBag.save();
        }
    }

    @Override
    public void close(Player player) {
        super.close(player);
    }

    public class RenameButton extends Button {

        @Override
        public int getSlot() {
            return 21;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.NAME_TAG)
                    .setName("&eRename LootBag")
                    .setLore(
                            "&7Current Name: &f" + lootBag.getName(),
                            "",
                            "&7Click to edit this lootbags name."
                    )
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            player.closeInventory();
            player.setMetadata("lootbag_editor_rename", new FixedMetadataValue(Samurai.getInstance(), lootBag));

            player.sendMessage(CC.translate("&aType what you would like to rename this lootbag. If you wish to stop this process, type 'cancel'."));
        }
    }

    public class SetDisplayNameButton extends Button {

        @Override
        public int getSlot() {
            return 41;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.OAK_SIGN)
                    .setName("&eSet Display Name")
                    .setLore(
                            "&7Current Display: &f" + lootBag.getDisplayName(),
                            "",
                            "&7Click to edit this lootbags name."
                    )
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            player.closeInventory();
            player.setMetadata("lootbag_editor_display", new FixedMetadataValue(Samurai.getInstance(), lootBag));

            player.sendMessage(CC.translate("&aType what you would like the new display name of this lootbag to be. If you wish to stop this process, type 'cancel'."));
        }
    }

    public class RewardsButton extends Button {

        @Override
        public int getSlot() {
            return 25;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.BEACON)
                    .setName("&eEdit Rewards")
                    .setLore(
                            "",
                            "&7Click to edit this lootbags rewards."
                    )
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            new LootBagRewardsEditorMenu(lootBag).openMenu(player);
        }
    }

}
