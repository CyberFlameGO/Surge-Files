package dev.aurapvp.samurai.util.loottable.editor.impl;

import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.ConversationBuilder;
import dev.aurapvp.samurai.util.ItemBuilder;
import dev.aurapvp.samurai.util.loottable.LootTable;
import dev.aurapvp.samurai.util.loottable.LootTableItem;
import dev.aurapvp.samurai.util.loottable.command.LootTableCommand;
import dev.aurapvp.samurai.util.loottable.editor.ItemEditor;
import dev.aurapvp.samurai.util.loottable.editor.menu.EditLootTableItemMenu;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CommandAddEdit implements ItemEditor {

    @Override
    public Conversation conversation(Player player, List<LootTableItem> items) {
        return new ConversationBuilder(player)
                .closeableStringPrompt(CC.translate("&aType a command to add to the loottable, you can also type 'cancel' to stop this process.\n&aPlaceholders:\n&e%player% - returns the player"), (ctx, response) -> {
                    for (LootTableItem item : items) {
                        LootTable lootTable = item.getParent();
                        LootTableCommand.addcommand(player, lootTable, item, response);
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
        return 26;
    }

    @Override
    public ItemStack displayItem() {
        return new ItemBuilder(Material.PIGLIN_BANNER_PATTERN).setName("&aAdd Command &7(Click)").create();
    }

}
