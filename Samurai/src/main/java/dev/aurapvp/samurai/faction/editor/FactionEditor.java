package dev.aurapvp.samurai.faction.editor;

import dev.aurapvp.samurai.faction.Faction;
import dev.aurapvp.samurai.faction.editor.impl.NameEditor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class FactionEditor {

    public abstract void edit(Player sender, Faction faction);
    public abstract int slot(Player sender, Faction faction);
    public abstract ItemStack displayItem(Player sender, Faction faction);

}
