package dev.aurapvp.samurai.faction.editor.impl;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.faction.Faction;
import dev.aurapvp.samurai.faction.editor.FactionEditor;
import dev.aurapvp.samurai.faction.editor.menu.FactionEditorMenu;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.ConversationBuilder;
import dev.aurapvp.samurai.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NameEditor extends FactionEditor {

    @Override
    public void edit(Player sender, Faction faction) {
        ConversationBuilder builder = new ConversationBuilder(sender);

        sender.beginConversation(builder.closeableStringPrompt(CC.translate("&aType the new name of the faction in chat, type 'cancel' to stop this process."), (ctx, response) -> {
            if (response.equalsIgnoreCase("cancel")) {
                sender.sendMessage(CC.translate("&cProcess cancelled."));
                new FactionEditorMenu(faction).openMenu(sender);
                return Prompt.END_OF_CONVERSATION;
            }

            faction.rename(response);
            new FactionEditorMenu(faction).openMenu(sender);
            return Prompt.END_OF_CONVERSATION;
        }).echo(false).build());

        sender.closeInventory();
    }

    @Override
    public int slot(Player sender, Faction faction) {
        return 1;
    }

    @Override
    public ItemStack displayItem(Player sender, Faction faction) {
        return new ItemBuilder(Material.NAME_TAG).setName(CC.GREEN + "Edit Faction Name &7(Click)").create();
    }
}
