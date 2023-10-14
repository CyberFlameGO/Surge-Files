package dev.lbuddyboy.pcore.pets.menu;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.pets.PetRarity;
import dev.lbuddyboy.pcore.pets.menu.sub.PetRarityMenu;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.GUISettings;
import dev.lbuddyboy.pcore.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PetsMenu extends Menu {

    private static final GUISettings guiSettings = pCore.getInstance().getPetHandler().getRarityGUISettings();

    @Override
    public String getTitle(Player player) {
        return CC.translate(guiSettings.getTitle());
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (PetRarity rarity : pCore.getInstance().getPetHandler().getRarities().values()) {
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
            Material material = Material.getMaterial(pCore.getInstance().getPetHandler().getConfig().getString("menu-settings.button-format.material"));
            int data = pCore.getInstance().getPetHandler().getConfig().getInt("menu-settings.button-format.data");

            if (data == -1) data = CC.toDyeColor(ChatColor.getByChar(this.rarity.getColor().charAt(1)));

            return new ItemBuilder(material, 1)
                    .setName(pCore.getInstance().getPetHandler().getConfig().getString("menu-settings.button-format.name"),
                            "%rarity-color%", this.rarity.getColor(),
                            "%rarity-display%", this.rarity.getDisplayName())
                    .setLore(pCore.getInstance().getPetHandler().getConfig().getStringList("menu-settings.button-format.lore"),
                            "%rarity-color%", this.rarity.getColor(),
                            "%rarity-display%", this.rarity.getDisplayName())
                    .setDurability(data)
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            new PetRarityMenu(this.rarity).openMenu(player);
        }
    }

}
