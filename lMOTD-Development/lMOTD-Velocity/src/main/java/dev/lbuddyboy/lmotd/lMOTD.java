package dev.lbuddyboy.lmotd;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.lbuddyboy.lmotd.command.GListCommand;
import dev.lbuddyboy.lmotd.command.HubCommand;
import dev.lbuddyboy.lmotd.command.MOTDTimerCommand;
import dev.lbuddyboy.lmotd.listener.MOTDListener;
import dev.lbuddyboy.lmotd.model.MOTDTimer;
import dev.lbuddyboy.lmotd.util.config.TextFile;
import lombok.Getter;
import lombok.Setter;
import org.simpleyaml.configuration.ConfigurationSection;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Plugin(
        id = "lmotd",
        name = "lMOTD",
        version = "1.0",
        authors = {"LBuddyBoy"}
)
@Getter
public class lMOTD {

    @Getter private static lMOTD instance;

    private final ProxyServer server;
    private final Logger logger;
    private final Path path;

    @Setter private MOTDTimer activeTimer;
    private TextFile configYML;
    @Setter private MOTD activeTimerMOTD;
    private List<MOTD> motds;

    @Inject
    public lMOTD(ProxyServer server, Logger logger, @DataDirectory Path path) {
        this.server = server;
        this.logger = logger;
        this.path = path;
    }

    @Subscribe
    public void onIntialize(ProxyInitializeEvent event) {
        instance = this;

        this.loadListeners();
        this.loadCommands();
        this.loadConfigs();
        this.loadMOTDS();
    }

    private void loadListeners() {
        this.server.getEventManager().register(this, new MOTDListener());
    }

    private void loadCommands() {
        server.getCommandManager().register(server.getCommandManager().metaBuilder("motdtimer").build(), new MOTDTimerCommand());
        server.getCommandManager().register(server.getCommandManager().metaBuilder("hub").build(), new HubCommand());
        server.getCommandManager().register(server.getCommandManager().metaBuilder("glist").build(), new GListCommand());
    }

    private void loadConfigs() {
        this.configYML = new TextFile(this.path, "config.yml");
    }

    private void loadMOTDS() {
        this.motds = new ArrayList<>();
        activeTimerMOTD = new MOTD(this.configYML.getConfig().getStringList("motd-active-timer.legacy-lines"), this.configYML.getConfig().getStringList("motd-active-timer.new-lines"));

        ConfigurationSection section = this.configYML.getConfig().getConfigurationSection("motds");
        for (String key : section.getKeys(false)) {
            List<String> legacy = section.getStringList(key + ".legacy-lines");
            List<String> newLines = section.getStringList(key + ".new-lines");

            this.motds.add(new MOTD(legacy, newLines));
        }
    }

}
