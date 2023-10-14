package dev.lbuddyboy.crates.menu;

import dev.lbuddyboy.crates.lCrates;
import dev.lbuddyboy.crates.model.Crate;
import dev.lbuddyboy.crates.util.CC;
import dev.lbuddyboy.crates.util.ItemBuilder;
import dev.lbuddyboy.crates.util.ItemUtils;
import dev.lbuddyboy.crates.util.XSound;
import dev.lbuddyboy.crates.util.menu.Button;
import dev.lbuddyboy.crates.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class OpenCrateMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "Inventory Crates";
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (Crate crate : lCrates.getInstance().getCrates().values()) {
            buttons.add(new CrateButton(player, crate.getMenuSettings().getSlot(), crate));
        }

        return buttons;
    }

    @AllArgsConstructor
    public class CrateButton extends Button {

        private Player player;
        private int slot;
        private Crate crate;

        @Override
        public int getSlot() {
            return this.slot;
        }

        @Override
        public ItemStack getItem() {
            List<String> lore = new ArrayList<>(crate.getMenuSettings().getLore());

            return new ItemBuilder(crate.getMenuSettings().getDisplayItem())
                    .setLore(lore,
                            "%keys%", lCrates.getInstance().getApi().getKeys(player.getUniqueId(), crate)
                    )
                    .addItemFlag(ItemFlag.values())
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            ItemStack stack = player.getInventory().getItemInHand();

            if (event.getClick().isLeftClick()) {
                player.closeInventory();
                new CratePreviewMenu(crate, true).openMenu(player);
                return;
            }

            if (lCrates.getInstance().isVirtualKeys()) {
                if (!lCrates.getInstance().getApi().attemptUse(player, crate)) {
                    return;
                }

                int amount = 1;

                if (event.getClick() == ClickType.SHIFT_RIGHT) amount = 10;

                if (lCrates.getInstance().getApi().getKeys(player.getUniqueId(), crate) < amount) {
                    player.sendMessage(CC.translate("&cYou do not have enough keys."));
                    return;
                }

                if (event.getClick() == ClickType.DROP) amount = lCrates.getInstance().getApi().getKeys(player.getUniqueId(), crate);

                for (int i = 0; i < amount; i++) {
                    crate.open(player);
                }

                lCrates.getInstance().getApi().removeKeys(player.getUniqueId(), crate, amount);
                XSound.BLOCK_CHEST_OPEN.play(player, 2.0f, 2.0f);
                update(player);
                return;
            }

            if (stack.getType() == Material.AIR) {
                player.sendMessage(CC.translate("&cYou need a key for this crate."));
                return;
            }

            if (stack.getAmount() < 1) {
                player.sendMessage(CC.translate("&cYou need a key for this crate."));
                return;
            }

            if (!lCrates.getInstance().getApi().attemptUse(player, crate)) {
                return;
            }

            crate.open(player);
            XSound.BLOCK_CHEST_OPEN.play(player, 2.0f, 2.0f);
            player.getInventory().setItemInHand(ItemUtils.takeItem(stack));
        }

    }

}
