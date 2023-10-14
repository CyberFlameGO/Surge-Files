package dev.lbuddyboy.samurai.map.game.impl.tntrun;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.game.Game;
import dev.lbuddyboy.samurai.map.game.GameState;
import dev.lbuddyboy.samurai.map.game.GameType;
import dev.lbuddyboy.samurai.map.game.arena.GameArena;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.InventoryUtils;
import lombok.Getter;
import mkremins.fanciful.FancyMessage;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TNTRunGame extends Game {

    @Getter private boolean started = false;

    public TNTRunGame(UUID host, List<GameArena> arenaOptions) {
        super(host, GameType.TNT_RUN, arenaOptions);
    }

    @Override
    public void startGame() {
        super.startGame();

        started = false;

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
                    startTNTRun();
                    cancel();
                }
            }
        }.runTaskTimer(Samurai.getInstance(), 10L, 10L);
    }

    @Override
    public void endGame() {
        super.endGame();

        if (getVotedArena().getBounds() != null) {
            Bukkit.getScheduler().runTaskLater(Samurai.getInstance(), getVotedArena()::restoreSnapshot, 5 * 20L);
        }
    }

    private void startTNTRun() {
        this.started = false;

        for (Player player : this.getPlayers()) {
            InventoryUtils.resetInventoryNow(player);
            player.teleport(this.getVotedArena().getPointA());
        }

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

        new BukkitRunnable() {
            @Override
            public void run() {

                if (!started) {
                    return;
                }

                if (state == GameState.ENDED) {
                    cancel();
                    return;
                }

                for (Player player : getPlayers()) {
                    ArrayList<BlockState> block = getBlocksBelow(player);

                    Samurai.getInstance().getServer().getScheduler().runTaskLater(Samurai.getInstance(), () -> {
                        if (!block.isEmpty()) {
                            getNewOldBlockBreakMap().put(block.get(0).getBlock(), Material.TNT);
                            block.get(0).getLocation().getBlock().setType(Material.AIR);
                        }
                    }, 2);
                }
            }
        }.runTaskTimer(Samurai.getInstance(), 0, 2L);
    }

    public ArrayList<BlockState> getBlocksBelow(Player player){
        ArrayList<BlockState> blocksBelow = new ArrayList<>();
        Location location = player.getLocation();
        World world = player.getWorld();
        double x = location.getX();
        double z = location.getZ();

        // Add the block below the player to the List.
        Location block = new Location(world, x, location.getY() - 1, z);
        if (block.getBlock().getType() == Material.TNT) blocksBelow.add(block.getBlock().getState());

        // If the player is on the edge of the block, add the necessary blocks to the List.
        if(x - Math.floor(x) <= 0.3){
            block.setX(Math.floor(x) - 0.5);
            if (block.getBlock().getType() == Material.TNT) blocksBelow.add(block.getBlock().getState());
            block = new Location(world, x, location.getY() - 1, z);
        } else if(x - Math.floor(x) >= 0.7){
            block.setX(Math.ceil(x) + 0.5);
            if (block.getBlock().getType() == Material.TNT) blocksBelow.add(block.getBlock().getState());
            block = new Location(world, x, location.getY() - 1, z);
        }

        if(z - Math.floor(z) <= 0.3){
            block.setZ(Math.floor(z) - 0.5);
            if (block.getBlock().getType() == Material.TNT) blocksBelow.add(block.getBlock().getState());
        } else if(z - Math.floor(z) >= 0.7){
            block.setZ(Math.ceil(z) + 0.5);
            if (block.getBlock().getType() == Material.TNT) blocksBelow.add(block.getBlock().getState());
        }

        if(x - Math.floor(x) <= 0.3 && z - Math.floor(z) <= 0.3){
            block.setX(Math.floor(x) - 0.5);
            block.setZ(Math.floor(z) - 0.5);

            if (block.getBlock().getType() == Material.TNT) blocksBelow.add(block.getBlock().getState());
        } else if(x - Math.floor(x) >= 0.7 && z - Math.floor(x) >= 0.7){
            block.setX(Math.ceil(x) + 0.5);
            block.setZ(Math.ceil(z) + 0.5);

            if (block.getBlock().getType() == Material.TNT) blocksBelow.add(block.getBlock().getState());
        } else if(x - Math.floor(x) <= 0.3 && z - Math.floor(z) >= 0.7){
            block.setX(Math.floor(x) - 0.5);
            block.setZ(Math.ceil(z) + 0.5);

            if (block.getBlock().getType() == Material.TNT) blocksBelow.add(block.getBlock().getState());
        } else if(x - Math.floor(x) >= 0.7 && z - Math.floor(z) <= 0.3){
            block.setX(Math.ceil(x) + 0.5);
            block.setZ(Math.floor(z) - 0.5);

            if (block.getBlock().getType() == Material.TNT) blocksBelow.add(block.getBlock().getState());
        }

        // Return the List.
        return blocksBelow;
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
                new FancyMessage("███████").color(ChatColor.GRAY),
                new FancyMessage("")
                        .then("██").color(ChatColor.GRAY)
                        .then("███").color(ChatColor.DARK_RED)
                        .then("████").color(ChatColor.GRAY)
                        .then(CC.translate(" &g&l" + getGameType().getDisplayName() + " Event")),
                new FancyMessage("")
                        .then("███").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.DARK_RED)
                        .then("███").color(ChatColor.GRAY)
                        .then(CC.translate(" &7Players: &f" + this.getPlayers().size() + "/" + this.getMaxPlayers())),
                new FancyMessage("")
                        .then("███").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.DARK_RED)
                        .then("███").color(ChatColor.GRAY)
                        .then(" Hosted by ").color(ChatColor.GRAY)
                        .then(getHostName()).color(ChatColor.AQUA),
                new FancyMessage("")
                        .then("███").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.DARK_RED)
                        .then("███").color(ChatColor.GRAY)
                        .then(" Click to join the event").color(ChatColor.GREEN)
                        .command("/game join")
                        .then(" [").color(ChatColor.GRAY)
                        .then("Click to join event").color(ChatColor.YELLOW)
                        .command("/join")
                        .formattedTooltip(new FancyMessage("Click here to join the event.").color(ChatColor.YELLOW))
                        .then("]").color(ChatColor.GRAY),
                new FancyMessage("███████").color(ChatColor.GRAY)
        );
    }
}
