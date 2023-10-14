package dev.lbuddyboy.samurai.map.game.impl.spleef;

import dev.lbuddyboy.samurai.map.game.Game;
import dev.lbuddyboy.samurai.map.game.GameState;
import dev.lbuddyboy.samurai.map.game.GameType;
import dev.lbuddyboy.samurai.map.game.arena.GameArena;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.InventoryUtils;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import mkremins.fanciful.FancyMessage;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SpleefGame extends Game {

    public SpleefGame(UUID host, List<GameArena> arenaOptions) {
        super(host, GameType.SPLEEF, arenaOptions);
    }

    @Override
    public void startGame() {
        super.startGame();

        // just in case whoever made the arena forgets to set bounds, null check so no npe
        if (getVotedArena().getBounds() != null) {
            getVotedArena().createSnapshot();
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (state == GameState.ENDED) {
                    cancel();
                    return;
                }

                if (state == GameState.RUNNING) {
                    startSpleef();
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(Samurai.getInstance(), 10L, 10L);
    }

    @Override
    public void endGame() {
        super.endGame();

        // another null check
        if (getVotedArena().getBounds() != null) {
            // restore arena after everyone is teleported out
            Bukkit.getScheduler().runTaskLater(Samurai.getInstance(), getVotedArena()::restoreSnapshot, 5 * 20L);
        }
    }

    private void startSpleef() {
        Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> {
            for (Player player : getPlayers()) {
                InventoryUtils.resetInventoryNow(player);
                player.getInventory().setItem(0, ItemBuilder.of(Material.DIAMOND_SHOVEL).enchant(Enchantment.DIG_SPEED, 5).build());
            }

            // split players into 2 groups and tp both to separate spawn point
            int midIndex = (getPlayers().size() - 1) / 2;
            List<List<Player>> split = new ArrayList<>(
                    getPlayers().stream()
                            .collect(Collectors.partitioningBy(s -> getPlayers().indexOf(s) > midIndex))
                            .values()
            );

            split.get(0).forEach(player -> player.teleport(getVotedArena().getPointA()));
            split.get(1).forEach(player -> player.teleport(getVotedArena().getPointB()));

        });
        setStartedAt(System.currentTimeMillis());
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
                    sendMessages(ChatColor.YELLOW + "Spleef event has begun!");
                    sendSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 2F);
                } else {
                    sendMessages(ChatColor.YELLOW + "Spleef event will begin in " + CC.MAIN + i + ChatColor.YELLOW + " second" + (i == 1 ? "" : "s") + "...");
                    sendSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 1F);
                }

                if (i <= 0) {
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(Samurai.getInstance(), 20L, 20L);
    }

    @Override
    public void eliminatePlayer(Player player, Player killer) {
        super.eliminatePlayer(player, killer);
        addSpectator(player);

        if (players.size() == 1) {
            endGame();
        }
    }

    public double getDeathHeight() {
        return Math.min(getVotedArena().getPointA().getBlockY(), getVotedArena().getPointB().getBlockY()) - 2.9;
    }

    @Override
    public Player findWinningPlayer() {
        if (players.size() == 1) {
            return Bukkit.getPlayer(players.iterator().next());
        }

        return null;
    }

    @Override
    public List<String> getScoreboardLines(Player player) {
        List<String> lines = new ArrayList<>();
        List<Object> replacements = new ArrayList<>(List.of(
                "%players%", players.size(),
                "%max-players%", getMaxPlayers(),
                "%started-with%", getStartedWith()
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
            lines.addAll(CC.translate(getConfig().getStringList("scoreboard-lines.running"), replacements.toArray(new Object[0])));
        } else {
            replacements.addAll(List.of(
                    "%winner%", (winningPlayer == null ? "None" : winningPlayer.getName())
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
                        .then("█████").color(ChatColor.AQUA)
                        .then("██").color(ChatColor.GRAY),
                new FancyMessage("")
                        .then("██").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.AQUA)
                        .then("██████").color(ChatColor.GRAY)
                        .then(CC.translate(" &g&l" + getGameType().getDisplayName() + " Event")),
                new FancyMessage("")
                        .then("██").color(ChatColor.GRAY)
                        .then("█████").color(ChatColor.AQUA)
                        .then("██").color(ChatColor.GRAY)
                        .then(" Hosted by ").color(ChatColor.GRAY)
                        .then(getHostName()).color(ChatColor.AQUA),
                new FancyMessage("")
                        .then("██████").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.AQUA)
                        .then("██").color(ChatColor.GRAY)
                        .then(" [").color(ChatColor.GRAY)
                        .then("Click to join event").color(ChatColor.YELLOW)
                        .command("/game join")
                        .formattedTooltip(new FancyMessage("Click here to join the event.").color(ChatColor.YELLOW))
                        .then("]").color(ChatColor.GRAY),
                new FancyMessage("")
                        .then("██").color(ChatColor.GRAY)
                        .then("█████").color(ChatColor.AQUA)
                        .then("██").color(ChatColor.GRAY),
                new FancyMessage("█████████").color(ChatColor.GRAY)
        );
    }
}
