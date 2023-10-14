package dev.lbuddyboy.samurai.util.loottable.editor.impl;

import dev.lbuddyboy.samurai.util.loottable.LootTableItem;
import dev.lbuddyboy.samurai.util.loottable.command.LootTableCommand;
import dev.lbuddyboy.samurai.util.loottable.editor.ItemEditor;
import dev.lbuddyboy.samurai.util.ItemBuilder;
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
        return ItemBuilder.of(Material.GREEN_WOOL).name("&9Toggle Give Item &7(Click)").build();
    }

    public ItemStack getToggledOnItem() {
        return ItemBuilder.of(Material.GREEN_WOOL).name("&aGive Item On &7(Click to toggle on/off)").build();
    }

    public ItemStack getToggledOffItem() {
        return ItemBuilder.of(Material.RED_WOOL).name("&cGive Item Off &7(Click to toggle on/off)").build();
    }

}
