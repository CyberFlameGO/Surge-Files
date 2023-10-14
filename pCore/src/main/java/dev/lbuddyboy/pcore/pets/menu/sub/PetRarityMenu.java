package dev.lbuddyboy.pcore.pets.menu.sub;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.pets.IPet;
import dev.lbuddyboy.pcore.pets.PetRarity;
import dev.lbuddyboy.pcore.pets.menu.PetsMenu;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.PagedGUISettings;
import dev.lbuddyboy.pcore.util.menu.button.BackButton;
import dev.lbuddyboy.pcore.util.menu.paged.PagedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class PetRarityMenu extends PagedMenu {

    private static final PagedGUISettings guiSettings = pCore.getInstance().getPetHandler().getPetGUISettings();

    private PetRarity rarity;

    @Override
    public String getPageTitle(Player player) {
        return CC.translate(guiSettings.getTitle(),
                "%rarity-color%", this.rarity.getColor(),
                "%rarity-display%", this.rarity.getDisplayName());
    }

    @Override
    public List<Button> getPageButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (IPet pet : pCore.getInstance().getPetHandler().getPets().values()) {
            if (pet.getPetRarity() == this.rarity && pet.isEnabled()) buttons.add(new PetButton(pet));
        }

        return buttons;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        buttons.add(new BackButton(pCore.getInstance().getPetHandler().getConfig().getInt("pet-menu-settings.back-button.slot"), new PetsMenu()));

        return buttons;
    }

    @Override
    public int getSize(Player player) {
        return pCore.getInstance().getPetHandler().getPetGUISettings().getSize();
    }

    @Override
    public int[] getButtonSlots() {
        return pCore.getInstance().getPetHandler().getPetGUISettings().getButtonSlots();
    }

    @Override
    public ItemStack autoFillItem() {
        return pCore.getInstance().getPetHandler().getPetGUISettings().getAutoFillItem();
    }

    @Override
    public boolean autoFill() {
        return pCore.getInstance().getPetHandler().getPetGUISettings().isAutoFill();
    }

    @Override
    public int getNextPageButtonSlot() {
        return pCore.getInstance().getPetHandler().getPetGUISettings().getNextSlot();
    }

    @Override
    public int getPreviousButtonSlot() {
        return pCore.getInstance().getPetHandler().getPetGUISettings().getPreviousSlot();
    }

    @AllArgsConstructor
    public class PetButton extends Button {

        private IPet pet;

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public ItemStack getItem() {
            ItemStack stack = CC.getCustomSkull(this.pet.getHeadURL());

            return new ItemBuilder(stack)
                    .setName(this.pet.getDisplayName(),
                            "%rarity-color%", this.pet.getLore(),
                            "%level%", "1-" + this.pet.getMaxLevel(),
                            "%experience%", 0,
                            "%needed-experience%", 100 * 350,
                            "%rarity-color%", this.pet.getPetRarity().getColor(),
                            "%rarity-display%", this.pet.getPetRarity().getDisplayName())
                    .setLore(this.pet.getMenuLore(),
                            "%rarity-color%", this.pet.getLore(),
                            "%level%", "1-" + this.pet.getMaxLevel(),
                            "%experience%", 0,
                            "%needed-experience%", 100 * 350,
                            "%rarity-color%", this.pet.getPetRarity().getColor(),
                            "%rarity-display%", this.pet.getPetRarity().getDisplayName())
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            if (!player.isOp()) return;

            player.getInventory().addItem(pet.createPet(1));
        }
    }

}
