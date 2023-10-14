package dev.aurapvp.samurai.faction.editor.impl;

import dev.aurapvp.samurai.faction.Faction;
import dev.aurapvp.samurai.faction.editor.FactionEditor;
import dev.aurapvp.samurai.faction.editor.menu.FactionEditorMenu;
import dev.aurapvp.samurai.faction.editor.menu.FactionSystemTypeEditorMenu;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.ConversationBuilder;
import dev.aurapvp.samurai.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TypeEditor extends FactionEditor {

    @Override
    public void edit(Player sender, Faction faction) {
        new FactionSystemTypeEditorMenu(faction).openMenu(sender);
    }

    @Override
    public int slot(Player sender, Faction faction) {
        return 1;
    }

    @Override
    public ItemStack displayItem(Player sender, Faction faction) {
        return new ItemBuilder(faction.getType().getEditorMaterial()).setName(CC.GREEN + "Edit System Type &7(Click)").setLore("&7Current: " + faction.getType().getName()).create();
    }
}
