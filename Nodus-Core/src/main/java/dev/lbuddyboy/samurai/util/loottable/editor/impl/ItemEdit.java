package dev.lbuddyboy.samurai.util.loottable.editor.impl;

import dev.lbuddyboy.samurai.util.loottable.LootTableItem;
import dev.lbuddyboy.samurai.util.loottable.command.LootTableCommand;
import dev.lbuddyboy.samurai.util.loottable.editor.ItemEditor;
import dev.lbuddyboy.samurai.util.loottable.editor.menu.EditLootTableItemMenu;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.ConversationBuilder;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemEdit implements ItemEditor {

    @Override
    public Conversation conversation(Player player, List<LootTableItem> items) {
        return new ConversationBuilder(player)
                .closeableStringPrompt(CC.translate("&aType 'yes' or 'confirm' to set the new item, you can also type 'cancel' to stop this process.\n&aPlaceholders:\n&e%player% - returns the player"), (ctx, response) -> {
                    if (response.equalsIgnoreCase("yes") || response.equalsIgnoreCase("confirm")) {
                        for (LootTableItem item : items) {
                            LootTableCommand.setitem(player, item.getParent(), item);
                        }
                    }
                    new EditLootTableItemMenu(items).openMenu(player);
                    return Prompt.END_OF_CONVERSATION;
                })
                .echo(false)
                .build();
    }

    @Override
    public void action(Player player, List<LootTableItem> items) {

    }

    @Override
    public int slot() {
        return 5;
    }

    @Override
    public ItemStack displayItem() {
        return ItemBuilder.of(Material.BEACON).name("&bChange Display Item &7(Click)").build();
    }

}
