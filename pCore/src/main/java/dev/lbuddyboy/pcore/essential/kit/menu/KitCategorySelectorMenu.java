package dev.lbuddyboy.pcore.essential.kit.menu;

import dev.lbuddyboy.pcore.essential.kit.Kit;
import dev.lbuddyboy.pcore.essential.kit.KitCategory;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.shop.ShopCategory;
import dev.lbuddyboy.pcore.shop.ShopItem;
import dev.lbuddyboy.pcore.shop.menu.editor.ShopItemEditorMenu;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import dev.lbuddyboy.pcore.util.Tasks;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class KitCategorySelectorMenu extends Menu {

    private Kit kit;

    @Override
    public String getTitle(Player player) {
        return "Select a category...";
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

        for (KitCategory category : pCore.getInstance().getKitHandler().getCategories().values()) {
            buttons.add(new CategoryButton(category));
        }

        return buttons;
    }

    @Override
    public void close(Player player) {
        super.close(player);
        if (player.hasMetadata("opening_menu")) return;

        Tasks.run(() -> new KitEditorMenu(this.kit).openMenu(player));
    }

    @AllArgsConstructor
    public class CategoryButton extends Button {

        private KitCategory category;

        @Override
        public int getSlot() {
            return this.category.getSlot();
        }

        @Override
        public ItemStack getItem() {
            ItemBuilder builder = new ItemBuilder(this.category.getDisplayItem())
                    .setLore("&7Click to move this item to this category!");

            if (kit.getCategory() == category) {
                builder.addEnchant(Enchantment.DURABILITY, 10);
                builder.setLore("&aYou currently have this category selected.");
            }

            return builder.create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;

            kit.setCategory(category);
            kit.save();
        }

        @Override
        public boolean clickUpdate() {
            return true;
        }
    }

}
