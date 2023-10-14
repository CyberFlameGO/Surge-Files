package dev.lbuddyboy.bunkers;

import co.aikar.commands.PaperCommandManager;
import dev.lbuddyboy.bunkers.command.GameCommand;
import dev.lbuddyboy.bunkers.command.claim.ClaimCommand;
import dev.lbuddyboy.bunkers.command.context.ACFTeamType;
import dev.lbuddyboy.bunkers.command.context.ACFUUIDType;
import dev.lbuddyboy.bunkers.game.GameHandler;
import dev.lbuddyboy.bunkers.game.pearl.EnderpearlCooldownHandler;
import dev.lbuddyboy.bunkers.game.pearl.command.ClearTimerCommand;
import dev.lbuddyboy.bunkers.listener.LobbyListener;
import dev.lbuddyboy.bunkers.listener.PreventionListener;
import dev.lbuddyboy.bunkers.mongo.MongoHandler;
import dev.lbuddyboy.bunkers.nametag.FrozenNametagHandler;
import dev.lbuddyboy.bunkers.nametag.impl.BunkersNametagProvider;
import dev.lbuddyboy.bunkers.scoreboard.BunkersScoreboard;
import dev.lbuddyboy.bunkers.scoreboard.assemble.Assemble;
import dev.lbuddyboy.bunkers.scoreboard.assemble.AssembleStyle;
import dev.lbuddyboy.bunkers.spectator.SpectatorHandler;
import dev.lbuddyboy.bunkers.team.Team;
import dev.lbuddyboy.bunkers.team.TeamHandler;
import dev.lbuddyboy.bunkers.team.command.LocationCommand;
import dev.lbuddyboy.bunkers.team.command.TeamManageCommand;
import dev.lbuddyboy.bunkers.util.bukkit.CC;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 19/03/2022 / 6:56 PM
 * SteelBunkers / com.steelpvp.bunkers
 */

@Getter
public class Bunkers extends JavaPlugin {

	@Getter
	private static Bunkers instance;
	public static final String PREFIX = CC.translate("&6&l[BUNKERS] &f");

	private PaperCommandManager commandManager;
	private MongoHandler mongoHandler;
	private TeamHandler teamHandler;
	private GameHandler gameHandler;
	private SpectatorHandler spectatorHandler;
	private Assemble assemble;

	@Override
	public void onEnable() {
		instance = this;

		this.saveDefaultConfig();

		this.registerHandlers();
		this.registerCommands();
		this.registerListeners();
		this.registerScoreboard();

	}

	@Override
	public void onDisable() {
		this.assemble.cleanup();
		this.gameHandler.end(true);
	}

	private void registerScoreboard() {
		FrozenNametagHandler.init();
		FrozenNametagHandler.registerProvider(new BunkersNametagProvider());
		this.assemble = new Assemble(this, new BunkersScoreboard());
		this.assemble.setTicks(4);
		this.assemble.setAssembleStyle(AssembleStyle.KOHI);
	}

	private void registerListeners() {
		this.getServer().getPluginManager().registerEvents(new LobbyListener(), this);
		this.getServer().getPluginManager().registerEvents(new PreventionListener(), this);
		this.getServer().getPluginManager().registerEvents(new EnderpearlCooldownHandler(), this);
	}

	private void registerHandlers() {
		this.commandManager = new PaperCommandManager(this);
		this.mongoHandler = new MongoHandler();
		this.teamHandler = new TeamHandler();
		this.gameHandler = new GameHandler();
		this.spectatorHandler = new SpectatorHandler();
	}

	private void registerCommands() {
		commandManager.getCommandCompletions().registerCompletion("team", c -> {
			List<String> teams = new ArrayList<>();
			for (Team team : this.teamHandler.getTeams().values()) {
				teams.add(team.getName());
			}

			if (teams.isEmpty()) {
				teams.add("No teams available");
			}

			return teams;
		});
		commandManager.getCommandContexts().registerContext(UUID.class, new ACFUUIDType());
		commandManager.getCommandContexts().registerContext(Team.class, new ACFTeamType());

		this.commandManager.registerCommand(new GameCommand());
		this.commandManager.registerCommand(new ClaimCommand());
		this.commandManager.registerCommand(new TeamManageCommand());
		this.commandManager.registerCommand(new LocationCommand());
		this.commandManager.registerCommand(new ClearTimerCommand());
	}

}
