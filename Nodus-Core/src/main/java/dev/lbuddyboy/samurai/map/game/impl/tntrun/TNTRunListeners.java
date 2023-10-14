package dev.lbuddyboy.samurai.map.game.impl.tntrun;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.game.GameHandler;
import dev.lbuddyboy.samurai.map.game.GameState;
import dev.lbuddyboy.samurai.util.Cuboid;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class TNTRunListeners implements Listener {

    private final GameHandler gameHandler = Samurai.getInstance().getMapHandler().getGameHandler();

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {

        if (!gameHandler.isOngoingGame() || !(this.gameHandler.getOngoingGame() instanceof TNTRunGame)) {
            return;
        }

        TNTRunGame ongoingGame = (TNTRunGame) gameHandler.getOngoingGame();

        if (ongoingGame.getState() != GameState.RUNNING) {
            return;
        }

        if (event.getTo() == null) return;

        if (!ongoingGame.getVotedArena().getBounds().expand(Cuboid.CuboidDirection.DOWN, 256).contains(event.getTo())) {
            return;
        }

        if (!ongoingGame.isPlaying(event.getPlayer().getUniqueId())) {
            return;
        }

        if (!ongoingGame.isStarted()) {
            return;
        }

        final Block from = event.getFrom().getBlock();
        final Block to = event.getTo().getBlock();

        if (from.getType().name().contains("WATER") || to.getType().name().contains("WATER") || from.getType().name().contains("LAVA") || to.getType().name().contains("LAVA")) {
            ongoingGame.eliminatePlayer(event.getPlayer(), null);
        }

    }

}
