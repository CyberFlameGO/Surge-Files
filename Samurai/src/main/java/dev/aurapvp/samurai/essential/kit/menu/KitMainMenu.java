package dev.aurapvp.samurai.essential.kit.menu;

import dev.aurapvp.samurai.essential.kit.Kit;
import dev.aurapvp.samurai.essential.kit.KitCategory;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.Callback;
import dev.aurapvp.samurai.util.menu.Button;
import dev.aurapvp.samurai.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class KitMainMenu extends Menu {

    private Callback.SingleCallback<Kit> callable;

    @Override
    public String getTitle(Player player) {
        return CC.translate(Samurai.getInstance().getKitHandler().getGuiSettings().getTitle());
    }

    @Override
    public boolean autoFill() {
        return Samurai.getInstance().getKitHandler().getGuiSettings().isAutoFill();
    }

    @Override
    public int getSize(Player player) {
        return Samurai.getInstance().getKitHandler().getGuiSettings().getSize();
    }

    @Override
    public ItemStack autoFillItem() {
        return Samurai.getInstance().getKitHandler().getGuiSettings().getAutoFillItem();
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (KitCategory category : Samurai.getInstance().getKitHandler().getCategories().values()) {
            buttons.add(new CategoryButton(category));
        }

        return buttons;
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
            return this.category.getDisplayItem();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;

            new KitMenu(this.category, callable, KitMainMenu.this).openMenu((Player) event.getWhoClicked());
        }
    }

}
