package dev.lbuddyboy.samurai.util.loottable.editor.impl;

import dev.lbuddyboy.samurai.util.loottable.LootTableItem;
import dev.lbuddyboy.samurai.util.loottable.editor.ItemEditor;
import dev.lbuddyboy.samurai.util.loottable.editor.menu.sub.CommandListMenu;
import dev.lbuddyboy.samurai.util.ItemBuilder;
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
        return ItemBuilder.of(Material.COMMAND_BLOCK).name("&eCommand Editor &7(Click)").build();
    }

}
