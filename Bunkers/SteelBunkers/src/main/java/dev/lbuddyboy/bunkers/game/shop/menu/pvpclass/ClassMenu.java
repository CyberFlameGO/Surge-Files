package dev.lbuddyboy.bunkers.game.shop.menu.pvpclass;

import dev.lbuddyboy.bunkers.game.shop.menu.buttons.BuyButton;
import dev.lbuddyboy.bunkers.util.ItemBuilder;
import dev.lbuddyboy.bunkers.util.menu.Button;
import dev.lbuddyboy.bunkers.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

public class ClassMenu extends Menu {

    public static int[] ARMOR_SLOTS = new int[]{
            10,11,12,13,
            19,20,21,22,
            28,29,30,31
    };

    @Override
    public String getTitle(Player player) {
        return "Combat Shop";
    }
    // helm: 150 chest: 225 legs 200 boots 125 700
    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        int i = 0;

        buttons.add(new BuyButton(player, ARMOR_SLOTS[i++], "&a1x Bard Helmet", 150, ItemBuilder.of(Material.GOLDEN_HELMET).build(), false));
        buttons.add(new BuyButton(player, ARMOR_SLOTS[i++], "&a1x Bard Helmet", 150, ItemBuilder.of(Material.GOLDEN_HELMET).build(), false));
        
        return buttons;
    }

    @Override
    public boolean autoFill() {
        return true;
    }

    @Override
    public int getSize(Player player) {
        return 54;
    }
}
