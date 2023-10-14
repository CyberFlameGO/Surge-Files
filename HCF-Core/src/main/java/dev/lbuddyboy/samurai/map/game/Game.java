package dev.lbuddyboy.samurai.map.game;

import com.google.common.collect.ImmutableList;
import dev.lbuddyboy.samurai.map.game.arena.GameArena;
import dev.lbuddyboy.samurai.map.game.impl.gungame.GunGameGame;
import dev.lbuddyboy.samurai.map.game.menu.MapVoteMenu;
import dev.lbuddyboy.samurai.server.SpawnTagHandler;
import dev.lbuddyboy.samurai.util.*;
import dev.lbuddyboy.samurai.util.object.Config;
import dev.lbuddyboy.samurai.util.modsuite.ModUtils;
import dev.lbuddyboy.samurai.economy.uuid.FrozenUUIDCache;
import lombok.Getter;
import lombok.Setter;
import mkremins.fanciful.FancyMessage;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Getter
public abstract class Game {

    private final UUID host;
    private final GameType gameType;

    @Setter
    protected GameState state = GameState.WAITING;

    private Long nextAnnouncement = System.currentTimeMillis();

    private boolean hasExpired;
    @Setter
    private Long startedAt;
    @Setter
    private int startedWith;
    @Setter
    private int maxPlayers;

    @Setter
    private boolean gemRequiredToJoin = true;
    @Setter
    private boolean hostForceStarted;
    @Setter
    private int voteStart;

    protected Set<UUID> players = new HashSet<>();
    private Set<UUID> spectators = new HashSet<>();
    private Set<UUID> usedMessage = new HashSet<>();
    private Set<UUID> playerGems = new HashSet<>();

    private Map<GameArena, AtomicInteger> arenaOptions = new HashMap<>();
    private Map<UUID, GameArena> playerVotes = new HashMap<>();
    private GameArena votedArena;

    protected Player winningPlayer;

    public Game(UUID host, GameType gameType, List<GameArena> arenaOptions) {
        this.host = host;
        this.gameType = gameType;
        this.maxPlayers = gameType.getMaxPlayers();

        if (arenaOptions.size() == 1) {
            this.votedArena = arenaOptions.get(0);
        } else {
            for (GameArena arena : arenaOptions) {
                this.arenaOptions.put(arena, new AtomicInteger(0));
            }
        }
    }

    public void hostForceStart() {
        if (hostForceStarted || host == null) return;
        hostForceStarted = true;

        sendMessages(
                "",
                ChatColor.GREEN + "The event has been started by " + FrozenUUIDCache.name(host) + "!",
                ""
        );
    }

    public void forceStart() {
        state = GameState.RUNNING;
        startedWith = players.size();

        List<Map.Entry<GameArena, AtomicInteger>> arenas = getArenaOptions().entrySet().stream().sorted(Comparator.comparingInt(v -> v.getValue().get())).collect(Collectors.toList());
        Collections.reverse(arenas);
        try {
            votedArena = arenas.get(0).getKey();
            sendMessages(ChatColor.GOLD.toString() + votedArena.getName() + ChatColor.YELLOW.toString() + " has won the map vote!");
        } catch (Exception ignored) {

        }

        for (Player p1 : getPlayers()) {
            for (Player p2 : getPlayers()) {
                if (p1 == p2) continue;
                p1.showPlayer(p2);
                p2.showPlayer(p1);
            }
        }

        gameBegun();

        sendMessages(
                "",
                ChatColor.GOLD + "The event has been forcefully started by an administrator!",
                ""
        );
    }

