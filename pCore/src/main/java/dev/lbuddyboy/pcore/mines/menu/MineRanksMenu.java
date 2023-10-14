package dev.lbuddyboy.pcore.mines.menu;

import dev.lbuddyboy.pcore.mines.MineRank;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.pets.IPet;
import dev.lbuddyboy.pcore.pets.PetRarity;
import dev.lbuddyboy.pcore.pets.menu.PetsMenu;
import dev.lbuddyboy.pcore.pets.menu.sub.PetRarityMenu;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.PagedGUISettings;
import dev.lbuddyboy.pcore.util.menu.button.BackButton;
import dev.lbuddyboy.pcore.util.menu.paged.PagedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MineRanksMenu extends PagedMenu {

    private static final PagedGUISettings guiSettings = pCore.getInstance().getPrivateMineHandler().getRankGUISettings();

    @Override
    public String getPageTitle(Player player) {
        return CC.translate(guiSettings.getTitle());
    }

    @Override
    public List<Button> getPageButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (MineRank rank : pCore.getInstance().getPrivateMineHandler().getRanks().values()) {
            buttons.add(new RankButton(rank, player));
        }

        return buttons;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        buttons.add(new BackButton(pCore.getInstance().getPrivateMineHandler().getConfig().getInt("private-mines-menu.back-button.slot"), new PrivateMineMenu()));

        return buttons;
    }

    @Override
    public int getSize(Player player) {
        return guiSettings.getSize();
    }

    @Override
    public int[] getButtonSlots() {
        return guiSettings.getButtonSlots();
    }

    @Override
    public ItemStack autoFillItem() {
        return guiSettings.getAutoFillItem();
    }

    @Override
    public boolean autoFill() {
        return guiSettings.isAutoFill();
    }

    @Override
    public int getNextPageButtonSlot() {
        return guiSettings.getNextSlot();
    }

    @Override
    public int getPreviousButtonSlot() {
        return guiSettings.getPreviousSlot();
    }

    @AllArgsConstructor
    public static class RankButton extends Button {

        private MineRank rank;
        private Player player;

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public ItemStack getItem() {
            return this.rank.getDisplayItem(this.player);
        }

    }

}
