package dev.lbuddyboy.samurai.map.duel;

import dev.lbuddyboy.samurai.map.duel.arena.DuelArena;
import dev.lbuddyboy.samurai.map.kits.DefaultKit;
import dev.lbuddyboy.samurai.server.SpawnTagHandler;
import dev.lbuddyboy.samurai.util.ItemUtils;
import dev.lbuddyboy.samurai.util.modsuite.ModUtils;
import dev.lbuddyboy.samurai.util.modsuite.PlayerUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Getter
public class Duel {

    @NonNull private UUID player1;
    @NonNull private UUID player2;
    @NonNull private DuelArena arena;
    private final int wager;

    private DuelState state;
    private long startedAt = -1;
    private long endedAt = -1;

    private UUID winner;

    public void setup() {
        state = DuelState.COUNTDOWN;

        for (Player player : getPlayers()) {
            player.teleport(player.getUniqueId().equals(player1) ? arena.getPointA() : arena.getPointB());
            PlayerUtils.resetInventory(player, GameMode.SURVIVAL);

            DefaultKit pvpKit = Samurai.getInstance().getMapHandler().getKitManager().getDefaultKit("PvP");

            if (pvpKit != null) {
                pvpKit.apply(player);
            } else {
                player.sendMessage(ChatColor.RED + "Failed to find PvP kit.");
            }
        }

        new BukkitRunnable() {
            int timer = 5;

            @Override
            public void run() {
                if (state == DuelState.FINISHED) {
                    cancel();
                    return;
                }

                if (timer == 0) {
                    cancel();
                    playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 2F);
                    start();
                    return;
                }

                sendMessage(ChatColor.GRAY + "Match will begin in " + ChatColor.RED + timer + ChatColor.GRAY + " second" + (timer != 1 ? "s" : "") + "!");
                playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1F);
                timer--;
            }
        }.runTaskTimer(Samurai.getInstance(), 20L, 20L);
    }

    public void start() {
        state = DuelState.FIGHTING;
        startedAt = System.currentTimeMillis();

        sendMessage(ChatColor.GREEN + "The match has begun.");
    }

    public void end(Player winner) {
        //todo bootleg temp fixt
        if (state == DuelState.FINISHED) return;
        state = DuelState.FINISHED;
        endedAt = System.currentTimeMillis();

        this.winner = winner.getUniqueId();
        Player loser = Bukkit.getPlayer(getOpponent(winner.getUniqueId()));

        // give winner shards here
        Samurai.getInstance().getShardMap().addShards(winner.getUniqueId(), wager * 2); // multiply 2 so they get their shards back too

        getPlayers().forEach(player ->
                player.sendMessage(winner.getDisplayName() + ChatColor.GRAY + " won the duel for " + ChatColor.GREEN.toString() + ChatColor.BOLD + wager + " Shards" + ChatColor.GRAY + "!")
        );

        int winnerPots = ItemUtils.countStacksMatching(winner.getInventory().getContents(), ItemUtils.INSTANT_HEAL_POTION_PREDICATE);
        int loserPots = ItemUtils.countStacksMatching(loser.getInventory().getContents(), ItemUtils.INSTANT_HEAL_POTION_PREDICATE);

        winner.sendMessage(ChatColor.GREEN + "You won the duel against " + loser.getName() + "!");
        winner.sendMessage(ChatColor.GREEN + loser.getName() + " had " + loserPots + " potions left.");

        loser.sendMessage(ChatColor.RED + "You lost the duel against " + winner.getName() + "!");
        loser.sendMessage(ChatColor.GREEN + winner.getName() + " had " + winnerPots + " potions left.");

        Bukkit.getScheduler().runTaskLater(Samurai.getInstance(), this::finish, 3 * 20L);
    }

    private void finish() {
        DuelHandler duelHandler = Samurai.getInstance().getMapHandler().getDuelHandler();

        for (Player online : Bukkit.getOnlinePlayers()) {
            for (Player player : getPlayers()) {
                if (!ModUtils.isInvisible(online))
                    player.showPlayer(online);

                online.showPlayer(player);
            }
        }

        duelHandler.removeDuel(this);

        getPlayers().forEach(player -> {
            SpawnTagHandler.removeTag(player);
            PlayerUtils.resetInventory(player);
            player.teleport(Samurai.getInstance().getServerHandler().getSpawnLocation());
        });
    }

    public void disconnect(Player player) {
        end(Bukkit.getPlayer(getOpponent(player.getUniqueId())));

        PlayerUtils.resetInventory(player);
        player.teleport(Samurai.getInstance().getServerHandler().getSpawnLocation());
    }

    public void eliminate(Player player) {
        end(Bukkit.getPlayer(getOpponent(player.getUniqueId())));

        PlayerUtils.resetInventory(player);
    }

    public void sendMessage(String message) {
        getPlayers().forEach(player -> player.sendMessage(message));
    }

    public void playSound(Sound sound, float pitch) {
        getPlayers().forEach(player -> player.playSound(player.getLocation(), sound, 1F, pitch));
    }

    public UUID getOpponent(UUID uuid) {
        return uuid.equals(player1) ? player2 : player1;
    }

    public boolean contains(Player player) {
        return player.getUniqueId().equals(player1) || player.getUniqueId().equals(player2);
    }

    public List<Player> getPlayers() {
        return Stream.of(Bukkit.getPlayer(player1), Bukkit.getPlayer(player2))
                .filter(Objects::nonNull).collect(Collectors.toList());
    }
}
