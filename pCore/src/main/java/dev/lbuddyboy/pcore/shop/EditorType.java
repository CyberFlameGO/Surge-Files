package dev.lbuddyboy.pcore.shop;

import dev.lbuddyboy.pcore.essential.kit.Kit;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.shop.menu.editor.ItemsEditorMenu;
import dev.lbuddyboy.pcore.util.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

@AllArgsConstructor
@Getter
public enum EditorType {

    ITEMS(20, "", new ItemBuilder(Material.CHEST).setName("&6Inventory Contents &7(Click)").create(), (p, category, value) -> {
        new ItemsEditorMenu(category).openMenu(p);
    }),
    DISPLAY_ITEM(21, "&aType 'confirm' to set the item in your hand as the display material. Type 'cancel' to stop this process.", new ItemBuilder(Material.BEACON).setName("&eChange Display Material &7(Click)").create(), (p, category, value) -> {
        if (!value.equalsIgnoreCase("confirm")) return;
        if (p.getItemInHand() == null) {
            p.sendMessage(CC.translate("&cYou do not have an item in your hand."));
            return;
        }

        category.setDisplayItem(p.getItemInHand());
    }),
    SLOT(22, "&aType the slot in the GUI you want this to be. Type 'cancel' to stop this process.", new ItemBuilder(Material.IRON_INGOT).setName("&aSlot &7(Click)").create(), (p, category, value) -> {
        try {
            category.setSlot(Integer.parseInt(value));
        } catch (Exception ignored) {
            p.sendMessage(CC.translate("&cProcess failed... You need to provide an integer."));
        }
    }),
    CHANGE_DISPLAY(23, "&aType what you would like the new display name of this category to be. Type 'cancel' to stop this process.", new ItemBuilder(Material.SIGN).setName("&dChange Display &7(Click)").create(), (p, category, value) -> category.setDisplayName(value)),
    RENAME(24, "&aType what you would like the new name of this category to be. Type 'cancel' to stop this process.", new ItemBuilder(Material.NAME_TAG).setName("&dChange Name &7(Click)").create(), (p, category, value) -> {
        category.getFile().getFile().renameTo(new File(pCore.getInstance().getShopHandler().getCategoryFolder(), value + ".yml"));
        category.setFile(new Config(pCore.getInstance(), value, pCore.getInstance().getShopHandler().getCategoryFolder()));
        category.setName(value);
    }),
    DELETE(25, "&aType 'confirm' to delete this category. Type 'cancel' to stop this process.", new ItemBuilder(Material.BARRIER).setName("&4&lDELETE &7(Click)").create(), (p, category, value) -> {
        if (category.getFile().getFile().exists()) category.getFile().getFile().delete();
        pCore.getInstance().getKitHandler().getKits().remove(category.getName());
    });

    private final int slot;
    private final String prompt;
    private final ItemStack stack;
    private final Callback.TripleCallback<Player, ShopCategory, String> edit;

}
