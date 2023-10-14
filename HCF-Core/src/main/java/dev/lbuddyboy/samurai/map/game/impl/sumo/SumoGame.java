package dev.lbuddyboy.samurai.map.game.impl.sumo;

import dev.lbuddyboy.samurai.map.game.Game;
import dev.lbuddyboy.samurai.map.game.GameState;
import dev.lbuddyboy.samurai.map.game.GameType;
import dev.lbuddyboy.samurai.map.game.GameUtils;
import dev.lbuddyboy.samurai.map.game.arena.GameArena;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.InventoryUtils;
import dev.lbuddyboy.samurai.util.object.ItemBuilder;
import lombok.Getter;
import mkremins.fanciful.FancyMessage;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Getter
public class SumoGame extends Game {

	private Player playerA;
	private Player playerB;
	private Map<UUID, Integer> roundsPlayed = new HashMap<>();
	private int currentRound = 0;
	private static ItemStack KB_ITEM = new ItemBuilder(Material.BLAZE_ROD, 1)
			.displayName(CC.translate("&6&lSUMO KB"))
			.enchant(Enchantment.KNOCKBACK, 1)
			.build();

	public SumoGame(UUID host, List<GameArena> arenaOptions) {
		super(host, GameType.SUMO, arenaOptions);
	}

	@Override
	public void startGame() {
		super.startGame();

		new BukkitRunnable() {
			@Override
			public void run() {
				if (state == GameState.ENDED) {
					cancel();
					return;
				}

				if (state == GameState.RUNNING) {
					determineNextPlayers();
					startRound();
					cancel();
				}
			}
		}.runTaskTimerAsynchronously(Samurai.getInstance(), 10L, 10L);
	}

