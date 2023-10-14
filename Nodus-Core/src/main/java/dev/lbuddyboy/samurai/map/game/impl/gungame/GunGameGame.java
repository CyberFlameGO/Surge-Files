package dev.lbuddyboy.samurai.map.game.impl.gungame;

import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import lombok.Getter;
import mkremins.fanciful.FancyMessage;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.game.Game;
import dev.lbuddyboy.samurai.map.game.GameState;
import dev.lbuddyboy.samurai.map.game.GameType;
import dev.lbuddyboy.samurai.map.game.arena.GameArena;
import dev.lbuddyboy.samurai.listener.LunarClientListener;
import dev.lbuddyboy.samurai.util.InventoryUtils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class GunGameGame extends Game {
    public GunGameGame(UUID host, List<GameArena> arenaOptions) {
        super(host, GameType.GUN_GAME, arenaOptions);
    }

    @Getter private List<UUID> redTeam = new ArrayList<>();
    @Getter private List<UUID> blueTeam = new ArrayList<>();
    @Getter private int redStartedOutWith;
    @Getter private int blueStartedOutWith;
    private Set<UUID> playerGems = new HashSet<>();

    @Getter private static ItemStack shotGun = ItemBuilder.of(Material.GOLDEN_HORSE_ARMOR).modelData(100).name("&6Shotgun Kit &7(Right Click)").enchant(Enchantment.ARROW_INFINITE, 1).build();
    @Getter private static ItemStack smg = ItemBuilder.of(Material.IRON_HORSE_ARMOR).modelData(101).name("&6SMG Kit &7(Right Click)").enchant(Enchantment.ARROW_INFINITE, 1).build();
    @Getter private static ItemStack rifle = ItemBuilder.of(Material.DIAMOND_HORSE_ARMOR).modelData(102).name("&6Rifle Kit &7(Right Click)").enchant(Enchantment.ARROW_INFINITE, 1).build();
    @Getter private boolean started = false;

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
                    startMineStrike();
                    cancel();
                }
            }
        }.runTaskTimer(Samurai.getInstance(), 10L, 10L);
    }

    @Override
    public void endGame() {
        state = GameState.ENDED;
        Samurai.getInstance().getMapHandler().getGameHandler().setOngoingGame(null);

        String team = ChatColor.RED + "Red Team";

        if (redTeam.isEmpty() && !blueTeam.isEmpty()) {
            team = ChatColor.BLUE + "Blue Team";
        }

        String finalTeam = team;
        new BukkitRunnable() {
            private int i = 5;

            @Override
            public void run() {
                i--;

                if (i <= 3 && i > 0) {
                    Bukkit.broadcastMessage(finalTeam + ChatColor.YELLOW + " has " + ChatColor.GREEN + "won" + ChatColor.YELLOW + " the event!");
                }

                if (i <= 0) {
                    cancel();

                    new BukkitRunnable() {
                        @Override
                        public void run() {

                            blueTeam.clear();
                            redTeam.clear();

                            for (Player player : getPlayers()) {
                                removePlayer(player);
                            }

                            for (Player spectator : getSpectators()) {
                                removeSpectator(spectator);
                            }
                        }
                    }.runTask(Samurai.getInstance());

                    Samurai.getInstance().getMapHandler().getGameHandler().endGame();
                }
            }
        }.runTaskTimerAsynchronously(Samurai.getInstance(), 20L, 20L);

        this.started = false;

        if (getVotedArena().getBounds() != null) {
            Bukkit.getScheduler().runTaskLater(Samurai.getInstance(), getVotedArena()::restoreSnapshot, 5 * 20L);
        }
    }

    @Override
    public void handleDamage(Player victim, Player damager, EntityDamageByEntityEvent event) {
        event.setCancelled(false);
    }

    private void startMineStrike() {
        for (Player player : getPlayers()) {
            InventoryUtils.resetInventoryNow(player);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));

            player.getInventory().addItem(shotGun);
            player.getInventory().addItem(rifle);
            player.getInventory().addItem(smg);
        }

        int midIndex = (getPlayers().size() - 1) / 2;
        List<List<Player>> split = new ArrayList<>(
                getPlayers().stream()
                        .collect(Collectors.partitioningBy(s -> getPlayers().indexOf(s) > midIndex))
                        .values()
        );

        final List<Player> red = split.get(0);
        final List<Player> blue = split.get(1);

        red.forEach(player -> {
            player.getInventory().setHelmet(ItemBuilder.of(Material.REDSTONE_BLOCK).build());
            player.getInventory().setChestplate(ItemBuilder.of(Material.LEATHER_CHESTPLATE).color(Color.RED).build());
            player.getInventory().setLeggings(ItemBuilder.of(Material.LEATHER_LEGGINGS).color(Color.RED).build());
            player.getInventory().setBoots(ItemBuilder.of(Material.LEATHER_BOOTS).color(Color.RED).build());

            redTeam.add(player.getUniqueId());
            player.teleport(getVotedArena().getPointA());
        });

        blue.forEach(player -> {
            player.getInventory().setHelmet(ItemBuilder.of(Material.LAPIS_BLOCK).build());
            player.getInventory().setChestplate(ItemBuilder.of(Material.LEATHER_CHESTPLATE).color(Color.BLUE).build());
            player.getInventory().setLeggings(ItemBuilder.of(Material.LEATHER_LEGGINGS).color(Color.BLUE).build());
            player.getInventory().setBoots(ItemBuilder.of(Material.LEATHER_BOOTS).color(Color.BLUE).build());

            blueTeam.add(player.getUniqueId());
            player.teleport(getVotedArena().getPointB());
        });

        for (Player player : getPlayers()) {
            LunarClientListener.updateNametag(player);
        }

        this.redStartedOutWith = this.redTeam.size();
        this.blueStartedOutWith = this.blueTeam.size();

        this.started = false;

        this.setStartedAt(System.currentTimeMillis());

        new BukkitRunnable() {
            private int i = 6;

            @Override
            public void run() {
                if (state == GameState.ENDED || started) {
                    cancel();
                    return;
                }

                i--;

                sendSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1F, i == 0 ? 2F : 1F);
                started = i == 0;

                if (i == 0) {
                    sendMessages("&6&l" + getGameType().getDisplayName() + " &eevent has begun!");
                } else {
                    sendMessages("&6&l" + getGameType().getDisplayName() + " &eevent begins in &f" + i + " second" + (i == 1 ? "" : "s") + "&e...");
                }
            }
        }.runTaskTimer(Samurai.getInstance(), 20L, 20L);
    }

    @Override
    public void eliminatePlayer(Player player, Player killer) {
        super.eliminatePlayer(player, killer);

//        this.sendMessages(player.getDisplayName() + " &ewas shot to death by &4" + killer.getDisplayName());

        addSpectator(player);

        blueTeam.remove(player.getUniqueId());
        redTeam.remove(player.getUniqueId());

        if (blueTeam.isEmpty() || redTeam.isEmpty()) {
            endGame();
        }

        LunarClientListener.updateTeammates(player);
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
                        "%starting-in%", ((double) Math.round(10.0D * (double) remainingSeconds) / 10.0D),
                        "%red-team-players-left%", redTeam.size() + "/" + getRedStartedOutWith(),
                        "%blue-team-players-left%", blueTeam.size() + "/" + getBlueStartedOutWith()
                ));
                lines.addAll(CC.translate(getConfig().getStringList("scoreboard-lines.waiting.starting-players-met"), replacements.toArray(new Object[0])));
            }
        } else if (state == GameState.RUNNING) {
            replacements.addAll(List.of(
                    "%red-team-players-left%", redTeam.size() + "/" + getRedStartedOutWith(),
                    "%blue-team-players-left%", blueTeam.size() + "/" + getBlueStartedOutWith()
            ));

            lines.addAll(CC.translate(getConfig().getStringList("scoreboard-lines.running"), replacements.toArray(new Object[0])));
        } else {
            replacements.addAll(List.of(
                    "%winner%", (winningPlayer == null ? "None" : winningPlayer.getName()),
                    "%red-team-players-left%", redTeam.size() + "/" + getRedStartedOutWith(),
                    "%blue-team-players-left%", blueTeam.size() + "/" + getBlueStartedOutWith()
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
                        .then("█████").color(ChatColor.DARK_PURPLE)
                        .then("██").color(ChatColor.GRAY),
                new FancyMessage("")
                        .then("██").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.DARK_PURPLE)
                        .then("██████").color(ChatColor.GRAY)
                        .then(CC.translate(" &g&l" + getGameType().getDisplayName() + " Event")),
                new FancyMessage("")
                        .then("██").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.DARK_PURPLE)
                        .then("██").color(ChatColor.GRAY)
                        .then("███").color(ChatColor.DARK_PURPLE)
                        .then("█").color(ChatColor.GRAY)
                        .then(" [").color(ChatColor.GRAY)
                        .then(" Hosted by ").color(ChatColor.GRAY)
                        .then(getHostName()).color(ChatColor.AQUA),
                new FancyMessage("")
                        .then("██").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.DARK_PURPLE)
                        .then("████").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.DARK_PURPLE)
                        .then("█").color(ChatColor.GRAY)
                        .then(" [").color(ChatColor.GRAY)
                        .then("Click to join event").color(ChatColor.YELLOW)
                        .command("/game join")
                        .formattedTooltip(new FancyMessage("Click here to join the event.").color(ChatColor.YELLOW))
                        .then("]").color(ChatColor.GRAY),
                new FancyMessage("")
                        .then("██").color(ChatColor.GRAY)
                        .then("█████").color(ChatColor.DARK_PURPLE)
                        .then("██").color(ChatColor.GRAY),
                new FancyMessage("█████████").color(ChatColor.GRAY)
        );
    }
}