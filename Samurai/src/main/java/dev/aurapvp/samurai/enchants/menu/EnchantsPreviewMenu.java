package dev.aurapvp.samurai.enchants.menu;

import dev.aurapvp.samurai.enchants.CustomEnchant;
import dev.aurapvp.samurai.Samurai;
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
public class EnchantsPreviewMenu extends PagedMenu {

    private String gear;

    @Override
    public String getPageTitle(Player player) {
        return "Custom Enchants (" + gear + ")";
    }

    @Override
    public List<Button> getPageButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (CustomEnchant enchant : Samurai.getInstance().getEnchantHandler().getEnchants()) {
            if (!enchant.getApplicable().contains(gear)) continue;

            buttons.add(new EnchantButton(enchant));
        }

        return buttons;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        buttons.add(new BackButton(5, new EnchantsMenu()));

        return buttons;
    }

    @AllArgsConstructor
    private class EnchantButton extends Button {

        private CustomEnchant enchant;

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public ItemStack getItem() {
            return enchant.getBook(100, 100, 0, true);
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            if (!player.isOp()) return;

            player.getInventory().addItem(enchant.getBook(enchant.getRange().getMax(), 100, 0, false));
        }
    }

}
