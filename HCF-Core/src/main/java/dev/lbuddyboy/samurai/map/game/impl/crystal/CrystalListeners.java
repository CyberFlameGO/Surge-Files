package dev.lbuddyboy.samurai.map.game.impl.crystal;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.game.GameHandler;
import dev.lbuddyboy.samurai.map.game.impl.ffa.FFAGame;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class CrystalListeners implements Listener {

    private static final ItemStack GOLDEN_APPLE = ItemBuilder.of(Material.GOLDEN_APPLE).build();

    private final GameHandler gameHandler = Samurai.getInstance().getMapHandler().getGameHandler();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (gameHandler.isOngoingGame() && gameHandler.getOngoingGame() instanceof CrystalGame ongoingGame) {

            if (!ongoingGame.isPlaying(player.getUniqueId())) {
                return;
            }

            Player killer = player.getKiller();

            event.getDrops().clear();

            if (killer == null) {
                if (player.getLastDamageCause() instanceof EntityDamageByEntityEvent damage) {
                    if (damage.getDamager() instanceof EnderCrystal crystal) {
                        if (!(crystal.hasMetadata("placer"))) return;
                        Player placer = (Player) crystal.getMetadata("placer").get(0).value();

                        ongoingGame.eliminatePlayer(player, placer);
                    } else if (damage.getDamager() instanceof Arrow arrow) {
                        if (!(arrow.getShooter() instanceof Player shooter)) return;

                        ongoingGame.eliminatePlayer(player, shooter);
                    }
                }
                return;
            }

            ongoingGame.eliminatePlayer(player, killer);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getMaterial() == Material.END_CRYSTAL && event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.OBSIDIAN && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
                if (!(entity instanceof EnderCrystal crystal)) continue;
                if (crystal.getLocation().distance(event.getClickedBlock().getLocation()) > 1.5) continue;

                crystal.setMetadata("placer", new FixedMetadataValue(Samurai.getInstance(), player));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        if (gameHandler.isOngoingGame() && gameHandler.getOngoingGame() instanceof CrystalGame ongoingGame) {

            if (ongoingGame.isPlaying(event.getPlayer().getUniqueId())) {
                ItemStack drop = event.getItemDrop().getItemStack();

                if (drop.getType() == Material.GLASS_BOTTLE) {
                    event.getItemDrop().remove();
                }
            }
        }
    }

}
