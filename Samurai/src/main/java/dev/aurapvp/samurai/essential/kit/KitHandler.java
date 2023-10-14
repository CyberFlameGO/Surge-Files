package dev.aurapvp.samurai.essential.kit;

import dev.aurapvp.samurai.essential.kit.command.KitCommand;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.player.SamuraiPlayer;
import dev.aurapvp.samurai.util.Config;
import dev.aurapvp.samurai.util.IModule;
import dev.aurapvp.samurai.util.Tasks;
import dev.aurapvp.samurai.util.menu.GUISettings;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class KitHandler implements IModule {

    private final Map<String, Kit> kits;
    private final Map<String, KitCategory> categories;

    private File kitsDirectory, categoriesDirectory;
    private Config kitsConfig;
    private GUISettings guiSettings;

    public KitHandler() {
        this.kits = new HashMap<>();
        this.categories = new HashMap<>();
    }

    @Override
    public String getId() {
        return "kits";
    }

    @Override
    public void load(Samurai plugin) {
        this.loadCommands();
        reload();
    }

    @Override
    public void unload(Samurai plugin) {

    }

    @Override
    public void reload() {
        this.loadDirectories();
        this.kits.clear();
        this.categories.clear();

        this.loadKits();
        this.loadDefaults();
    }

    private void loadDirectories() {
        this.kitsConfig = new Config(Samurai.getInstance(), "kits");
        this.kitsDirectory = new File(Samurai.getInstance().getDataFolder(), "kits");

        if (!this.kitsDirectory.exists()) this.kitsDirectory.mkdir();

        this.categoriesDirectory = new File(this.kitsDirectory, "categories");
        if (!this.categoriesDirectory.exists()) this.categoriesDirectory.mkdir();

        this.guiSettings = new GUISettings(this.kitsConfig);
    }

    public void loadKits() {
        for (String s : this.categoriesDirectory.list()) {
            if (!s.endsWith(".yml")) continue;

            String name = s.replaceAll(".yml", "");

            this.categories.put(name.toLowerCase(), new KitCategory(new Config(Samurai.getInstance(), name, this.categoriesDirectory)));
        }

        for (String s : this.kitsDirectory.list()) {
            if (!s.endsWith(".yml")) continue;

            String name = s.replaceAll(".yml", "");

            this.kits.put(name, new Kit(new Config(Samurai.getInstance(), name, this.kitsDirectory)));
        }

        Bukkit.getConsoleSender().sendMessage("[Kit Handler] Loaded " + this.kits.size() + " kits.");
        Bukkit.getConsoleSender().sendMessage("[Kit Handler] Loaded " + this.categories.size() + " categories.");
    }

    private void loadDefaults() {
        if (!this.kits.isEmpty()) return;

        Tasks.delayed(() -> this.kits.put("Starter", new Kit(new Config(Samurai.getInstance(), "Starter", this.kitsDirectory))));
    }

    private void loadCommands() {
        Samurai.getInstance().getCommandManager().getCommandCompletions().registerCompletion("kits", s -> kits.values().stream().map(Kit::getName).collect(Collectors.toList()));
        Samurai.getInstance().getCommandManager().registerCommand(new KitCommand());
    }

    public void applyCooldown(UUID uuid, Kit kit) {
        SamuraiPlayer user = Samurai.getInstance().getPlayerHandler().loadPlayer(uuid, true);
        if (user == null) return;

        List<KitCooldown> cooldowns = user.getKitCooldowns();

        cooldowns.add(KitCooldown.builder()
                .kit(kit.getName())
                .addedAt(System.currentTimeMillis())
                .duration(kit.getCooldown())
                .build());
        user.save(true);
    }

    public Optional<KitCooldown> getCooldown(UUID uuid, String kitName) {
        SamuraiPlayer user = Samurai.getInstance().getPlayerHandler().loadPlayer(uuid, true);
        if (user == null) return Optional.empty();

        List<KitCooldown> cooldowns = user.getKitCooldowns();

        return cooldowns.stream().filter(cooldown -> cooldown.getKit().equals(kitName)).findFirst();
    }

}
