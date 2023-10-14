package dev.aurapvp.samurai.util.loottable.editor.impl;

import dev.aurapvp.samurai.util.ItemBuilder;
import dev.aurapvp.samurai.util.loottable.LootTableItem;
import dev.aurapvp.samurai.util.loottable.editor.ItemEditor;
import dev.aurapvp.samurai.util.loottable.editor.menu.sub.CommandListMenu;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CommandListEdit implements ItemEditor {

    @Override
    public Conversation conversation(Player player, List<LootTableItem> items) {
        return null;
    }

    @Override
    public void action(Player player, List<LootTableItem> items) {
        new CommandListMenu(items.get(0)).openMenu(player);
    }

    @Override
    public int slot() {
        return 6;
    }

    @Override
    public ItemStack displayItem() {
        return new ItemBuilder(Material.COMMAND_BLOCK).setName("&eCommand Editor &7(Click)").create();
    }

}
