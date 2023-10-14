package dev.aurapvp.samurai.lootbags;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.lootbags.command.LootBagCommand;
import dev.aurapvp.samurai.lootbags.listener.LootBagListener;
import dev.aurapvp.samurai.util.Config;
import dev.aurapvp.samurai.util.IModule;
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
    public String getId() {
        return "lootbags";
    }

    @Override
    public void load(Samurai plugin) {
        this.loadListeners();
        this.loadCommands();
        reload();
    }

    @Override
    public void unload(Samurai plugin) {
        save();
    }

    @Override
    public void save() {
        this.lootBags.values().forEach(LootBag::save);
    }

    private void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new LootBagListener(), Samurai.getInstance());
    }

    private void loadCommands() {
        Samurai.getInstance().getCommandManager().getCommandCompletions().registerCompletion("lootbags", s -> lootBags.values().stream().map(LootBag::getName).collect(Collectors.toList()));
        Samurai.getInstance().getCommandManager().registerCommand(new LootBagCommand());
    }

    private void loadDirectories() {
        this.lootBagsDirectory = new File(Samurai.getInstance().getDataFolder(), "lootbags");

        if (!this.lootBagsDirectory.exists()) this.lootBagsDirectory.mkdir();
    }

    private void loadLootBags() {
        for (String s : this.lootBagsDirectory.list()) {
            String name = s.replaceAll(".yml", "");

            this.lootBags.put(name, new LootBag(new Config(Samurai.getInstance(), name, this.lootBagsDirectory)));
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