	@Override
	public void gameBegun() {
		Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> {
			for (Player player : getPlayers()) {
				player.teleport(getVotedArena().getSpectatorSpawn());
			}
		});
	}

	public void startRound() {
		if (playerA == null || playerB == null) {
			throw new IllegalStateException("Cannot start round without both players");
		}

		currentRound++;
		setStartedAt(System.currentTimeMillis());

		Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> {
			InventoryUtils.resetInventoryNow(playerA);
			playerA.teleport(getVotedArena().getPointA());
			playerA.getInventory().setItem(0, KB_ITEM);
			playerA.getInventory().setHeldItemSlot(0);

			InventoryUtils.resetInventoryNow(playerB);
			playerB.teleport(getVotedArena().getPointB());
			playerB.getInventory().setItem(0, KB_ITEM);
			playerB.getInventory().setHeldItemSlot(0);
		});

		new BukkitRunnable() {
			private int i = 6;

			@Override
			public void run() {
				if (state == GameState.ENDED) {
					cancel();
					return;
				}

				i--;

				if (i == 0) {
					sendMessages(ChatColor.YELLOW + "The round has started!");
					sendSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 2F);
				} else {
					sendMessages(ChatColor.YELLOW + "The round is starting in " + CC.MAIN + i + ChatColor.YELLOW + " second" + (i == 1 ? "" : "s") + "...");
					sendSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 1F);
				}

				if (i <= 0) {
					cancel();
				}
			}
		}.runTaskTimerAsynchronously(Samurai.getInstance(), 20L, 20L);
	}

	public void endRound() {
		if (players.size() == 1) {
			endGame();
		} else {
			if (playerA != null) {
				playerA.teleport(getVotedArena().getSpectatorSpawn());
				GameUtils.resetPlayer(playerA);
				playerA = null;
			}

			if (playerB != null) {
				playerB.teleport(getVotedArena().getSpectatorSpawn());
				GameUtils.resetPlayer(playerB);
				playerB = null;
			}

			Bukkit.getServer().getScheduler().runTaskLater(Samurai.getInstance(), () -> {
				determineNextPlayers();
				startRound();
			}, 50L);
		}
	}

	@Override
	public void eliminatePlayer(Player player, Player killer) {
		if (state == GameState.ENDED) return;

		super.eliminatePlayer(player, killer);

		if (killer != null) {
			sendMessages(CC.MAIN + player.getName() + ChatColor.YELLOW + " has been eliminated by " + CC.MAIN + killer.getName() + ChatColor.YELLOW + "! " + ChatColor.GRAY + "(" + getPlayers().size() + "/" + getStartedWith() + " players remaining)");
		}

		if (isCurrentlyFighting(player)) {
			if (playerA.getUniqueId() == player.getUniqueId()) {
				playerA = null;
			} else if (playerB.getUniqueId() == player.getUniqueId()) {
				playerB = null;
			}

			endRound();
		}
	}

	public boolean isCurrentlyFighting(Player player) {
		return (playerA != null && playerA.getUniqueId() == player.getUniqueId()) || (playerB != null && playerB.getUniqueId() == player.getUniqueId());
	}

	public Player getOpponent(Player player) {
		if (playerA != null && playerA.getUniqueId() == player.getUniqueId()) {
			return playerB;
		}

		if (playerB != null && playerB.getUniqueId() == player.getUniqueId()) {
			return playerA;
		}

		return null;
	}

	public void determineNextPlayers() {
		if (players.size() < 2) return;
		List<Player> players = getPlayers().stream().sorted(Comparator.comparingInt(player -> roundsPlayed.getOrDefault(player.getUniqueId(), 0))).collect(Collectors.toList());

		playerA = players.get(0);
		playerB = players.get(1);

		roundsPlayed.putIfAbsent(playerA.getUniqueId(), 0);
		roundsPlayed.put(playerA.getUniqueId(), roundsPlayed.get(playerA.getUniqueId()) + 1);

		roundsPlayed.putIfAbsent(playerB.getUniqueId(), 0);
		roundsPlayed.put(playerB.getUniqueId(), roundsPlayed.get(playerB.getUniqueId()) + 1);

		sendMessages(CC.MAIN + ChatColor.BOLD + "Next round: " + ChatColor.RESET + playerA.getName() + ChatColor.GRAY + " vs. " + ChatColor.RESET + playerB.getName());
	}

	public double getDeathHeight() {
		return Math.min(getVotedArena().getPointA().getBlockY(), getVotedArena().getPointB().getBlockY()) - 2.9;
	}

	@Override
	public void handleDamage(Player victim, Player damager, EntityDamageByEntityEvent event) {
		if (state == GameState.RUNNING) {
			if (isPlaying(victim.getUniqueId()) && isPlaying(damager.getUniqueId())) {
				event.setDamage(0.0);

				if (!isCurrentlyFighting(victim) || !isCurrentlyFighting(damager)) {
					event.setCancelled(true);
				} else {
					victim.setHealth(victim.getMaxHealth());
					victim.updateInventory();
				}
			} else {
				event.setCancelled(true);
			}
		} else {
			event.setCancelled(true);
		}
	}

	@Override
	public Player findWinningPlayer() {
		return playerA == null ? playerB : playerA;
	}

	@Override
	public List<String> getScoreboardLines(Player player) {
		List<String> lines = new ArrayList<>();
		List<Object> replacements = new ArrayList<>(List.of(
				"%players%", players.size(),
				"%max-players%", getMaxPlayers(),
				"%started-with%", getStartedWith(),
				"%current-round%", currentRound

		));

		if (state == GameState.WAITING) {
			lines.addAll(CC.translate(getConfig().getStringList("scoreboard-lines.waiting.global"), replacements.toArray(new Object[0])));

			if (getVotedArena() != null) {
				replacements.addAll(List.of(
						"%voted-map%", getVotedArena().getName()
				));
				lines.addAll(CC.translate(getConfig().getStringList("scoreboard-lines.waiting.voted"), replacements.toArray(new Object[0])));

			} else {
				int i = 1;
				for (Map.Entry<GameArena, AtomicInteger> entry : getArenaOptions().entrySet().stream().sorted((o1, o2) -> o2.getValue().get()).toList()) {
					replacements.addAll(List.of(
							"%map-" + i + "%", (getPlayerVotes().getOrDefault(player.getUniqueId(), null) == entry.getKey() ? "&l" : "") + entry.getKey().getName(),
							"%map-" + i++ + "-votes%", entry.getValue().get()
					));
				}
				lines.addAll(CC.translate(getConfig().getStringList("scoreboard-lines.waiting.voting"), replacements.toArray(new Object[0])));
			}

			if (getStartedAt() == null) {
				int playersNeeded = getGameType().getMinPlayers() - getPlayers().size();
				replacements.addAll(List.of(
						"%players-needed-no-player%", playersNeeded + " player" + (playersNeeded == 1 ? "" : "s"),
						"%players-needed%", playersNeeded + " player" + (playersNeeded == 1 ? "" : "s")
				));
				lines.addAll(CC.translate(getConfig().getStringList("scoreboard-lines.waiting.starting-waiting"), replacements.toArray(new Object[0])));
			} else {
				float remainingSeconds = (getStartedAt() - System.currentTimeMillis()) / 1000F;
				replacements.addAll(List.of(
						"%starting-in%", ((double) Math.round(10.0D * (double) remainingSeconds) / 10.0D)
				));
				lines.addAll(CC.translate(getConfig().getStringList("scoreboard-lines.waiting.starting-players-met"), replacements.toArray(new Object[0])));
			}
		} else if (state == GameState.RUNNING) {
			lines.addAll(CC.translate(getConfig().getStringList("scoreboard-lines.running.global"), replacements.toArray(new Object[0])));

			if (playerA != null && playerB != null) {
				lines.add("");

				replacements.addAll(List.of(
						"%playerA%", playerA.getName(),
						"%playerB%", playerB.getName()
				));

				final int namesLength = playerA.getName().length() + playerB.getName().length();
				if (namesLength <= 20) {
					lines.addAll(CC.translate(getConfig().getStringList("scoreboard-lines.running.normal"), replacements.toArray(new Object[0])));
				} else {
					lines.addAll(CC.translate(getConfig().getStringList("scoreboard-lines.running.name-too-long"), replacements.toArray(new Object[0])));

				}
			} else {
				lines.addAll(CC.translate(getConfig().getStringList("scoreboard-lines.running.selecting"), replacements.toArray(new Object[0])));
			}
		} else {
			replacements.addAll(List.of(
					"%winner%", (winningPlayer == null ? "None" : winningPlayer.getName()),
					"%current-round%", currentRound
			));
			lines.addAll(CC.translate(getConfig().getStringList("scoreboard-lines.finished"), replacements.toArray(new Object[0])));

		}
		return lines;
	}

	@Override
	public List<FancyMessage> createHostNotification() {
		return Arrays.asList(
				new FancyMessage("█████████").color(ChatColor.GRAY),
				new FancyMessage("")
						.then("██").color(ChatColor.GRAY)
						.then("█████").color(ChatColor.DARK_AQUA)
						.then("██").color(ChatColor.GRAY),
				new FancyMessage("")
						.then("██").color(ChatColor.GRAY)
						.then("█").color(ChatColor.DARK_AQUA)
						.then("██████").color(ChatColor.GRAY)
						.then(CC.MAIN + " " + getGameType().getDisplayName() + " Event"),
				new FancyMessage("")
						.then("██").color(ChatColor.GRAY)
						.then("█████").color(ChatColor.DARK_AQUA)
						.then("██").color(ChatColor.GRAY)
						.then(" Hosted by ").color(ChatColor.GRAY)
						.then(getHostName()).color(ChatColor.AQUA),
				new FancyMessage("")
						.then("██████").color(ChatColor.GRAY)
						.then("█").color(ChatColor.DARK_AQUA)
						.then("██").color(ChatColor.GRAY)
						.then(" [").color(ChatColor.GRAY)
						.then("Click to join event").color(ChatColor.YELLOW)
						.command("/game join")
						.formattedTooltip(new FancyMessage("Click here to join the event.").color(ChatColor.YELLOW))
						.then("]").color(ChatColor.GRAY),
				new FancyMessage("")
						.then("██").color(ChatColor.GRAY)
						.then("█████").color(ChatColor.DARK_AQUA)
						.then("██").color(ChatColor.GRAY),
				new FancyMessage("█████████").color(ChatColor.GRAY)
		);
	}

}
