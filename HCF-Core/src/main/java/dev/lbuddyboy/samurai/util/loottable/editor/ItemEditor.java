package dev.lbuddyboy.samurai.util.loottable.editor;

import dev.lbuddyboy.samurai.util.loottable.LootTableItem;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface ItemEditor {

    Conversation conversation(Player player, List<LootTableItem> item);
    void action(Player player, List<LootTableItem> item);
    int slot();
    ItemStack displayItem();

}
