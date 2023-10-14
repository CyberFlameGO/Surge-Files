package dev.lbuddyboy.pcore.shop;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.util.Config;
import dev.lbuddyboy.pcore.util.IModule;
import dev.lbuddyboy.pcore.util.YamlDoc;
import dev.lbuddyboy.pcore.util.menu.GUISettings;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ShopHandler implements IModule {

    private final List<ShopCategory> categories;
    private File categoryFolder;
    private final YamlDoc shopYML;
    private GUISettings guiSettings;

    public ShopHandler() {
        this.categories = new ArrayList<>();
        this.shopYML = new YamlDoc(pCore.getInstance().getDataFolder(), "shop.yml");
    }

    private void loadMainGUISettings() {
        this.guiSettings = new GUISettings(this.shopYML.gc());
    }

    private void loadDirectories() {
        this.categoryFolder = new File(pCore.getInstance().getDataFolder(), "categories");

        if (!this.categoryFolder.exists()) this.categoryFolder.mkdir();
    }

    private void loadCategories() {
        for (String key : this.categoryFolder.list()) {
            String name = key.replaceAll(".yml", "");
            this.categories.add(new ShopCategory(new Config(pCore.getInstance(), name, this.categoryFolder)));
        }
        Bukkit.getConsoleSender().sendMessage("[Shop Handler] Loaded " + this.categories.size() + " shop categories.");
    }

    private void loadDefaultCategories() {
        if (!this.categories.isEmpty()) return;

        this.categories.add(new ShopCategory(new Config(pCore.getInstance(), "Blocks", this.categoryFolder)));
        this.categories.add(new ShopCategory(new Config(pCore.getInstance(), "Decoration", this.categoryFolder)));
    }

    @Override
    public void reload() {
        this.categories.clear();

        this.loadDirectories();
        this.loadCategories();
        this.loadDefaultCategories();
    }

    @Override
    public void load(pCore plugin) {
        this.loadMainGUISettings();
        this.loadDirectories();
        this.loadCategories();
        this.loadDefaultCategories();
    }

    @Override
    public void unload(pCore plugin) {

    }
}
