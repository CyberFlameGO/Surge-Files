package dev.lbuddyboy.hub.menu;

import dev.lbuddyboy.hub.lHub;
import dev.lbuddyboy.hub.lModule;
import dev.lbuddyboy.hub.menu.actions.IAction;
import dev.lbuddyboy.hub.menu.actions.impl.*;
import dev.lbuddyboy.hub.menu.exception.MenuAlreadyExistsException;
import dev.lbuddyboy.hub.util.CC;
import dev.lbuddyboy.hub.util.YMLBase;
import dev.lbuddyboy.hub.util.menu.Menu;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 22/09/2021 / 1:47 AM
 * LBuddyBoy-Development / me.lbuddyboy.hub.menu
 */

@Getter
public class CustomMenuHandler implements lModule {

    private final List<IAction<?, ?>> actions;
    private final Map<String, CustomMenu> customMenus;
    private File menusDirectory;

    public CustomMenuHandler() {
        this.actions = Arrays.asList(
                new AddQueueAction(),
                new CommandAction(),
                new EnderButtAction(),
                new GamesAction(),
                new OpenMenuAction(),
                new SendMessageAction(),
                new SendPlayerAction()
        );
        this.customMenus = new HashMap<>();
    }

    private void loadDirectories() {
        this.menusDirectory = new File(lHub.getInstance().getDataFolder(), "menus");

        if (!this.menusDirectory.exists()) this.menusDirectory.mkdir();
    }

    private void loadMenus() {
        for (String s : this.menusDirectory.list()) {
            String name = s.replaceAll(".yml", "");
            YMLBase file = new YMLBase(this.menusDirectory, name);
            FileConfiguration config = file.getConfig();

            String title = config.getString("title");
            int size = config.getInt("size");
            boolean autofill = config.getBoolean("autofill");

            this.customMenus.put(name, new CustomMenu(title, size, autofill, file));
        }
    }

    private void loadDefaults() {
        if (!this.customMenus.isEmpty()) return;

        try {
            createCustomMenu("ServerSelector").save();
        } catch (MenuAlreadyExistsException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void load(lHub plugin) {
        this.loadDirectories();
        this.loadMenus();
        this.loadDefaults();
    }

    @Override
    public void unload(lHub plugin) {

    }

    @Override
    public void reload() {
        this.customMenus.values().forEach(menu -> menu.getFile().reloadConfig());
        this.customMenus.clear();
        this.loadMenus();

        Bukkit.getOnlinePlayers().forEach(player -> {
            if (!Menu.openedMenus.containsKey(player.getName())) return;

            Menu menu = Menu.openedMenus.get(player.getName());

            player.closeInventory();
            menu.openMenu(player);
            player.sendMessage(CC.translate("&aTrouble connecting to the menu api. Your current menu has been reloaded."));
        });
    }

    public CustomMenu getMenu(String toSearch) {
        return this.customMenus.get(toSearch);
    }

    public void performAction(String input, Player player, String value) {
        for (IAction<?, ?> action : this.actions) {
            if (action.getName().equalsIgnoreCase(input)) {
                action.perform(player, value);
                return;
            }
        }
    }

    public CustomMenu createCustomMenu(String name) throws MenuAlreadyExistsException {
        if (this.customMenus.containsKey(name))
            throw new MenuAlreadyExistsException("A menu under this name already exists.");

        return new CustomMenu(new YMLBase(this.menusDirectory, name));
    }

}
