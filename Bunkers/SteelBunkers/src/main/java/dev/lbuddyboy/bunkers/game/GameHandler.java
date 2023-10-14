package dev.lbuddyboy.bunkers.game;

import dev.lbuddyboy.bunkers.Bunkers;
import dev.lbuddyboy.bunkers.event.GameEndEvent;
import dev.lbuddyboy.bunkers.game.koth.KoTHHandler;
import dev.lbuddyboy.bunkers.game.listener.GameListener;
import dev.lbuddyboy.bunkers.game.ore.OreBreak;
import dev.lbuddyboy.bunkers.game.task.GameTask;
import dev.lbuddyboy.bunkers.game.user.GameUser;
import dev.lbuddyboy.bunkers.scoreboard.BunkersScoreboard;
import dev.lbuddyboy.bunkers.team.Team;
import dev.lbuddyboy.bunkers.util.ItemBuilder;
import dev.lbuddyboy.bunkers.util.WorldUtils;
import dev.lbuddyboy.communicate.BunkersCom;
import dev.lbuddyboy.communicate.BunkersGame;
import dev.lbuddyboy.communicate.FinalGame;
import dev.lbuddyboy.communicate.database.redis.packets.BunkersUpdatePacket;
import dev.lbuddyboy.communicate.profile.Profile;
import lombok.Data;
import dev.lbuddyboy.bunkers.util.bukkit.CC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 19/03/2022 / 7:14 PM
 * SteelBunkers / com.steelpvp.bunkers.game
 */

@Data
public class GameHandler {

	private static final int PLAYERS_NEEDED_TO_START = 20;
	private static final int MAX_PLAYERS_PER_TEAM = 5;

	private final GameSettings gameSettings;
	/* Just a list of locations because they can't really place blocks where there isn't air, unless it's grass or something like that */
	private final List<Location> placedBlocks;
	private final List<OreBreak> oreBreaks;
	private final Map<UUID, Integer> deathsMap, killsMap, oresMinedMap;
	private final Map<UUID, Long> lastJoinedMap;
	private final Map<UUID, Double> balanceMap;
	private final KoTHHandler koTHHandler;

	private Team winner;
	private GameTask task;
	private GameState state;
	private long startedAt;
	private boolean kothActivated;
	private boolean ended;

