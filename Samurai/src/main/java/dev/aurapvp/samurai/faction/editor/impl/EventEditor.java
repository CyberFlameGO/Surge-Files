package dev.aurapvp.samurai.faction.editor.impl;

import dev.aurapvp.samurai.faction.Faction;
import dev.aurapvp.samurai.faction.editor.FactionEditor;
import dev.aurapvp.samurai.faction.editor.menu.FactionEventEditorMenu;
import dev.aurapvp.samurai.faction.editor.menu.FactionSystemTypeEditorMenu;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EventEditor extends FactionEditor {

    @Override
    public void edit(Player sender, Faction faction) {
        new FactionEventEditorMenu(faction).openMenu(sender);
    }

    @Override
    public int slot(Player sender, Faction faction) {
        return 1;
    }

    @Override
    public ItemStack displayItem(Player sender, Faction faction) {
        return new ItemBuilder((faction.getBoundEvent() == null ? Material.BARRIER : faction.getBoundEvent().getEditorMaterial())).setName(CC.GREEN + "Bind Event &7(Click)").setLore("&7Current: " + (faction.getBoundEvent() == null ? "None" : faction.getBoundEvent().getName())).create();
    }
}
