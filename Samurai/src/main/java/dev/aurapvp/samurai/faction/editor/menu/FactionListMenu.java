package dev.aurapvp.samurai.faction.editor.menu;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.faction.Faction;
import dev.aurapvp.samurai.util.ItemBuilder;
import dev.aurapvp.samurai.util.menu.Button;
import dev.aurapvp.samurai.util.menu.paged.PagedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class FactionListMenu extends PagedMenu {

    @Override
    public String getPageTitle(Player player) {
        return "Faction Editor";
    }

    @Override
    public List<Button> getPageButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (Faction faction : Samurai.getInstance().getFactionHandler().getFactions().values()) {
            if (faction.getLeader() != null) continue;

            buttons.add(new FactionButton(faction));
        }

        return buttons;
    }

    @AllArgsConstructor
    private class FactionButton extends Button {

        private Faction faction;

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(faction.getType().getEditorMaterial()).setName(faction.getName()).setLore("&7Click to edit this faction!").create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player player)) return;

            new FactionEditorMenu(this.faction).openMenu(player);
        }
    }

}
