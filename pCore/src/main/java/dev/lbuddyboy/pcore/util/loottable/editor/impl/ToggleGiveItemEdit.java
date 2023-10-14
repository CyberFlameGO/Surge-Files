package dev.lbuddyboy.pcore.util.loottable.editor.impl;

import dev.lbuddyboy.pcore.util.ItemBuilder;
import dev.lbuddyboy.pcore.util.loottable.LootTableItem;
import dev.lbuddyboy.pcore.util.loottable.command.LootTableCommand;
import dev.lbuddyboy.pcore.util.loottable.editor.ItemEditor;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ToggleGiveItemEdit implements ItemEditor {

    @Override
    public Conversation conversation(Player player, List<LootTableItem> items) {
        return null;
    }

    @Override
    public void action(Player player, List<LootTableItem> items) {
        for (LootTableItem item : items) {
            LootTableCommand.togglegiveitem(player, item.getParent(), item);
        }
    }

    @Override
    public int slot() {
        return 20;
    }

    @Override
    public ItemStack displayItem() {
        return new ItemBuilder(Material.WOOL).setDurability(5).setName("&9Toggle Give Item &7(Click)").create();
    }

    public ItemStack getToggledOnItem() {
        return new ItemBuilder(Material.WOOL).setDurability(5).setName("&9Toggle Give Item &7(Click)").create();
    }

    public ItemStack getToggledOffItem() {
        return new ItemBuilder(Material.WOOL).setDurability(14).setName("&cGive Item Off &7(Click to toggle on/off)").create();
    }

}
