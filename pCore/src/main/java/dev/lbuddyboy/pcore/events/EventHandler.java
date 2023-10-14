package dev.lbuddyboy.pcore.events;

import dev.lbuddyboy.pcore.events.command.KoTHCommand;
import dev.lbuddyboy.pcore.events.command.completions.KoTHCompletion;
import dev.lbuddyboy.pcore.events.command.contexts.KoTHContext;
import dev.lbuddyboy.pcore.events.koth.KoTH;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.util.Config;
import dev.lbuddyboy.pcore.util.IModule;
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
    public void load(pCore plugin) {
        this.loadCommands();
        this.loadConfig();
        this.loadDirectories();
        this.loadKoTHs();
    }

    @Override
    public void unload(pCore plugin) {

    }

    @Override
    public void reload() {
        this.loadConfig();
        this.loadDirectories();
        this.loadKoTHs();
    }

    private void loadConfig() {
        this.eventsConfig = new Config(pCore.getInstance(), "events");
    }

    private void loadDirectories() {
        this.kothsFolder = new File(pCore.getInstance().getDataFolder(), "koths");

        if (!this.kothsFolder.exists()) this.kothsFolder.mkdir();
    }

    private void loadKoTHs() {
        for (String s : this.kothsFolder.list()) {
            String name = s.replaceAll(".yml", "");
            this.events.put(name, new KoTH(new Config(pCore.getInstance(), name, this.kothsFolder)));
        }
    }

    private void loadCommands() {
        pCore.getInstance().getCommandManager().getCommandCompletions().registerCompletion("koths", new KoTHCompletion());
        pCore.getInstance().getCommandManager().getCommandContexts().registerContext(KoTH.class, new KoTHContext());
        pCore.getInstance().getCommandManager().registerCommand(new KoTHCommand());
    }

}
