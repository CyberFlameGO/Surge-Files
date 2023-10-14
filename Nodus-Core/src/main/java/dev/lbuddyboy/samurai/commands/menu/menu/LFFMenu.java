package dev.lbuddyboy.samurai.commands.menu.menu;

import dev.lbuddyboy.samurai.commands.menu.menu.buttons.LFFCompleteButton;
import dev.lbuddyboy.samurai.commands.menu.menu.buttons.LFFKitButton;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LFFMenu extends Menu {

    @Getter
    private List<String> selected = new ArrayList<>();

    public LFFMenu() {
        super(ChatColor.BLUE + "Select your Classes");
        setAutoUpdate(true);
    }


    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();

        buttonMap.put(0, new LFFKitButton("Diamond", Material.DIAMOND_CHESTPLATE, this));
        buttonMap.put(1, new LFFKitButton("Bard", Material.GOLDEN_CHESTPLATE, this));
        buttonMap.put(2, new LFFKitButton("Rogue", Material.CHAINMAIL_CHESTPLATE, this));
        buttonMap.put(3, new LFFKitButton("Archer", Material.LEATHER_CHESTPLATE, this));
        buttonMap.put(4, new LFFKitButton("Base Bitch", Material.DIAMOND_PICKAXE, this));
        buttonMap.put(8, new LFFCompleteButton(this));

        return buttonMap;
    }
}
