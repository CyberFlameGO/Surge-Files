package dev.aurapvp.samurai.util.loottable.editor.impl;

import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.ConversationBuilder;
import dev.aurapvp.samurai.util.ItemBuilder;
import dev.aurapvp.samurai.util.loottable.LootTable;
import dev.aurapvp.samurai.util.loottable.LootTableItem;
import dev.aurapvp.samurai.util.loottable.command.LootTableCommand;
import dev.aurapvp.samurai.util.loottable.editor.ItemEditor;
import dev.aurapvp.samurai.util.loottable.editor.menu.EditLootTableItemMenu;
import dev.aurapvp.samurai.util.loottable.editor.menu.EditLootTableMenu;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SlotEdit implements ItemEditor {

    @Override
    public Conversation conversation(Player player, List<LootTableItem> items) {
        return new ConversationBuilder(player)
                .closeableStringPrompt(CC.translate("&aType the new item slot to the loottable, you can also type 'cancel' to stop this process."), (ctx, response) -> {
                    if (items.size() > 1) {
                        player.sendMessage(CC.translate("&cYou cannot mass edit the item slots."));
                        new EditLootTableMenu(items.get(0).getParent()).openMenu(player);
                        return Prompt.END_OF_CONVERSATION;
                    }
                    int slot = 1;
                    LootTableItem item = items.get(0);
                    LootTable lootTable = item.getParent();

                    try {
                        slot = Integer.parseInt(response);
                    } catch (NumberFormatException ignored) {
                        player.sendMessage(CC.translate("&cIncorrect number format."));
                        return Prompt.END_OF_CONVERSATION;
                    }

                    LootTableCommand.slot(player, lootTable, item, slot);
                    new EditLootTableItemMenu(item).openMenu(player);
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
        return 22;
    }

    @Override
    public ItemStack displayItem() {
        return new ItemBuilder(Material.IRON_NUGGET).setName("&7Edit Slot &7(Click)").create();
    }

}
