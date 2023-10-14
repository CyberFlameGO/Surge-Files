package dev.aurapvp.samurai.util.loottable.editor.impl;

import dev.aurapvp.samurai.util.ItemBuilder;
import dev.aurapvp.samurai.util.loottable.LootTableItem;
import dev.aurapvp.samurai.util.loottable.command.LootTableCommand;
import dev.aurapvp.samurai.util.loottable.editor.ItemEditor;
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
        return new ItemBuilder(Material.GREEN_WOOL).setName("&9Toggle Give Item &7(Click)").create();
    }

    public ItemStack getToggledOnItem() {
        return new ItemBuilder(Material.GREEN_WOOL).setName("&aGive Item On &7(Click to toggle on/off)").create();
    }

    public ItemStack getToggledOffItem() {
        return new ItemBuilder(Material.RED_WOOL).setName("&cGive Item Off &7(Click to toggle on/off)").create();
    }

}
