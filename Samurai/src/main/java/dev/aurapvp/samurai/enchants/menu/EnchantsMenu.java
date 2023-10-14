package dev.aurapvp.samurai.enchants.menu;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.util.ItemBuilder;
import dev.aurapvp.samurai.util.menu.Button;
import dev.aurapvp.samurai.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EnchantsMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "Custom Enchants";
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        int i = 1;
        for (String gear : Samurai.getInstance().getEnchantHandler().getGEAR()) {
            Material gearType = Material.getMaterial("LEATHER_" + gear);

            buttons.add(new GearButton(gear, gearType, i));
            i++;
        }
        for (String gear : Samurai.getInstance().getEnchantHandler().getNON_GEAR()) {
            Material gearType = Material.getMaterial(gear);

            buttons.add(new GearButton(gear, gearType, i));
            i++;
        }
        for (String gear : Samurai.getInstance().getEnchantHandler().getTOOLS()) {
            Material gearType = Material.getMaterial("STONE_" + gear);

            buttons.add(new GearButton(gear, gearType, i));
            i++;
        }

        return buttons;
    }

    @AllArgsConstructor
    private class GearButton extends Button {

        private String gear;
        private Material material;
        private int slot;

        @Override
        public int getSlot() {
            return slot;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(this.material)
                    .setName("&6&l" + gear + " Enchants")
                    .setLore("&7Click to view all of the enchants", "&7regarding " + gear + "'s!")
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            new EnchantsPreviewMenu(gear).openMenu(player);
        }
    }

}
