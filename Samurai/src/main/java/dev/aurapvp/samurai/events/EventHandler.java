package dev.aurapvp.samurai.events;

import dev.aurapvp.samurai.events.command.KoTHCommand;
import dev.aurapvp.samurai.events.command.completions.KoTHCompletion;
import dev.aurapvp.samurai.events.command.contexts.KoTHContext;
import dev.aurapvp.samurai.events.koth.KoTH;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.util.Config;
import dev.aurapvp.samurai.util.IModule;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
public class EventHandler implements IModule {

    private Map<String, Event> events;
    private File kothsFolder;
    private Config eventsConfig;

    public EventHandler() {
        this.events = new HashMap<>();
    }

    @Override
    public String getId() {
        return "events";
    }

    @Override
    public void load(Samurai plugin) {
        this.loadCommands();
        this.loadConfig();
        this.loadDirectories();
        this.loadKoTHs();
    }

    @Override
    public void unload(Samurai plugin) {

    }

    @Override
    public void reload() {
        this.loadConfig();
        this.loadDirectories();
        this.loadKoTHs();
    }

    private void loadConfig() {
        this.eventsConfig = new Config(Samurai.getInstance(), "events");
    }

    private void loadDirectories() {
        this.kothsFolder = new File(Samurai.getInstance().getDataFolder(), "koths");

        if (!this.kothsFolder.exists()) this.kothsFolder.mkdir();
    }

    private void loadKoTHs() {
        for (String s : this.kothsFolder.list()) {
            String name = s.replaceAll(".yml", "");
            this.events.put(name, new KoTH(new Config(Samurai.getInstance(), name, this.kothsFolder)));
        }
    }

    private void loadCommands() {
        Samurai.getInstance().getCommandManager().getCommandCompletions().registerCompletion("koths", new KoTHCompletion());
        Samurai.getInstance().getCommandManager().getCommandContexts().registerContext(KoTH.class, new KoTHContext());
        Samurai.getInstance().getCommandManager().registerCommand(new KoTHCommand());
    }

}
