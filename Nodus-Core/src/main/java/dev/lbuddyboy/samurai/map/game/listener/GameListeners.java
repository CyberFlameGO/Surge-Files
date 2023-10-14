package dev.lbuddyboy.samurai.map.game.listener;

import dev.lbuddyboy.flash.FlashLanguage;
import dev.lbuddyboy.samurai.map.game.*;
import dev.lbuddyboy.samurai.map.game.impl.crystal.CrystalGame;
import dev.lbuddyboy.samurai.map.game.impl.gungame.GunGameGame;
import dev.lbuddyboy.samurai.map.game.menu.MapVoteMenu;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.modsuite.PlayerUtils;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.game.impl.ffa.FFAGame;
import dev.lbuddyboy.samurai.map.game.impl.spleef.SpleefGame;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.List;

public class GameListeners implements Listener {

    private static List<Material> CONTAINER_TYPES = Arrays.asList(
            Material.CHEST,
            Material.TRAPPED_CHEST,
            Material.ENDER_CHEST,
            Material.FURNACE,
            Material.LEGACY_BURNING_FURNACE,
            Material.DISPENSER,
            Material.HOPPER,
            Material.DROPPER,
            Material.BREWING_STAND
    );

    private static List<String> ALLOWED_COMMANDS = Arrays.asList(
            "msg",
            "m",
            "message",
            "tell",
            "r",
            "reply",
            "tpm",
            "togglemessages"
    );