    public void startGame() {
        List<UUID> list = new ArrayList<>(players);
        Collections.shuffle(list);
        players = new HashSet<>(list);

        for (Player player : getPlayersAndSpectators()) {
            if (isPlaying(player.getUniqueId())) {
                GameUtils.resetPlayer(player);
            }
            player.teleport(votedArena.getSpectatorSpawn());
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (state != GameState.WAITING) {
                    cancel();
                    return;
                }

                if (System.currentTimeMillis() > nextAnnouncement) {
                    nextAnnouncement = System.currentTimeMillis() + 15_000L;

                    List<FancyMessage> notificationMessages = createHostNotification();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        for (FancyMessage message : notificationMessages) {
                            message.send(player);
                        }
                    }
                }

                if (startedAt == null) {
                    if (players.size() >= gameType.getMinPlayers() || hostForceStarted || (voteStart >= players.size() && players.size() > 0)) {
                        startedAt = System.currentTimeMillis() + 30_000L;
                    }
                } else {
                    if (System.currentTimeMillis() > startedAt) {
                        state = GameState.RUNNING;
                        startedWith = players.size();

                        List<Map.Entry<GameArena, AtomicInteger>> arenas = getArenaOptions().entrySet().stream().sorted(Comparator.comparingInt(v -> v.getValue().get())).collect(Collectors.toList());
                        Collections.reverse(arenas);
                        try {
                            votedArena = arenas.get(0).getKey();
                            sendMessages(ChatColor.GOLD.toString() + votedArena.getName() + ChatColor.YELLOW.toString() + " has won the map vote!");
                        } catch (Exception ignored) {

                        }

                        gameBegun();
                    } else {
                        if (System.currentTimeMillis() > startedAt - 5_000L && votedArena == null) {
                            votedArena = getArenaOptions().entrySet().stream().sorted((o1, o2) -> o1.getValue().get()).collect(Collectors.toList()).get(0).getKey();
                            sendMessages(ChatColor.GOLD.toString() + votedArena.getName() + ChatColor.YELLOW.toString() + " has won the map vote!");
                        }
                    }
                }
            }
        }.runTaskTimer(Samurai.getInstance(), 10L, 10L);

        new BukkitRunnable() {
            private final long expiresAt = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(3L);
            private final List<Integer> broadcasts = new ArrayList<>(Arrays.asList(5, 10, 15));

            @Override
            public void run() {
                if (state != GameState.WAITING || getStartedAt() != null) {
                    cancel();
                    return;
                }

                long diff = expiresAt - System.currentTimeMillis();
                int diffSeconds = (int) (diff / 1000L);

                if (diff <= 0L) {
                    sendMessages(
                            "",
                            ChatColor.DARK_RED + "The event has been cancelled because it couldn't get enough players!",
                            ""
                    );

                    hasExpired = true;

                    Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> {
                        for (Player player : getPlayers()) {
                            player.removeMetadata("gaming", Samurai.getInstance());
                            player.teleport(Bukkit.getWorld("world").getSpawnLocation());
                        }

                        endGame();
                        Samurai.getInstance().getMapHandler().getGameHandler().endGame();
                    });

                    cancel();
                } else if (broadcasts.remove((Integer) diffSeconds)) {
                    sendMessages(
                            "",
                            ChatColor.RED + "The event will be automatically cancelled for lack of players in " + TimeUtils.formatIntoDetailedString(diffSeconds) + "!",
                            ""
                    );
                }
            }
        }.runTaskTimerAsynchronously(Samurai.getInstance(), 10L, 10L);
    }

    @Getter public Map<Block, Material> newOldBlockPlaceMap = new HashMap<>();
    @Getter public Map<Block, Material> newOldBlockBreakMap = new HashMap<>();

    public void endGame() {
        state = GameState.ENDED;
        winningPlayer = findWinningPlayer();

        if (winningPlayer != null) {
            Samurai.getInstance().getEventsWonMap().incrementStatistic(winningPlayer.getUniqueId(), 1);
            Samurai.getInstance().getShardMap().addShards(winningPlayer.getUniqueId(), playerGems.size());
            winningPlayer.sendMessage(ChatColor.WHITE + "You have received " + ChatColor.GOLD + "+" + playerGems.size() + " Shards" + ChatColor.WHITE + " for winning the event!");
        }

        playerGems.clear();

        for (Map.Entry<Block, Material> entry : newOldBlockPlaceMap.entrySet()) {
            entry.getKey().setType(entry.getValue());
        }

        for (Map.Entry<Block, Material> entry : newOldBlockBreakMap.entrySet()) {
            entry.getKey().setType(entry.getValue());
        }

        new BukkitRunnable() {
            private int i = 5;

            @Override
            public void run() {
                i--;

                if (winningPlayer != null && i <= 3 && i > 0) {
                    Bukkit.broadcastMessage(winningPlayer.getDisplayName() + ChatColor.YELLOW + " has " + ChatColor.GREEN + "won" + ChatColor.YELLOW + " the event!");
                    winningPlayer.playSound(winningPlayer.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 2F, 1F);
                }

                if (i <= 0) {

                    // this block of code should never throw errors, but just in case it does,
                    // lets wrap in a try-catch so the game gets cleared from the game handler
                    try {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                for (Player player : getPlayers()) {
                                    removePlayer(player);
                                    if (!(Game.this instanceof GunGameGame gunGame)) {
                                        if (winningPlayer != null) {
                                            if (player == winningPlayer) {
                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crate give Surge " + winningPlayer.getName() + " 3");

                                            }
                                        }
                                        continue;
                                    }
                                    List<UUID> winners = null;
                                    if (gunGame.getRedTeam().isEmpty()) winners = gunGame.getBlueTeam();
                                    if (gunGame.getBlueTeam().isEmpty()) winners = gunGame.getRedTeam();

                                    if (winners != null) {
                                        for (UUID winner : winners) {
                                            if (winner != null) {
                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crate give Surge " + UUIDUtils.name(winner) + " 3");
                                            }
                                        }
                                    }
                                }

                                for (Player spectator : getSpectators()) {
                                    removeSpectator(spectator);
                                }
                            }
                        }.runTask(Samurai.getInstance());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Samurai.getInstance().getMapHandler().getGameHandler().endGame();

                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(Samurai.getInstance(), 20L, 20L);
    }

    public boolean isPlaying(UUID player) {
        return players.contains(player);
    }

    public boolean isSpectating(UUID player) {
        return spectators.contains(player);
    }

    public boolean isPlayingOrSpectating(UUID player) {
        return isPlaying(player) || isSpectating(player);
    }

    public void addPlayer(Player player) throws IllegalStateException {
        if (state != GameState.WAITING) {
            throw new IllegalStateException("That event has already started. Try spectating instead with /game spectate.");
        }

        if (ModUtils.isModMode(player)) {
            player.sendMessage(ChatColor.RED + "You can't join the event while in mod-mode.");
            return;
        }

        if (SpawnTagHandler.isTagged(player)) {
            player.sendMessage(ChatColor.RED + "You can't join the event while spawn-tagged.");
            return;
        }

        if (!ItemUtils.hasEmptyInventory(player)) {
            player.sendMessage(ChatColor.RED + "You need to have an empty inventory to join the event.");
            return;
        }

        if (Samurai.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You can't have pvp timer while joining this event. /pvp enable");
            return;
        }

        if (isGemRequiredToJoin() && !Samurai.getInstance().getShardMap().removeShards(player.getUniqueId(), 1)) {
            player.sendMessage(ChatColor.RED + "You must have at least 1 shard to join the event.");
            return;
        }

        players.add(player.getUniqueId());
        playerGems.add(player.getUniqueId());
        player.setMetadata("gaming", new FixedMetadataValue(Samurai.getInstance(), true));

        GameUtils.resetPlayer(player);

        if (Samurai.getInstance().getMapHandler().getGameHandler().getConfig().getLobbySpawn() != null) {
            player.teleport(Samurai.getInstance().getMapHandler().getGameHandler().getConfig().getLobbySpawn());
        }

        if (!usedMessage.contains(player.getUniqueId())) {
            sendMessages(player.getDisplayName() + ChatColor.YELLOW.toString() + " has joined the " + ChatColor.LIGHT_PURPLE + gameType.getDisplayName() + ChatColor.YELLOW + " event! (" + ChatColor.LIGHT_PURPLE + players.size() + "/" + maxPlayers + ChatColor.YELLOW + ")");
        }

        if (getVotedArena() == null && getArenaOptions().size() > 1) {
            new MapVoteMenu(this).openMenu(player);
        }
    }

    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());

        InventoryUtils.resetInventoryNow(player);
        SpawnTagHandler.removeTag(player);
        player.teleport(Samurai.getInstance().getServerHandler().getSpawnLocation());
        player.removeMetadata("gaming", Samurai.getInstance());

        if (state == GameState.WAITING) {
            if (!usedMessage.contains(player.getUniqueId())) {
                usedMessage.add(player.getUniqueId());
                sendMessages(player.getDisplayName() + ChatColor.YELLOW.toString() + " has left the " + ChatColor.LIGHT_PURPLE + gameType.getDisplayName() + ChatColor.YELLOW + " event! (" + ChatColor.LIGHT_PURPLE + players.size() + "/" + 64 + ChatColor.YELLOW + ")");
            }

            playerGems.remove(player.getUniqueId());
            Samurai.getInstance().getShardMap().addShards(player.getUniqueId(), 1);
        }
    }

    public void eliminatePlayer(Player player, Player killer) {
        players.remove(player.getUniqueId());
        player.removeMetadata("gaming", Samurai.getInstance());

        InventoryUtils.resetInventoryNow(player);

        if (killer == null) {
            if (Bukkit.getPlayer(player.getUniqueId()) != null) {
                sendMessages(CC.MAIN + player.getName() + ChatColor.YELLOW + " has been eliminated! " + ChatColor.GRAY + "(" + getPlayers().size() + "/" + getStartedWith() + " players remaining)");
            } else {
                sendMessages(CC.MAIN + player.getName() + ChatColor.YELLOW + " has disconnected and has been disqualified! " + ChatColor.GRAY + "(" + getPlayers().size() + "/" + getStartedWith() + " players remaining)");
            }
        }

        Samurai.getInstance().getLeftGameMap().setActive(player.getUniqueId(), true);
        if (killer != null) {
            addSpectator(player);
        }
    }

    public void addSpectator(Player player) {
        spectators.add(player.getUniqueId());

        player.setMetadata("gaming", new FixedMetadataValue(Samurai.getInstance(), true));

        Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> {
            GameUtils.resetPlayer(player);
            player.teleport(votedArena.getSpectatorSpawn());
        });
    }

    public void removeSpectator(Player player) {
        spectators.remove(player.getUniqueId());

        player.removeMetadata("gaming", Samurai.getInstance());
        Samurai.getInstance().getLeftGameMap().setActive(player.getUniqueId(), true);

        Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> {
            InventoryUtils.resetInventoryNow(player);
            player.teleport(Samurai.getInstance().getServerHandler().getSpawnLocation());
            for (Player refreshFor : getPlayers()) {
                refreshFor.showPlayer(Samurai.getInstance(), player);
            }
        });
    }

    public String getHostName() {
        return FrozenUUIDCache.name(host);
    }

    public List<Player> getPlayers() {
        if (players.isEmpty()) {
            return ImmutableList.of();
        }

        List<Player> players = new ArrayList<>();
        for (UUID uuid : this.players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                players.add(player);
            }
        }

        return players;
    }

    public List<Player> getSpectators() {
        if (spectators.isEmpty()) {
            return ImmutableList.of();
        }

        List<Player> spectators = new ArrayList<>();
        for (UUID uuid : this.spectators) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                spectators.add(player);
            }
        }

        return spectators;
    }

    public List<Player> getPlayersAndSpectators() {
        List<Player> playersAndSpectators = new ArrayList<>();
        playersAndSpectators.addAll(getPlayers());
        playersAndSpectators.addAll(getSpectators());
        return playersAndSpectators;
    }

    public void sendMessages(String... messages) {
        for (Player player : getPlayersAndSpectators()) {
            for (String message : messages) {
                player.sendMessage(CC.translate(message));
            }
        }
    }

    public void sendSound(Sound sound, float volume, float pitch) {
        for (Player player : getPlayersAndSpectators()) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    public void handleDamage(Player victim, Player damager, EntityDamageByEntityEvent event) {
        event.setCancelled(true);
    }

    public abstract Player findWinningPlayer();

    public Config getConfig() {
        return Samurai.getInstance().getMapHandler().getGameHandler().getConfigs().get(gameType.name());
    }

    public abstract List<String> getScoreboardLines(Player player);

    public abstract List<FancyMessage> createHostNotification();

    public void gameBegun() {
    }

}