	public GameHandler() {
		this.gameSettings = new GameSettings();
		this.placedBlocks = new ArrayList<>();
		this.oreBreaks = new ArrayList<>();
		this.deathsMap = new ConcurrentHashMap<>();
		this.killsMap = new ConcurrentHashMap<>();
		this.oresMinedMap = new ConcurrentHashMap<>();
		this.lastJoinedMap = new ConcurrentHashMap<>();
		this.balanceMap = new ConcurrentHashMap<>();
		this.koTHHandler = new KoTHHandler();

		this.state = GameState.WAITING;

		Bunkers.getInstance().getServer().getPluginManager().registerEvents(new GameListener(), Bunkers.getInstance());

		Bukkit.getScheduler().runTaskTimer(Bunkers.getInstance(), (task) -> {
			if (this.state != GameState.WAITING) task.cancel();

			List<Player> players = Bukkit.getOnlinePlayers().stream().filter(p -> !p.hasMetadata("spectator") && !p.hasMetadata("modmode")).collect(Collectors.toList());
			if (players.size() >= PLAYERS_NEEDED_TO_START) {
				start(60);
			}
		}, 20, 20);

		Bukkit.getScheduler().runTaskTimerAsynchronously(Bunkers.getInstance(), (task) -> {
			if (ended) return;
			List<Player> players = Bukkit.getOnlinePlayers().stream().filter(p -> !p.hasMetadata("spectator") && !p.hasMetadata("modmode")).collect(Collectors.toList());

			List<dev.lbuddyboy.communicate.Team> teams = new ArrayList<>();
			for (Team team : Bunkers.getInstance().getTeamHandler().getTeams().values()) {
				teams.add(new dev.lbuddyboy.communicate.Team(team.getColor(), team.getMembers().size() + 1, team.getDtr()));
			}

			BunkersGame game = new BunkersGame(
					Bunkers.getInstance().getConfig().getString("server-name"),
					players.size(),
					this.startedAt,
					teams,
					this.ended,
					!canKoTHActivate(),
					koTHHandler.getRemaining(),
					dev.lbuddyboy.communicate.GameState.valueOf(this.state.name())

			);
			BunkersCom.getInstance().getBunkersGames().removeIf(bunkersGame -> bunkersGame.getName().equals(Bunkers.getInstance().getConfig().getString("server-name")));
			BunkersCom.getInstance().getBunkersGames().add(game);
			new BunkersUpdatePacket(BunkersCom.getInstance().getBunkersGames()).send();

		}, 20, 20);

		Bukkit.getScheduler().runTaskTimer(Bunkers.getInstance(), (task) -> {

			List<OreBreak> toRemove = new ArrayList<>();
			for (OreBreak oreBreak : this.oreBreaks) {
				if (oreBreak.getBrokeAt() < System.currentTimeMillis()) {
					oreBreak.getLocation().getBlock().setType(oreBreak.getMaterial());
					toRemove.add(oreBreak);
				}
			}
			toRemove.forEach(oreBreaks::remove);

			if (getState() != GameState.ACTIVE) return;

			if (this.isGracePeriod()) return;

			/*
			Check if there's one team alive still, if so. End the game and announce them as the winner.
			 */

			if (this.koTHHandler.isFinished() && this.koTHHandler.getWinner() != null) {
				task.cancel();
				this.winner = Bunkers.getInstance().getTeamHandler().getTeam(this.koTHHandler.getWinner());
				end(false);
				return;
			}

			if (Bunkers.getInstance().getTeamHandler().getAliveTeams().size() == 1) {
				this.winner = Bunkers.getInstance().getTeamHandler().getAliveTeams().get(0);
				end(false);
				task.cancel();
			}

			if (Bunkers.getInstance().getTeamHandler().getAliveTeams().size() == 0) {
				end(false);
				task.cancel();
			}

			if (!kothActivated && canKoTHActivate()) {
				setKothActivated(true);
			}

		}, 20, 20);
	}

	public void start(int seconds) {
		if (this.task != null) {
			this.task.cancel();
		}
		this.task = new GameTask(seconds);
		this.task.start();
		this.setState(GameState.COUNTING);
	}

