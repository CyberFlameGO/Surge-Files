package dev.lbuddyboy.samurai.settings.menu;

import com.google.common.collect.Maps;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import dev.lbuddyboy.samurai.settings.Setting;
import org.bukkit.entity.Player;

import java.util.Map;

public class SettingsMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "Options";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        int i = -1;
        for (Setting setting : Setting.values()) {
            buttons.put(++i, setting.toButton());
        }

        return buttons;
    }

}
