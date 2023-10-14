package dev.lbuddyboy.pcore.shop.menu.editor;

import dev.lbuddyboy.pcore.shop.ShopCategory;
import dev.lbuddyboy.pcore.shop.ShopItem;
import dev.lbuddyboy.pcore.shop.menu.ShopMainMenu;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.button.BackButton;
import dev.lbuddyboy.pcore.util.menu.paged.PagedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class ItemsEditorMenu extends PagedMenu {
    
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
        List<Button> buttons = new ArrayList<>();

        buttons.add(new Button() {
            @Override
            public int getSlot() {
                return 6;
            }

            @Override
            public ItemStack getItem() {
                return new ItemBuilder(Material.WOOL).setDurability(4).setName("&a&lCREATE EMPTY ITEM &7(CLICK)").create();
            }

            @Override
            public void action(InventoryClickEvent event) throws Exception {
                if (!(event.getWhoClicked() instanceof Player)) return;

                category.getItems().add(ShopItem.builder().name("item-" + category.getItems().size())
                                .data(new MaterialData(Material.GRASS))
                                .category(category)
                                .description(CC.translate(Arrays.asList(
                                        "&aBuy Price&7: &f%buy-price%",
                                        "&cSell Price&7: &f%sell-price%"
                                ), "%buy-price%", 10.0, "%sell-price%", 10.0))
                                .sellPrice(10)
                                .buyPrice(10)
                        .displayName("&aItem #" + category.getItems().size()).build());
                category.save();
            }

            @Override
            public boolean clickUpdate() {
                return true;
            }
        });
        buttons.add(new BackButton(4, new CategoryEditorMenu(this.category)));

        return buttons;
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
                    .setLore("&7Click to edit this item")
                    .setName(this.item.getDisplayName())
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            new ShopItemEditorMenu(item).openMenu(player);
        }
    }
    
}
