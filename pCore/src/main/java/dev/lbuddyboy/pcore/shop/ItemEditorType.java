package dev.lbuddyboy.pcore.shop;

import dev.lbuddyboy.pcore.command.menu.LoreEditorMenu;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.shop.menu.editor.CategorySelectorMenu;
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
public enum ItemEditorType {

    DESCRIPTION(20, "&aType the description people will see for this. Use | to make multiple lines. Type 'cancel' to stop this process.", new ItemBuilder(Material.BOOK).setName("&aDescription &7(Click)").setLore("&c&lRECOMMENDATION", "&cEdit in the config.yml", "&cthen /kit reload!").create(), (p, kit, value) -> {

    }),
    DISPLAY_ITEM(21, "&aType 'confirm' to set the item in your hand as the item. Type 'cancel' to stop this process.", new ItemBuilder(Material.BEACON).setName("&eChange Display Material &7(Click)").create(), (p, item, value) -> {
        if (!value.equalsIgnoreCase("confirm")) return;
        if (p.getItemInHand() == null) {
            p.sendMessage(CC.translate("&cYou do not have an item in your hand."));
            return;
        }

        item.setData(p.getItemInHand().getData());
    }),
    BUY(22, "&aType the buy price you would like this to be. Type 'cancel' to stop this process.", new ItemBuilder(Material.EMERALD).setName("&aBuy Price &7(Click)").create(), (p, item, value) -> {
        try {
            item.setBuyPrice(Double.parseDouble(value));
        } catch (Exception ignored) {
            p.sendMessage(CC.translate("&cProcess failed... You need to provide a decimal."));
        }
    }),
    SELL(23, "&aType the sell price you would like this to be. Type 'cancel' to stop this process.", new ItemBuilder(Material.REDSTONE).setName("&cSell Price &7(Click)").create(), (p, item, value) -> {
        try {
            item.setSellPrice(Double.parseDouble(value));
        } catch (Exception ignored) {
            p.sendMessage(CC.translate("&cProcess failed... You need to provide a decimal."));
        }
    }),
    CHANGE_DISPLAY(24, "&aType what you would like the new display name of this category to be. Type 'cancel' to stop this process.", new ItemBuilder(Material.SIGN).setName("&dChange Display &7(Click)").create(), (p, item, value) -> item.setDisplayName(value)),
    RENAME(25, "&aType what you would like the new name of this item to be. Type 'cancel' to stop this process.", new ItemBuilder(Material.NAME_TAG).setName("&dChange Name &7(Click)").create(), (p, item, value) -> {
        item.setName(value);
    }),
    CATEGORY(26, "", new ItemBuilder(Material.PAINTING).setName("&9&lCHANGE CATEGORY &7(Click)").create(), (p, item, value) -> {
        new CategorySelectorMenu(item).openMenu(p);
    }),
    CLONE(29, "", new ItemBuilder(Material.NETHER_STAR).setName("&b&lCLONE ITEM &7(Click)").create(), (p, item, value) -> {
        ShopItem cloned = item.clone();
        item.getCategory().getItems().add(cloned);
        cloned.save();
    }),
    DELETE(30, "&aType 'confirm' to delete this item. Type 'cancel' to stop this process.", new ItemBuilder(Material.BARRIER).setName("&4&lDELETE &7(Click)").create(), (p, item, value) -> {
        ShopCategory category = item.getCategory();

        category.getItems().remove(item);
        category.save();
        Tasks.run(() -> new ItemsEditorMenu(category).openMenu(p));
    });

    private final int slot;
    private final String prompt;
    private final ItemStack stack;
    private final Callback.TripleCallback<Player, ShopItem, String> edit;

}
