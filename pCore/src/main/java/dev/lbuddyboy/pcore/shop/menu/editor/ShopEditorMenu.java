package dev.lbuddyboy.pcore.shop.menu.editor;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.shop.ShopCategory;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ShopEditorMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.translate(pCore.getInstance().getShopHandler().getGuiSettings().getTitle());
    }

    @Override
    public boolean autoFill() {
        return pCore.getInstance().getShopHandler().getGuiSettings().isAutoFill();
    }

    @Override
    public int getSize(Player player) {
        return pCore.getInstance().getShopHandler().getGuiSettings().getSize();
    }

    @Override
    public ItemStack autoFillItem() {
        return pCore.getInstance().getShopHandler().getGuiSettings().getAutoFillItem();
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (ShopCategory category : pCore.getInstance().getShopHandler().getCategories()) {
            buttons.add(new CategoryButton(category));
        }

        return buttons;
    }

    @AllArgsConstructor
    public class CategoryButton extends Button {

        private ShopCategory category;

        @Override
        public int getSlot() {
            return this.category.getSlot();
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(this.category.getDisplayItem())
                    .setLore("&7Click to edit this category!")
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;

            new CategoryEditorMenu(this.category).openMenu((Player) event.getWhoClicked());
        }
    }

}
