package dev.lbuddyboy.samurai.map.game.impl.crystal;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.game.Game;
import dev.lbuddyboy.samurai.map.game.GameState;
import dev.lbuddyboy.samurai.map.game.GameType;
import dev.lbuddyboy.samurai.map.game.arena.GameArena;
import dev.lbuddyboy.samurai.map.kits.DefaultKit;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.InventoryUtils;
import dev.lbuddyboy.samurai.util.ItemUtils;
import lombok.Getter;
import mkremins.fanciful.FancyMessage;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class CrystalGame extends Game {

    private Map<UUID, Integer> kills = new HashMap<>();

    public CrystalGame(UUID host, List<GameArena> arenaOptions) {
        super(host, GameType.CRYSTAL_FFA, arenaOptions);
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
                    startFFA();
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(Samurai.getInstance(), 10L, 10L);
    }

    @Override
    public void endGame() {
        super.endGame();

        CoreProtectAPI coreProtect = Samurai.getInstance().getCoreProtect();

        CompletableFuture.runAsync(() -> {
            coreProtect.performRollback(
                    (int) ((System.currentTimeMillis() - getStartedAt()) / 1000),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    (int) getVotedArena().getBounds().getLowerNE().distance(getVotedArena().getBounds().getCenter()),
                    getVotedArena().getBounds().getCenter()
            );
        });

        if (winningPlayer != null) {
            sendMessages(winningPlayer.getDisplayName() + ChatColor.YELLOW + " won the Crystal FFA with " + ChatColor.DARK_RED + kills.get(winningPlayer.getUniqueId()) + ChatColor.YELLOW + " kills.");
        }
        int i = 1;
        for (Player p : getTopThree()) {
            if (p != null) {
                sendMessages(CC.translate("&g" + (i++) + ") &7" + p.getName() + ": &f" + kills.get(p.getUniqueId())));
            }
        }

        if (getVotedArena().getBounds() != null) {
            for (Item item : getVotedArena().getBounds().getWorld().getEntitiesByClass(Item.class)) {
                if (!getVotedArena().getBounds().contains(item.getLocation())) continue;

                item.remove();
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                kills.clear();
            }
        }.runTaskLater(Samurai.getInstance(), 100L);
    }

    private void startFFA() {
        Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> {
            DefaultKit pvpKit = Samurai.getInstance().getMapHandler().getKitManager().getDefaultKit("Crystal");
            for (Player player : getPlayers()) {
                InventoryUtils.resetInventoryNow(player);
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
                pvpKit.apply(player);
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
                    sendMessages(ChatColor.YELLOW + "The Crystal FFA has started!");
                    sendSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 2F);
                } else {
                    sendMessages(ChatColor.YELLOW + "The Crystal FFA is starting in " + ChatColor.DARK_RED + i + ChatColor.YELLOW + " second" + (i == 1 ? "" : "s") + "...");
                    sendSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 1F);
                }

                if (i <= 0) {
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(Samurai.getInstance(), 20L, 20L);
    }

    @Override
    public void addPlayer(Player player) throws IllegalStateException {
        super.addPlayer(player);
        kills.putIfAbsent(player.getUniqueId(), 0);
    }

    @Override
    public void removePlayer(Player player) {
        super.removePlayer(player);
    }

    @Override
    public void eliminatePlayer(Player player, Player killer) {
        super.eliminatePlayer(player, killer);

        if (killer != null) {
            sendMessages(ChatColor.DARK_RED.toString() + player.getName() + ChatColor.YELLOW + " has been eliminated by " + ChatColor.DARK_RED + killer.getName() + ChatColor.YELLOW + "! " + ChatColor.GRAY + "(" + getPlayers().size() + "/" + getStartedWith() + " players remaining)");
            kills.put(killer.getUniqueId(), kills.get(killer.getUniqueId()) + 1);

            ItemStack crapples = new ItemStack(Material.GOLDEN_APPLE, 2);
            if (killer.getInventory().firstEmpty() == -1 &&
                    Stream.of(killer.getInventory().getContents()).filter(Objects::nonNull).noneMatch(item -> item.isSimilar(crapples))) {
                killer.getInventory().setItem(7, crapples);
            } else {
                player.getInventory().addItem(crapples);
            }

            killer.setHealth(killer.getMaxHealth());
            killer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 8 * 20, 0));
            killer.updateInventory();
        }

        removeSpectator(player);

        if (players.size() == 1) {
            endGame();
        }

        Team team = Samurai.getInstance().getTeamHandler().getTeam(player);
        if (team != null) {
            team.setDTR(team.getDTR() + 1);
            Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> team.setDTRCooldown(System.currentTimeMillis()));
        }

    }

    public boolean isGracePeriod() {
        return getStartedAt() == null || System.currentTimeMillis() <= getStartedAt() + 6_000L;
    }

    @Override
    public void handleDamage(Player victim, Player damager, EntityDamageByEntityEvent event) {
        if (isGracePeriod()) {
            event.setCancelled(true);
            return;
        }

        if (state == GameState.RUNNING) {
            if (!isPlaying(victim.getUniqueId()) && !isPlaying(damager.getUniqueId())) {
                event.setCancelled(true);
            }
        } else {
            event.setCancelled(true);
        }
    }

    public List<Player> getTopThree() {
        return getPlayers().stream()
                .sorted(Comparator.comparingInt(p -> kills.get(((Player) p).getUniqueId())).reversed())
                .limit(3)
                .collect(Collectors.toList());
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

            int potions = ItemUtils.countStacksMatching(player.getInventory().getContents(), ItemUtils.INSTANT_HEAL_POTION_PREDICATE);

            replacements.addAll(List.of(
                    "%pots%", potions,
                    "%kills%", kills.get(player.getUniqueId())
            ));

            int i = 1;
            for (Player p : getTopThree()) {
                if (p != null) {
                    replacements.addAll(List.of(
                            "%kills-" + i + "%", kills.get(p.getUniqueId()),
                            "%kills-" + i++ + "-name%", p.getName()
                    ));
                }
            }

            lines.addAll(CC.translate(getConfig().getStringList("scoreboard-lines.running"), replacements.toArray(new Object[0])));

        } else {
            replacements.addAll(List.of(
                    "%winner%", (winningPlayer == null ? "None" : winningPlayer.getName()),
                    "%kills%", kills.get(player.getUniqueId())
            ));

            int i = 1;
            for (Player p : getTopThree()) {
                if (p != null) {
                    replacements.addAll(List.of(
                            "%kills-" + i + "%", kills.get(p.getUniqueId()),
                            "%kills-" + i++ + "-name%", p.getName()
                    ));
                }
            }

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
                        .then("█████").color(ChatColor.DARK_RED)
                        .then("██").color(ChatColor.GRAY),
                new FancyMessage("")
                        .then("██").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.DARK_RED)
                        .then("██████").color(ChatColor.GRAY)
                        .then(CC.translate(" &g&l" + getGameType().getDisplayName() + " Event")),
                new FancyMessage("")
                        .then("██").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.DARK_RED)
                        .then("██████").color(ChatColor.GRAY)
                        .then(" Hosted by ").color(ChatColor.GRAY)
                        .then(getHostName()).color(ChatColor.AQUA),
                new FancyMessage("")
                        .then("██").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.DARK_RED)
                        .then("██████").color(ChatColor.GRAY)
                        .then(" [").color(ChatColor.GRAY)
                        .then("Click to join event").color(ChatColor.YELLOW)
                        .command("/game join")
                        .formattedTooltip(new FancyMessage("Click here to join the event.").color(ChatColor.YELLOW))
                        .then("]").color(ChatColor.GRAY),
                new FancyMessage("")
                        .then("██").color(ChatColor.GRAY)
                        .then("█████").color(ChatColor.DARK_RED)
                        .then("██").color(ChatColor.GRAY),
                new FancyMessage("█████████").color(ChatColor.GRAY)
        );
    }


}
