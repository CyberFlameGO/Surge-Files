package dev.lbuddyboy.jailcorder.util.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public abstract class Button {

    public abstract int getSlot();
    public abstract ItemStack getItem();
    public void action(InventoryClickEvent event) {

    }
    public boolean clickUpdate() {
        return false;
    }

    public boolean cancels() {
        return true;
    }

    public static dev.lbuddyboy.jailcorder.util.menu.Button fromButton(int slot, dev.lbuddyboy.jailcorder.util.menu.Button old) {
        return new dev.lbuddyboy.jailcorder.util.menu.Button() {
            @Override
            public int getSlot() {
                return slot;
            }

            @Override
            public ItemStack getItem() {
                return old.getItem();
            }

            @Override
            public void action(InventoryClickEvent event) {
                old.action(event);
            }
        };
    }

    public static dev.lbuddyboy.jailcorder.util.menu.Button fromItem(ItemStack stack, int slot) {
        return new dev.lbuddyboy.jailcorder.util.menu.Button() {
            @Override
            public int getSlot() {
                return slot;
            }

            @Override
            public ItemStack getItem() {
                return stack;
            }

        };
    }

}
