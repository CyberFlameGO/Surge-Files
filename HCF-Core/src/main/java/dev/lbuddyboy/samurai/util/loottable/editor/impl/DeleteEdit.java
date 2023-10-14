package dev.lbuddyboy.samurai.util.loottable.editor.impl;

import dev.lbuddyboy.samurai.util.loottable.LootTableItem;
import dev.lbuddyboy.samurai.util.loottable.command.LootTableCommand;
import dev.lbuddyboy.samurai.util.loottable.editor.ItemEditor;
import dev.lbuddyboy.samurai.util.loottable.editor.menu.EditLootTableMenu;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DeleteEdit implements ItemEditor {

    @Override
    public Conversation conversation(Player player, List<LootTableItem> items) {
        return null;
    }

    @Override
    public void action(Player player, List<LootTableItem> items) {
        for (LootTableItem item : items) {
            LootTableCommand.deleteitem(player, item.getParent(), item);
        }
        new EditLootTableMenu(items.get(0).getParent()).openMenu(player);
    }

    @Override
    public int slot() {
        return 23;
    }

    @Override
    public ItemStack displayItem() {
        return ItemBuilder.of(Material.BARRIER).name("&c&l<!> &e&l<!> &c&l<!> &4&lDELETE ITEM &7(CLICK) &c&l<!> &e&l<!> &c&l<!>").build();
    }

}
