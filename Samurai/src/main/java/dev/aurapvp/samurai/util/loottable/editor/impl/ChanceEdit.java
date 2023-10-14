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

public class ChanceEdit implements ItemEditor {

    @Override
    public Conversation conversation(Player player, List<LootTableItem> items) {

        return new ConversationBuilder(player)
                .closeableStringPrompt(CC.translate("&aType the new item chance to the loottable, you can also type 'cancel' to stop this process."), (ctx, response) -> {
                    double chance = 1;

                    try {
                        chance = Double.parseDouble(response);
                    } catch (NumberFormatException ignored) {
                        player.sendMessage(CC.translate("&cIncorrect number format."));
                        return Prompt.END_OF_CONVERSATION;
                    }

                    for (LootTableItem item : items) {
                        LootTable lootTable = item.getParent();

                        LootTableCommand.chance(player, lootTable, item, chance);
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
        return 24;
    }

    @Override
    public ItemStack displayItem() {
        return new ItemBuilder(Material.PAPER).setName("&6Chance Editor &7(Click)").create();
    }

}
