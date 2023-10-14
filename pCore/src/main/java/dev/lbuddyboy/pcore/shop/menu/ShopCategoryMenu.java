package dev.lbuddyboy.pcore.shop.menu;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.shop.ShopCategory;
import dev.lbuddyboy.pcore.shop.ShopItem;
import dev.lbuddyboy.pcore.shop.menu.purchase.PurchaseMenu;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.button.BackButton;
import dev.lbuddyboy.pcore.util.menu.paged.PagedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class ShopCategoryMenu extends PagedMenu {
    
    private ShopCategory category;
    
    @Override
    public String getPageTitle(Player player) {
        return CC.translate(this.category.getGuiSettings().getTitle());
    }

    @Override
    public boolean autoFill() {
        return this.category.getGuiSettings().isAutoFill();
    }

    @Override
    public int getSize(Player player) {
        return this.category.getGuiSettings().getSize();
    }

    @Override
    public ItemStack autoFillItem() {
        return this.category.getGuiSettings().getAutoFillItem();
    }

    @Override
    public int getPreviousButtonSlot() {
        return this.category.getGuiSettings().getPreviousSlot();
    }

    @Override
    public int getNextPageButtonSlot() {
        return this.category.getGuiSettings().getNextSlot();
    }

    @Override
    public int[] getButtonSlots() {
        return this.category.getGuiSettings().getButtonSlots();
    }

    @Override
    public List<Button> getPageButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        int i = 1;
        for (ShopItem item : this.category.getItems()) {
            buttons.add(new ShopItemButton(i, item));
        }
        
        return buttons;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        return Collections.singletonList(new BackButton(5, new ShopMainMenu()));
    }

    @AllArgsConstructor
    public class ShopItemButton extends Button {

        private int slot;
        private ShopItem item;

        @Override
        public int getSlot() {
            return this.slot;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(this.item.getData().toItemStack(), 1)
                    .setLore(this.item.getDescription(),
                            "%buy-price%", pCore.getInstance().getEconomyHandler().getEconomy().formatMoney(this.item.getBuyPrice()),
                            "%sell-price%", pCore.getInstance().getEconomyHandler().getEconomy().formatMoney(this.item.getSellPrice())
                    )
                    .setName(this.item.getDisplayName())
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();
            if (item.getSellPrice() <= 0 && event.getClick() == ClickType.RIGHT) {
                return;
            }
            if (item.getBuyPrice() <= 0 && event.getClick() != ClickType.RIGHT) {
                return;
            }

            new PurchaseMenu(item, event.getClick() == ClickType.RIGHT).openMenu(player);
        }
    }
    
}
