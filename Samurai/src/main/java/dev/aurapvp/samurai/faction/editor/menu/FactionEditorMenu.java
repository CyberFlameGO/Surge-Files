package dev.aurapvp.samurai.faction.editor.menu;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.faction.Faction;
import dev.aurapvp.samurai.faction.editor.FactionEditor;
import dev.aurapvp.samurai.util.ItemBuilder;
import dev.aurapvp.samurai.util.menu.Button;
import dev.aurapvp.samurai.util.menu.paged.PagedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class FactionEditorMenu extends PagedMenu {

    private Faction faction;

    @Override
    public String getPageTitle(Player player) {
        return "Editing: " + faction.getName();
    }

    @Override
    public List<Button> getPageButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (FactionEditor editor : Samurai.getInstance().getFactionHandler().getEditors()) {
            buttons.add(new FactionEditorButton(player, this.faction, editor));
        }

        return buttons;
    }

    @Override
    public int getNextPageButtonSlot() {
        return -1;
    }

    @Override
    public int getPreviousButtonSlot() {
        return -1;
    }

    @AllArgsConstructor
    private class FactionEditorButton extends Button {

        private Player player;
        private Faction faction;
        private FactionEditor editor;

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public ItemStack getItem() {
            return editor.displayItem(this.player, this.faction);
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player player)) return;

            this.editor.edit(player, this.faction);
        }
    }

}