	public void end(boolean skip) {

		BunkersCom.getInstance().getBunkersGames().removeIf(bunkersGame -> bunkersGame.getName().equals(Bunkers.getInstance().getConfig().getString("server-name")));
		new BunkersUpdatePacket(BunkersCom.getInstance().getBunkersGames()).send();

		Bukkit.getPluginManager().callEvent(new GameEndEvent());

		for (Team team : Bunkers.getInstance().getTeamHandler().getTeams().values()) {
			for (UUID uuid : team.getMembers()) {
				Profile profile = BunkersCom.getInstance().getProfileHandler().getByUUID(uuid);
				GameUser user = getGameUser(uuid);

				if (profile == null) continue;

				FinalGame finalGame = new FinalGame();

				finalGame.setServer(Bunkers.getInstance().getConfig().getString("server-name"));
				finalGame.setKills(user.getKills());
				finalGame.setDeaths(user.getDeaths());
				finalGame.setStartedAt(getStartedAt());
				finalGame.setEndedAt(System.currentTimeMillis());
				finalGame.setTeam(team.getDisplay());
				finalGame.setWinner((winner != null ? winner.getDisplay() : "No one"));

				profile.getGameHistory().add(finalGame);
				profile.setOresMined(profile.getOresMined() + user.getOresMined());
				profile.setKills(profile.getKills() + user.getKills());
				profile.setDeaths(profile.getDeaths() + user.getDeaths());
				if (finalGame.getWinner().equals(team.getDisplay())) {
					profile.setWins(profile.getWins() + 1);
				}

				profile.save();
			}
		}

		this.task.cancel();
		this.task = null;
		this.setState(GameState.ENDING);

		if (this.winner != null) {
			Bukkit.broadcastMessage(CC.translate(Bunkers.PREFIX + "" + this.winner.getDisplay() + " &fhas just &awon&f won the Bunkers game!!!"));
			Bukkit.broadcastMessage(CC.translate(Bunkers.PREFIX + "" + this.winner.getDisplay() + " &fhas just &awon&f won the Bunkers game!!!"));
			Bukkit.broadcastMessage(CC.translate(Bunkers.PREFIX + "" + this.winner.getDisplay() + " &fhas just &awon&f won the Bunkers game!!!"));

			for (Player player : this.winner.getOnlineMembers()) {
				for (int i = 0; i < 15; i++) {
					player.getWorld().spawn(player.getLocation(), Firework.class);
				}
			}

			for (Player player : Bukkit.getOnlinePlayers()) {
				player.sendTitle(CC.translate("&x&0&4&4&5&f&b&lWINNER"), this.winner.getDisplay());
				player.getInventory().clear();
				if (winner != null) {
					for (int i = 0; i < 9; i++) {
						player.getInventory().setItem(i, ItemBuilder.of(Material.valueOf(winner.getColor().name() + "_WOOL")).name(winner.getColor()+ "Winner: " + winner.getName()).build());
					}
				}
			}

		}

		Bukkit.broadcastMessage(CC.translate(" "));
		Bukkit.broadcastMessage(CC.translate("&g&lLeaderboards &7(Kills)"));
		Bukkit.broadcastMessage(CC.translate(" "));
		List<GameUser> users = Bunkers.getInstance().getGameHandler().getKillsMap().keySet().stream().map(entry -> Bunkers.getInstance().getGameHandler().getGameUser(entry)).sorted(Comparator.comparingInt(GameUser::getKills)).collect(Collectors.toList());
		Collections.reverse(users);

		int i = 1;
		for (GameUser user : users) {
			if (i > 4) continue;
			Bukkit.broadcastMessage(CC.translate(" &f● &e#" + i + " &g" + Bukkit.getOfflinePlayer(user.getUuid()).getName() + "&f: &c" + user.getKills()));
			i++;
		}

		Bukkit.broadcastMessage(CC.translate(Bunkers.PREFIX + "&cThe Bunkers server will shutdown in 30 seconds..."));

		if (skip) {
			reset();
			return;
		}

		Bukkit.getScheduler().runTaskLater(Bunkers.getInstance(), () -> {
			reset();
			Bukkit.getScheduler().runTaskLater(Bunkers.getInstance(), Bukkit::shutdown, 20 * 15);
		}, 20 * 15);

		this.ended = true;
	}

	public GameUser getGameUser(UUID uuid) {
		return new GameUser(uuid, getKillsMap().getOrDefault(uuid, 0), getDeathsMap().getOrDefault(uuid, 0), getOresMinedMap().getOrDefault(uuid, 0), getBalanceMap().getOrDefault(uuid, 250.0), getLastJoinedMap().getOrDefault(uuid, 0L));
	}

	public boolean isGracePeriod() {
		return this.startedAt + 60_000L > System.currentTimeMillis();
	}

	public void reset() {
		for (Location location : this.getPlacedBlocks()) {
			location.getBlock().setType(Material.AIR);
		}

		WorldUtils.butcher();

		for (Team team : Bunkers.getInstance().getTeamHandler().getTeams().values()) {
			for (Map.Entry<Material, List<Location>> entry : team.getLocations().entrySet()) {
				for (Location location : entry.getValue()) {
					location.getBlock().setType(entry.getKey());
				}
			}
		}
	}

	public boolean canKoTHActivate() {
		return this.startedAt + BunkersScoreboard.KOTH_START_MILLIS > System.currentTimeMillis();
	}

}
