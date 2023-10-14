package dev.lbuddyboy.hub;

import co.aikar.commands.PaperCommandManager;
import dev.lbuddyboy.hub.command.HistoryCommand;
import dev.lbuddyboy.hub.command.PvPModeCommand;
import dev.lbuddyboy.hub.command.StatsCommand;
import dev.lbuddyboy.hub.command.lHubCommand;
import dev.lbuddyboy.hub.config.DocHandler;
import dev.lbuddyboy.hub.general.GeneralSettingsHandler;
import dev.lbuddyboy.hub.item.ItemHandler;
import dev.lbuddyboy.hub.item.ItemListener;
import dev.lbuddyboy.hub.item.command.ItemCommand;
import dev.lbuddyboy.hub.listener.BungeeListener;
import dev.lbuddyboy.hub.listener.HubListeners;
import dev.lbuddyboy.hub.listener.JoinListener;
import dev.lbuddyboy.hub.menu.CustomMenuHandler;
import dev.lbuddyboy.hub.placeholder.PlaceholderHandler;
import dev.lbuddyboy.hub.placeholder.papi.lHubPAPI;
import dev.lbuddyboy.hub.queue.QueueHandler;
import dev.lbuddyboy.hub.rank.RankCoreHandler;
import dev.lbuddyboy.hub.scoreboard.HubScoreboard;
import dev.lbuddyboy.hub.scoreboard.ScoreboardHandler;
import dev.lbuddyboy.hub.util.menu.ButtonListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 22/09/2021 / 1:30 AM
 * LBuddyBoy-Development / me.lbuddyboy.hub
 */

@Getter
public class lHub extends JavaPlugin {

	@Getter public static lHub instance;

	private List<lModule> modules;

	private DocHandler docHandler;
	private RankCoreHandler rankCoreHandler;
	private QueueHandler queueHandler;
	private ItemHandler itemHandler;
	private CustomMenuHandler customMenuHandler;
	private GeneralSettingsHandler settingsHandler;
	private PaperCommandManager commandManager;
	private PlaceholderHandler placeholderHandler;
	private ScoreboardHandler scoreboardHandler;

	@Override
	public void onEnable() {
		instance = this;
		saveDefaultConfig();

		this.loadListeners();
		this.loadHandlers();
		this.loadCommands();
		this.loadBungee();
		new lHubPAPI(this).register();
	}

	private void loadCommands() {
		this.commandManager.registerCommand(new HistoryCommand());
		this.commandManager.registerCommand(new StatsCommand());
		this.commandManager.registerCommand(new lHubCommand());
		this.commandManager.registerCommand(new PvPModeCommand());
		this.commandManager.registerCommand(new ItemCommand());
	}

	private void loadBungee() {
		Bukkit.getScheduler().runTask(this, () -> {
			boolean jail = false;
			for (HubScoreboard scoreboard : scoreboardHandler.getScoreboards()) {
				if (scoreboard.getTitle().getActiveFrame().toLowerCase().contains("jail")) {
					jail = true;
				}
			}
			if (!jail) {
				getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
			}
			getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeListener());
			new BungeeListener().start();
		});
	}

	private void loadListeners() {
		List<Listener> listeners = Arrays.asList(
				new ItemListener(),
				new HubListeners(),
				new ButtonListener(),
				new JoinListener()
		);
		listeners.forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
	}

	private void loadHandlers() {
		this.commandManager = new PaperCommandManager(this);
		this.modules = new ArrayList<>();
		this.modules.addAll(Arrays.asList(
				this.docHandler = new DocHandler(),
				this.placeholderHandler = new PlaceholderHandler(),
				this.scoreboardHandler = new ScoreboardHandler(),
				this.rankCoreHandler = new RankCoreHandler(),
				this.settingsHandler = new GeneralSettingsHandler(),
				this.queueHandler = new QueueHandler(),
				this.itemHandler = new ItemHandler(),
				this.customMenuHandler = new CustomMenuHandler()
		));
		this.modules.forEach(lModule -> lModule.load(this));
	}

}