    private GameHandler gameHandler = Samurai.getInstance().getMapHandler().getGameHandler();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.getLocation().getWorld().getName().equals("kits_events")) {
            player.teleport(Bukkit.getWorld("world").getSpawnLocation());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onSpawn(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() != Material.BEDROCK && event.getClickedBlock().getType() != Material.OBSIDIAN) return;

        if(event.getMaterial() == Material.END_CRYSTAL && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            if (event.getPlayer().getWorld().getEnvironment() == World.Environment.THE_END && Samurai.getInstance().getMapHandler().isKitMap()) {
                Block above = event.getClickedBlock().getRelative(BlockFace.UP);
                Location location = above.getLocation();

                location.setX(location.getBlockX() + 0.5);
                location.setZ(location.getBlockZ() + 0.5);

                EnderCrystal crystal = above.getWorld().spawn(location, EnderCrystal.class);
                crystal.setShowingBottom(false);
                return;
            }

            if (gameHandler.isOngoingGame() && gameHandler.getOngoingGame().isPlayingOrSpectating(event.getPlayer().getUniqueId())) {
                if (gameHandler.getOngoingGame() instanceof CrystalGame
                        && gameHandler.getOngoingGame().getState() == GameState.RUNNING
                        && gameHandler.getOngoingGame().isPlaying(event.getPlayer().getUniqueId())) {
                    Block above = event.getClickedBlock().getRelative(BlockFace.UP);
                    Location location = above.getLocation();

                    location.setX(location.getBlockX() + 0.5);
                    location.setZ(location.getBlockZ() + 0.5);

                    if (location.getBlock().getType() == Material.END_CRYSTAL) {
                        return;
                    }

                    above.getWorld().spawn(location, EnderCrystal.class).setShowingBottom(false);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (gameHandler.isOngoingGame() && gameHandler.getOngoingGame().isPlayingOrSpectating(event.getPlayer().getUniqueId())) {
                if (event.hasItem() && event.getItem().equals(GameItems.LEAVE_EVENT)) {
                    if (gameHandler.getOngoingGame().isPlaying(event.getPlayer().getUniqueId())) {
                        gameHandler.getOngoingGame().removePlayer(event.getPlayer());
                    } else if (gameHandler.getOngoingGame().isSpectating(event.getPlayer().getUniqueId())) {
                        gameHandler.getOngoingGame().removeSpectator(event.getPlayer());
                    }

                    event.getPlayer().teleport(Samurai.getInstance().getServerHandler().getSpawnLocation());
                }

                if (event.hasItem() && event.getItem().equals(GameItems.VOTE_FOR_ARENA)) {
                    if (gameHandler.getOngoingGame().getPlayerVotes().containsKey(event.getPlayer().getUniqueId())) {
                        event.getPlayer().sendMessage(CC.translate("&cYou've already voted!"));
                        return;
                    }
                    new MapVoteMenu(gameHandler.getOngoingGame()).openMenu(event.getPlayer());
                }

                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (CONTAINER_TYPES.contains(event.getClickedBlock().getType())) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (gameHandler.isOngoingGame() && gameHandler.getOngoingGame().isPlayingOrSpectating(event.getPlayer().getUniqueId())) {
            // Spleef
            if (gameHandler.getOngoingGame() instanceof SpleefGame
                    && gameHandler.getOngoingGame().getState() == GameState.RUNNING
                    && gameHandler.getOngoingGame().isPlaying(event.getPlayer().getUniqueId())) {
                event.setCancelled(System.currentTimeMillis() < gameHandler.getOngoingGame().getStartedAt() + 6_000L);
                gameHandler.getOngoingGame().getVotedArena().getNewOldMatBreakMap().put(event.getBlock().getLocation(), event.getBlock().getType());
                return;
            }
            // Crystal
            if (gameHandler.getOngoingGame() instanceof CrystalGame
                    && gameHandler.getOngoingGame().getState() == GameState.RUNNING
                    && gameHandler.getOngoingGame().isPlaying(event.getPlayer().getUniqueId())) {
                event.setCancelled(System.currentTimeMillis() < gameHandler.getOngoingGame().getStartedAt() + 6_000L);
                return;
            }

            event.setCancelled(true);
        } else if (event.getBlock().getWorld().equals(gameHandler.getWorld())) {
            event.setCancelled(!event.getPlayer().hasMetadata("Build"));
        }
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (gameHandler.isOngoingGame() && gameHandler.getOngoingGame().isPlayingOrSpectating(event.getPlayer().getUniqueId())) {
            // Crystal
            if (gameHandler.getOngoingGame() instanceof CrystalGame
                    && gameHandler.getOngoingGame().getState() == GameState.RUNNING
                    && gameHandler.getOngoingGame().isPlaying(event.getPlayer().getUniqueId())) {
                event.setCancelled(System.currentTimeMillis() < gameHandler.getOngoingGame().getStartedAt() + 6_000L);
                return;
            }

            event.setCancelled(true);
        } else if (event.getBlock().getWorld().equals(gameHandler.getWorld())) {
            event.setCancelled(!event.getPlayer().hasMetadata("Build"));
        }
    }

    @EventHandler
    public void onPlayerBucketFillEvent(PlayerBucketFillEvent event) {
        if (gameHandler.isOngoingGame() && gameHandler.getOngoingGame().isPlayingOrSpectating(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerBucketEmptyEvent(PlayerBucketFillEvent event) {
        if (gameHandler.isOngoingGame() && gameHandler.getOngoingGame().isPlayingOrSpectating(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        if (gameHandler.isOngoingGame()) {
            if (gameHandler.getOngoingGame().isPlayingOrSpectating(event.getPlayer().getUniqueId())) {
                // FFA
                if (gameHandler.getOngoingGame() instanceof FFAGame) {
                    if (!gameHandler.getOngoingGame().isPlaying(event.getPlayer().getUniqueId())) {
                        event.setCancelled(true);
                        return;
                    }

                    return;
                }

                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onFoodLevelChangeEvent(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player && gameHandler.isOngoingGame() && gameHandler.getOngoingGame().isPlayingOrSpectating(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
            ((Player) event.getEntity()).setFoodLevel(20);
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (gameHandler.isOngoingGame()) {
            if (event.getEntity() instanceof Player) {
                // Spleef
                if (gameHandler.getOngoingGame() instanceof SpleefGame && event.getDamager() instanceof Snowball) return;
                if (gameHandler.getOngoingGame() instanceof GunGameGame && event.getDamager() instanceof Snowball) return;

                Player victim = (Player) event.getEntity();
                Player damager = PlayerUtils.getDamageSource(event.getDamager());

                if (damager == null) {
                    return;
                }

                Game ongoingGame = gameHandler.getOngoingGame();
                boolean victimInGame = ongoingGame.isPlaying(victim.getUniqueId());
                boolean damagerInGame = ongoingGame.isPlaying(damager.getUniqueId());

                if (!victimInGame && !damagerInGame) {
                    return;
                }

                if (victimInGame && !damagerInGame) {
                    event.setCancelled(true);
                } else {
                    ongoingGame.handleDamage(victim, damager, event);
                }

            }
        }
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (gameHandler.isOngoingGame()) {
                if (gameHandler.getOngoingGame().isPlaying(player.getUniqueId())) {
                    if (!(event instanceof EntityDamageByEntityEvent) && !(gameHandler.getOngoingGame() instanceof FFAGame) && !(gameHandler.getOngoingGame() instanceof CrystalGame) && !(gameHandler.getOngoingGame() instanceof GunGameGame)) {
                        event.setCancelled(true);
                    }
                } else if (gameHandler.getOngoingGame().isSpectating(player.getUniqueId())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageVoid(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (event.getCause() != EntityDamageEvent.DamageCause.VOID) return;

            if (gameHandler.isOngoingGame()) {
                if (gameHandler.getOngoingGame().getState() != GameState.WAITING) return;

                player.teleport(gameHandler.getConfig().getLobbySpawn());
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (!player.hasMetadata("gaming")) return;
        if (player.isOp()) return;

        for (String s : ALLOWED_COMMANDS) {
            if (event.getMessage().replaceAll("/", "").equalsIgnoreCase(s)) {
                return;
            }
        }

        event.setCancelled(true);
        player.sendMessage(CC.translate("&cThat command is not allowed here."));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (gameHandler.isOngoingGame()) {
            if (gameHandler.getOngoingGame().isPlayingOrSpectating(event.getWhoClicked().getUniqueId())) {
                // FFA
                if (gameHandler.getOngoingGame() instanceof FFAGame) {
                    if (!gameHandler.getOngoingGame().isPlaying(event.getWhoClicked().getUniqueId())) {
                        event.setCancelled(true);
                        return;
                    }

                    event.setCancelled(event.getSlotType() == InventoryType.SlotType.ARMOR);
                    return;
                }
                // FFA
                if (gameHandler.getOngoingGame() instanceof CrystalGame) {
                    if (!gameHandler.getOngoingGame().isPlaying(event.getWhoClicked().getUniqueId())) {
                        event.setCancelled(true);
                        return;
                    }

                    event.setCancelled(event.getSlotType() == InventoryType.SlotType.ARMOR);
                    return;
                }

                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        if (gameHandler.isOngoingGame()) {
            Game ongoingGame = gameHandler.getOngoingGame();
            if (ongoingGame.isPlaying(event.getPlayer().getUniqueId())) {
                gameHandler.getOngoingGame().eliminatePlayer(event.getPlayer(), null);
            } else if (ongoingGame.isSpectating(event.getPlayer().getUniqueId())) {
                gameHandler.getOngoingGame().removeSpectator(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
            if (Samurai.getInstance().getLeftGameMap().isActive(player.getUniqueId())) {
                Samurai.getInstance().getLeftGameMap().setActive(player.getUniqueId(), false);
                player.getInventory().clear();
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!gameHandler.isOngoingGame())
            return;

        if (!gameHandler.getOngoingGame().isPlayingOrSpectating(event.getPlayer().getUniqueId()))
            return;

        if (event.getAction() == Action.PHYSICAL && (event.getClickedBlock().getType() == Material.LEGACY_CROPS || event.getClickedBlock().getType() == Material.LEGACY_SOIL)) {
            event.setCancelled(true);
        }
    }
}
