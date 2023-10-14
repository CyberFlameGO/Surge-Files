package dev.lbuddyboy.samurai.util.menu.menus;

import dev.lbuddyboy.samurai.util.menu.Menu;
import dev.lbuddyboy.samurai.util.menu.buttons.BooleanButton;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.object.Callback;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.Map;

public class ConfirmMenu extends Menu {
    private final String title;
    private final Callback<Boolean> response;

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons=new HashMap<Integer, Button>();
        for ( int i=0; i < 9; ++i ) {
            if (i == 3) {
                buttons.put(i, new BooleanButton(true, this.response));
                continue;
            }
            if (i == 5) {
                buttons.put(i, new BooleanButton(false, this.response));
                continue;
            }
            buttons.put(i, Button.placeholder(Material.LEGACY_STAINED_GLASS_PANE, (byte) 14));
        }
        return buttons;
    }

    @Override
    public String getTitle(Player player) {
        return this.title;
    }

    @ConstructorProperties(value={"title", "response"})
    public ConfirmMenu(String title, Callback<Boolean> response) {
        this.title=title;
        this.response=response;
    }
}

