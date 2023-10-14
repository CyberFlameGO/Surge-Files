package dev.lbuddyboy.samurai.custom.pets.menu;

import dev.lbuddyboy.flash.util.bukkit.ItemBuilder;
import dev.lbuddyboy.flash.util.menu.Button;
import dev.lbuddyboy.flash.util.menu.GUISettings;
import dev.lbuddyboy.flash.util.menu.Menu;
import dev.lbuddyboy.samurai.custom.pets.PetRarity;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.pets.menu.sub.PetRarityMenu;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PetsMenu extends Menu {

    private static final GUISettings guiSettings = Samurai.getInstance().getPetHandler().getRarityGUISettings();

    @Override
    public String getTitle(Player player) {
        return CC.translate(guiSettings.getTitle());
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (PetRarity rarity : Samurai.getInstance().getPetHandler().getRarities().values()) {
            buttons.add(new RarityButton(rarity));
        }
 
        return buttons;
    }

    @AllArgsConstructor
    public class RarityButton extends Button {

        private PetRarity rarity;

        @Override
        public int getSlot() {
            return rarity.getMenuSlot();
        }

        @Override
        public ItemStack getItem() {
            Material material = Material.getMaterial(Samurai.getInstance().getPetHandler().getConfig().getString("menu-settings.button-format.material"));
            int data = Samurai.getInstance().getPetHandler().getConfig().getInt("menu-settings.button-format.data");

            if (data == -1) data = CC.toDyeColor(ChatColor.getByChar(this.rarity.getColor().charAt(1)));

            return new ItemBuilder(material, 1)
                    .setName(Samurai.getInstance().getPetHandler().getConfig().getString("menu-settings.button-format.name"),
                            "%rarity-color%", this.rarity.getColor(),
                            "%rarity-display%", this.rarity.getDisplayName())
                    .setLore(Samurai.getInstance().getPetHandler().getConfig().getStringList("menu-settings.button-format.lore"),
                            "%rarity-color%", this.rarity.getColor(),
                            "%rarity-display%", this.rarity.getDisplayName())
                    .setDurability(data)
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            new PetRarityMenu(this.rarity).openMenu(player);
        }
    }

}
