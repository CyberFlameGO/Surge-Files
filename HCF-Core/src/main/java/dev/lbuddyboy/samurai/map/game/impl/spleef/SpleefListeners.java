package dev.lbuddyboy.samurai.map.game.impl.spleef;

import dev.lbuddyboy.samurai.map.game.GameHandler;
import dev.lbuddyboy.samurai.map.game.GameState;
import dev.lbuddyboy.samurai.util.Cuboid;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class SpleefListeners implements Listener {

    private final GameHandler gameHandler = Samurai.getInstance().getMapHandler().getGameHandler();

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (gameHandler.isOngoingGame() && gameHandler.getOngoingGame() instanceof SpleefGame) {
            SpleefGame ongoingGame = (SpleefGame) gameHandler.getOngoingGame();

            if (ongoingGame.getState() != GameState.RUNNING) return;

            if (!ongoingGame.getVotedArena().getBounds().expand(Cuboid.CuboidDirection.DOWN, 256).contains(event.getTo())) {
                return;
            }

            if (!ongoingGame.isPlaying(event.getPlayer().getUniqueId())) {
                return;
            }

            if (event.getPlayer().getLocation().getY() <= ongoingGame.getDeathHeight() || event.getPlayer().getLocation().getBlock().getType() == Material.WATER) {
                ongoingGame.eliminatePlayer(event.getPlayer(), null);
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (gameHandler.isOngoingGame() && gameHandler.getOngoingGame() instanceof SpleefGame) {
            SpleefGame ongoingGame = (SpleefGame) gameHandler.getOngoingGame();

            if (event.getEntityType() != EntityType.SNOWBALL) return;

            if (ongoingGame.getState() != GameState.RUNNING) return;

            Location location = event.getEntity().getLocation();
            Block block = location.getBlock().getRelative(BlockFace.DOWN);

            if (block.getType() == Material.SNOW_BLOCK) {
                gameHandler.getOngoingGame().getVotedArena().getNewOldMatBreakMap().put(block.getLocation(), block.getType());
                block.setType(Material.AIR);
            }
        }
    }

    @EventHandler
    public void onBlockDropItems(BlockDropItemEvent event) {
        if (gameHandler.isOngoingGame() && gameHandler.getOngoingGame() instanceof SpleefGame) {
            SpleefGame ongoingGame = (SpleefGame) gameHandler.getOngoingGame();

            if (ongoingGame.getState() != GameState.RUNNING || !ongoingGame.isPlaying(event.getPlayer().getUniqueId())) {
                return;
            }

            for (Item item : event.getItems()) {
                ItemStack itemStack = item.getItemStack();
                itemStack.setType(Material.SNOWBALL);
                event.getPlayer().getInventory().addItem(itemStack);
            }

            event.getItems().clear();
        }
    }

}
