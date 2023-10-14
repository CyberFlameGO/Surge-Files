package dev.aurapvp.samurai.faction.editor.menu;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.events.Event;
import dev.aurapvp.samurai.faction.Faction;
import dev.aurapvp.samurai.faction.FactionType;
import dev.aurapvp.samurai.util.ItemBuilder;
import dev.aurapvp.samurai.util.menu.Button;
import dev.aurapvp.samurai.util.menu.button.BackButton;
import dev.aurapvp.samurai.util.menu.paged.PagedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class FactionEventEditorMenu extends PagedMenu {

    private Faction faction;

    @Override
    public String getPageTitle(Player player) {
        return "Editing: " + faction.getName();
    }

    @Override
    public List<Button> getPageButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (Event event : Samurai.getInstance().getEventHandler().getEvents().values()) {
            if (faction.getType() != event.getFactionType()) continue;

            buttons.add(new FactionEditorButton(player, faction, event));
        }

        return buttons;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        buttons.add(new BackButton(5, new FactionEditorMenu(this.faction)));

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
        private Event event;

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(event.getEditorMaterial()).setName(event.getEventType().getColor() + event.getName() + " &7(CLICK)").create();
        }

        @Override
        public void action(InventoryClickEvent e) throws Exception {
            if (!(e.getWhoClicked() instanceof Player player)) return;

            faction.setBoundEvent(this.event);
            faction.triggerUpdate();
            new FactionEditorMenu(faction).openMenu(player);
        }
    }

}
