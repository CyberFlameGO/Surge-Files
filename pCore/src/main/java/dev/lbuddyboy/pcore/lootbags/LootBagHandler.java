package dev.lbuddyboy.pcore.lootbags;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.lootbags.command.LootBagCommand;
import dev.lbuddyboy.pcore.lootbags.listener.LootBagListener;
import dev.lbuddyboy.pcore.util.Config;
import dev.lbuddyboy.pcore.util.IModule;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class LootBagHandler implements IModule {

    private final Map<String, LootBag> lootBags;
    private File lootBagsDirectory;

    public LootBagHandler() {
        this.lootBags = new HashMap<>();
    }

    @Override
    public void load(pCore plugin) {
        this.loadListeners();
        this.loadCommands();
        reload();
    }

    @Override
    public void unload(pCore plugin) {
        save();
    }

    @Override
    public void save() {
        this.lootBags.values().forEach(LootBag::save);
    }

    private void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new LootBagListener(), pCore.getInstance());
    }

    private void loadCommands() {
        pCore.getInstance().getCommandManager().getCommandCompletions().registerCompletion("lootbags", s -> lootBags.values().stream().map(LootBag::getName).collect(Collectors.toList()));
        pCore.getInstance().getCommandManager().registerCommand(new LootBagCommand());
    }

    private void loadDirectories() {
        this.lootBagsDirectory = new File(pCore.getInstance().getDataFolder(), "lootbags");

        if (!this.lootBagsDirectory.exists()) this.lootBagsDirectory.mkdir();
    }

    private void loadLootBags() {
        for (String s : this.lootBagsDirectory.list()) {
            String name = s.replaceAll(".yml", "");

            this.lootBags.put(name, new LootBag(new Config(pCore.getInstance(), name, this.lootBagsDirectory)));
        }
        Bukkit.getConsoleSender().sendMessage("[LootBag Handler] Loaded " + this.lootBags.size() + " loot bags.");
    }

    @Override
    public void reload() {
        this.lootBags.clear();

        this.loadDirectories();
        this.loadLootBags();
    }

}
