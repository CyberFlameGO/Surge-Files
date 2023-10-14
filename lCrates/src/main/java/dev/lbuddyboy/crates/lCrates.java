package dev.lbuddyboy.crates;

import co.aikar.commands.PaperCommandManager;
import dev.lbuddyboy.crates.api.CrateAPI;
import dev.lbuddyboy.crates.api.FoxtrotAPI;
import dev.lbuddyboy.crates.api.pCoreAPI;
import dev.lbuddyboy.crates.command.CrateCommand;
import dev.lbuddyboy.crates.command.KeysCommand;
import dev.lbuddyboy.crates.command.completions.CrateCompletion;
import dev.lbuddyboy.crates.command.completions.CrateItemsCompletion;
import dev.lbuddyboy.crates.command.contexts.CrateContext;
import dev.lbuddyboy.crates.command.contexts.CrateItemContext;
import dev.lbuddyboy.crates.listener.CrateListener;
import dev.lbuddyboy.crates.model.Crate;
import dev.lbuddyboy.crates.model.CrateItem;
import dev.lbuddyboy.crates.util.Config;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Getter
public class lCrates extends JavaPlugin {

    @Getter private static lCrates instance;

    private API api;
    private boolean virtualKeys = false;
    private PaperCommandManager commandManager;
    private final Map<String, Crate> crates = new HashMap<>();
    private File crateFolder;

    @Override
    public void onEnable() {
        instance = this;
        this.crates.clear();
        saveDefaultConfig();

        this.virtualKeys = getConfig().getBoolean("virtual");
        this.loadListeners();
        this.loadCommands();
        this.loadDirectories();
        this.loadAllCrates();
        if (this.getServer().getPluginManager().getPlugin("Samurai") != null) {
            this.api = new FoxtrotAPI();
        } else if (this.getServer().getPluginManager().getPlugin("pCore") != null) {
            this.api = new pCoreAPI();
        } else {
            this.api = new CrateAPI();
        }
        this.api.onEnable();
        new CrateThread().start();
    }

    @Override
    public void onDisable() {
        this.crates.values().forEach(crate -> {
            crate.save();
            crate.resetLocations();
        });
    }

    private void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new CrateListener(), this);
    }

    private void loadCommands() {
        this.commandManager = new PaperCommandManager(this);
        this.commandManager.getCommandCompletions().registerCompletion("crates", new CrateCompletion());
        this.commandManager.getCommandCompletions().registerCompletion("itemIds", new CrateItemsCompletion());
        this.commandManager.getCommandContexts().registerContext(Crate.class, new CrateContext());
        this.commandManager.getCommandContexts().registerContext(CrateItem.class, new CrateItemContext());
        this.commandManager.registerCommand(new CrateCommand());
        this.commandManager.registerCommand(new KeysCommand());
    }

    private void loadDirectories() {
        this.crateFolder = new File(getDataFolder(), "crates");

        if (!this.crateFolder.exists()) this.crateFolder.mkdir();
    }

    public void loadAllCrates() {
        for (String s : this.crateFolder.list()) {
            System.out.println(s);
            String crateName = s.replaceAll(".yml", "");
            System.out.println(crateName);
            Config config = new Config(this, crateName, this.crateFolder);
            Crate crate = new Crate(config);

            this.crates.put(crateName, crate);
        }
    }

    public void save() {
        this.saveConfig();
    }

}
