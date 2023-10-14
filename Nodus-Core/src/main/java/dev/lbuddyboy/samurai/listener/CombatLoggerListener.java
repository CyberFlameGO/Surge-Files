package dev.lbuddyboy.samurai.listener;

import com.mojang.authlib.GameProfile;
import com.mongodb.BasicDBObject;
import dev.lbuddyboy.samurai.commands.staff.LastInvCommand;
import dev.lbuddyboy.samurai.deathmessage.listeners.DamageListener;
import dev.lbuddyboy.samurai.map.offline.OfflineInventory;
import dev.lbuddyboy.samurai.server.deathban.DeathbanListener;
import lombok.AllArgsConstructor;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.map.killstreaks.Killstreak;
import dev.lbuddyboy.samurai.map.killstreaks.PersistentKillstreak;
import dev.lbuddyboy.samurai.map.stats.StatsEntry;
import dev.lbuddyboy.samurai.server.SpawnTagHandler;
import dev.lbuddyboy.samurai.server.event.PlayerIncreaseKillEvent;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.ItemUtils;
import dev.lbuddyboy.samurai.util.LocationSerializer;
import net.minecraft.network.protocol.game.PacketPlayOutEntity;
import net.minecraft.network.protocol.game.PacketPlayOutRespawn;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.PlayerInteractManager;
import net.minecraft.world.entity.player.EntityHuman;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 26/01/2022 / 12:37 AM
 * SteelHCF-master / net.frozenorb.Samurai.listener
 */
public class CombatLoggerListener implements Listener {

    public static final String COMBAT_LOGGER_METADATA = "Samurai_LOGGER";
    private final Map<UUID, CombatLogger> combatLoggers = new ConcurrentHashMap<>();

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        Entity loggerEntity = event.getEntity();
        if (!combatLoggers.containsKey(loggerEntity.getUniqueId())) return;
        CombatLogger logger = this.combatLoggers.get(loggerEntity.getUniqueId());
        Location location = loggerEntity.getLocation();
        Player killer = logger.getVillager().getKiller();
        StatsEntry loggerStats = Samurai.getInstance().getMapHandler().getStatsHandler().getStats(logger.getUuid());

        combatLoggers.remove(loggerEntity.getUniqueId());

        if (!logger.getName().equals(event.getEntity().getCustomName())) {
            Samurai.getInstance().getLogger().warning("Combat logger name doesn't match metadata for " + logger.getName() + " (" + event.getEntity().getCustomName() + ")");
        }

        Samurai.getInstance().getLogger().info(logger.getName() + "'s combat logger at (" + event.getEntity().getLocation().getBlockX() + ", " + event.getEntity().getLocation().getBlockY() + ", " + event.getEntity().getLocation().getBlockZ() + ") died.");

        // Deathban the player
        Samurai.getInstance().getDeathbanMap().deathban(logger.getUuid(), logger.getDeathbanSeconds());

        // Increment players deaths
        loggerStats.addDeath();

        Team team = Samurai.getInstance().getTeamHandler().getTeam(logger.getUuid());

        // Take away DTR.
        if (team != null && ((Villager) loggerEntity).getKiller() != null) {
            team.playerDeath(logger.getName(), team.getDTR(), Samurai.getInstance().getServerHandler().getDTRLoss(event.getEntity().getLocation()), ((Villager) loggerEntity).getKiller());
        } else {
            if (team != null) {
                team.playerDeath(logger.getName(), team.getDTR(), Samurai.getInstance().getServerHandler().getDTRLoss(event.getEntity().getLocation()), null);
            }
        }

        // Drop the player's items.
        for (ItemStack item : logger.getArmor()) {
            event.getDrops().add(item);
        }
        for (ItemStack item : logger.getContents()) {
            event.getDrops().add(item);
        }

        // store the death amount -- we'll use this later on.
        int victimKills = loggerStats.getKills();

        if (event.getEntity().getKiller() != null) {
            // give them a kill
            StatsEntry killerStats = Samurai.getInstance().getMapHandler().getStatsHandler().getStats(event.getEntity().getKiller().getUniqueId());

            killerStats.addKill();

            // store the kill amount -- we'll use this later on.
            int killerKills = killerStats.getKills();

            String deathMessage = ChatColor.RED + logger.getName() + ChatColor.DARK_RED + "[" + victimKills + "]" + ChatColor.GRAY + " (Combat-Logger)" + ChatColor.YELLOW + " was slain by " + ChatColor.RED + event.getEntity().getKiller().getName() + ChatColor.DARK_RED + "[" + killerKills + "]" + ChatColor.YELLOW + ".";

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (Samurai.getInstance().getToggleDeathMessageMap().areDeathMessagesEnabled(player.getUniqueId())) {
                    player.sendMessage(deathMessage);
                } else {
                    if (Samurai.getInstance().getTeamHandler().getTeam(player.getUniqueId()) == null) {
                        continue;
                    }

                    if (Samurai.getInstance().getTeamHandler().getTeam(logger.getUuid()) != null
                            && Samurai.getInstance().getTeamHandler().getTeam(logger.getUuid()).equals(Samurai.getInstance().getTeamHandler().getTeam(player.getUniqueId()))) {
                        player.sendMessage(deathMessage);
                    }

                    if (Samurai.getInstance().getTeamHandler().getTeam(event.getEntity().getKiller().getUniqueId()) != null
                            && Samurai.getInstance().getTeamHandler().getTeam(event.getEntity().getKiller().getUniqueId()).equals(Samurai.getInstance().getTeamHandler().getTeam(player.getUniqueId()))) {
                        player.sendMessage(deathMessage);
                    }
                }
            }

            // Add the death sign.

            event.getDrops().add(Samurai.getInstance().getServerHandler().generateDeathSign(logger.getName(), event.getEntity().getKiller().getName()));

        } else {
            String deathMessage = ChatColor.RED + logger.getName() + ChatColor.DARK_RED + "[" + victimKills + "]" + ChatColor.GRAY + " (Combat-Logger)" + ChatColor.YELLOW + " died.";

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (Samurai.getInstance().getToggleDeathMessageMap().areDeathMessagesEnabled(player.getUniqueId())) {
                    player.sendMessage(deathMessage);
                } else {
                    if (Samurai.getInstance().getTeamHandler().getTeam(player.getUniqueId()) == null) {
                        continue;
                    }

                    if (Samurai.getInstance().getTeamHandler().getTeam(logger.getUuid()) != null
                            && Samurai.getInstance().getTeamHandler().getTeam(logger.getUuid()).equals(Samurai.getInstance().getTeamHandler().getTeam(player.getUniqueId()))) {
                        player.sendMessage(deathMessage);
                    }
                }
            }
        }

        Player target = Samurai.getInstance().getServer().getPlayer(logger.getUuid());

        if (target == null) {
            // Create an entity to load the player data
            EntityPlayer entity = new EntityPlayer(
                    ((CraftServer) Bukkit.getServer()).getServer(),
                    ((CraftWorld) loggerEntity.getWorld()).getHandle(),
                    new GameProfile(logger.getUuid(), logger.getName()));
            target = entity.getBukkitEntity();

            if (target != null) {
                target.loadData();
            }
        }

        if (target != null) {
            EntityHuman humanTarget = ((CraftHumanEntity) target).getHandle();

            target.getInventory().setContents(logger.contents);
            target.getInventory().setArmorContents(logger.armor);

            DeathbanListener.insertDeath(target, event.getEntity().getKiller());

            target.getInventory().clear();
            target.getInventory().setArmorContents(null);
            target.saveData();
        }

        SpawnTagHandler.removeTag(logger.getName());
        LastInvCommand.recordInventory(logger.getUuid(), logger.contents, logger.armor);

        event.getEntity().remove();

        loggerEntity.getWorld().strikeLightning(location);
        Samurai.getInstance().getDiedMap().setActive(logger.getUuid(), true);

        OfflineInventory inventory = Samurai.getInstance().getOfflineHandler().getOfflineInventories().getOrDefault(logger.getUuid(), null);
        if (inventory == null) return;

        inventory.setArmor(new ItemStack[4]);
        inventory.setContents(new ItemStack[36]);
        inventory.setExtras(new ItemStack[0]);
        Samurai.getInstance().getOfflineHandler().saveOffline(inventory);
    }

    // Prevent trading with the logger.
    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (combatLoggers.containsKey(event.getRightClicked().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    // Kill loggers when their chunk unloads
    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (combatLoggers.containsKey(entity.getUniqueId()) && !entity.isDead()) {
                entity.remove();
            }
        }
    }

    // Don't let the NPC go through portals
    @EventHandler
    public void onEntityPortal(EntityPortalEvent event) {
        if (combatLoggers.containsKey(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    // Despawn the NPC when its owner joins.
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        for (CombatLogger logger : combatLoggers.values()) {
            Villager villager = logger.getVillager();

            if (villager.isCustomNameVisible() && Objects.equals(ChatColor.stripColor(villager.getCustomName()), event.getPlayer().getName())) {
                event.getPlayer().teleport(villager);
                villager.remove();
                combatLoggers.remove(villager.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (!combatLoggers.containsKey(entity.getUniqueId())) {
            return;
        }

        Player damager = null;

        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile projectile) {

            if (projectile.getShooter() instanceof Player) {
                damager = (Player) projectile.getShooter();
            }
        }

        if (damager != null) {
            CombatLogger logger = this.combatLoggers.get(entity.getUniqueId());

            if (DTRBitmask.SAFE_ZONE.appliesAt(damager.getLocation()) || DTRBitmask.SAFE_ZONE.appliesAt(event.getEntity().getLocation())) {
                event.setCancelled(true);
                return;
            }

            if (Samurai.getInstance().getPvPTimerMap().hasTimer(damager.getUniqueId())) {
                event.setCancelled(true);
                return;
            }

            Team team = Samurai.getInstance().getTeamHandler().getTeam(logger.getUuid());

            if (team != null && team.isMember(damager.getUniqueId())) {
                event.setCancelled(true);
                return;
            }

            SpawnTagHandler.addOffensiveSeconds(damager, SpawnTagHandler.getMaxTagTime());
        }
    }

    // Prevent combatloggers from activating a pressure plate
    @EventHandler
    public void onEntityPressurePlate(EntityInteractEvent event) {
        if (event.getBlock().getType() == Material.STONE_PRESSURE_PLATE && event.getEntity() instanceof Villager && event.getEntity().hasMetadata(COMBAT_LOGGER_METADATA)) {
            event.setCancelled(true); // block is stone, entity is a combat tagged villager
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (Samurai.getInstance().getArenaHandler().isDeathbanned(player.getUniqueId())) return;
        if (SOTWCommand.isSOTWTimer() && !SOTWCommand.hasSOTWEnabled(player.getUniqueId())) return;
        if (Samurai.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) return;
        if (Samurai.getInstance().getStartingPvPTimerMap().get(player.getUniqueId())) return;
        if (player.getNearbyEntities(20, 20, 20)
                .stream()
                .filter(entity -> entity instanceof Player)
                .map(entity -> ((Player) entity)).noneMatch(p -> {
                    Team team = Samurai.getInstance().getTeamHandler().getTeam(p);
                    if (team == null) return false;

                    return team.getOnlineMembers().contains(player);
                }) && !SpawnTagHandler.isTagged(player)) return;
        if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) return;
        if (player.hasMetadata("loggedout")) return;
        if (player.hasMetadata("gaming") && player.getWorld() == Samurai.getInstance().getMapHandler().getGameHandler().getWorld()) return;

        CombatLogger logger = new CombatLogger(
                player.getName(),
                player.getUniqueId(),
                player.getInventory().getArmorContents(),
                player.getInventory().getStorageContents(),
                player.getWorld().spawn(player.getLocation(), Villager.class),
                Samurai.getInstance().getServerHandler().getDeathban(player),
                player.getAddress()
        );

        Villager villager = logger.getVillager();

        villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 100));
//        villager.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 100));

        if (event.getPlayer().hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
            for (PotionEffect potionEffect : event.getPlayer().getActivePotionEffects()) {
                if (potionEffect.getType().equals(PotionEffectType.FIRE_RESISTANCE)) {
                    villager.addPotionEffect(potionEffect);
                    break;
                }
            }
        }

        villager.setCustomName(ChatColor.YELLOW + event.getPlayer().getName());
        villager.setCustomNameVisible(true);
        villager.setFallDistance(event.getPlayer().getFallDistance());
        villager.setRemoveWhenFarAway(false);
        villager.setVelocity(event.getPlayer().getVelocity());

        this.combatLoggers.put(villager.getUniqueId(), logger);

        if (villager.getWorld().getEnvironment() == World.Environment.THE_END) {
            // check every second if the villager fell out of the world and kill the player if that happened.
            new BukkitRunnable() {

                int tries = 0;

                @Override
                public void run() {
                    if (villager.getLocation().getBlockY() >= 0) {
                        tries++;

                        if (tries == 30) {
                            cancel();
                        }
                        return;
                    }

                    Samurai.getInstance().getDeathbanMap().deathban(logger.getUuid(), logger.getDeathbanSeconds());

                    // Increment players deaths
                    Samurai.getInstance().getMapHandler().getStatsHandler().getStats(logger.getUuid()).addDeath();

                    Team team = Samurai.getInstance().getTeamHandler().getTeam(logger.getUuid());

                    // Take away DTR.
                    if (team != null && villager.getKiller() != null) {
                        team.playerDeath(logger.getName(), team.getDTR(), Samurai.getInstance().getServerHandler().getDTRLoss(villager.getLocation()), villager.getKiller());
                    } else {
                        if (team != null) {
                            team.playerDeath(logger.getName(), team.getDTR(), Samurai.getInstance().getServerHandler().getDTRLoss(villager.getLocation()), null);
                        }
                    }

                    // store the death amount -- we'll use this later on.
                    int victimKills = Samurai.getInstance().getMapHandler().getStatsHandler().getStats(player.getUniqueId()).getKills();

                    String deathMessage = ChatColor.RED + logger.getName() + ChatColor.DARK_RED + "[" + victimKills + "]" + ChatColor.GRAY + " (Combat-Logger)" + ChatColor.YELLOW + " died.";
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (Samurai.getInstance().getToggleDeathMessageMap().areDeathMessagesEnabled(player.getUniqueId())) {
                            player.sendMessage(deathMessage);
                        } else {
                            if (team != null && team == Samurai.getInstance().getTeamHandler().getTeam(player.getUniqueId())) {
                                player.sendMessage(deathMessage);
                            }
                        }
                    }

                    Player target = Samurai.getInstance().getServer().getPlayer(logger.getUuid());

                    if (target == null) {
                        // Create an entity to load the player data
                        EntityPlayer entity = new EntityPlayer(
                                ((CraftServer) Bukkit.getServer()).getServer(),
                                ((CraftWorld) logger.getVillager().getWorld()).getHandle(),
                                new GameProfile(logger.getUuid(), logger.getName()));
                        target = entity.getBukkitEntity();

                        if (target != null) {
                            target.loadData();
                        }
                    }

                    if (target != null) {
                        target.getInventory().clear();
                        target.getInventory().setArmorContents(null);

                        DeathbanListener.insertDeath(target, villager.getKiller());
                        target.saveData();
                    }

                    LastInvCommand.recordInventory(logger.getUuid(), logger.contents, logger.armor);

                    cancel();
                    villager.remove();
                    Samurai.getInstance().getDiedMap().setActive(logger.getUuid(), true);
                    combatLoggers.remove(villager.getUniqueId());
                    SpawnTagHandler.removeTag(logger.getName());

                    OfflineInventory inventory = Samurai.getInstance().getOfflineHandler().getOfflineInventories().getOrDefault(logger.getUuid(), null);
                    if (inventory == null) return;

                    inventory.setArmor(new ItemStack[4]);
                    inventory.setContents(new ItemStack[36]);
                    inventory.setExtras(new ItemStack[0]);
                    Samurai.getInstance().getOfflineHandler().saveOffline(inventory);
                }

            }.runTaskTimer(Samurai.getInstance(), 0L, 20L);
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                if (!villager.isDead()) {
                    villager.remove();
                    combatLoggers.remove(villager.getUniqueId());
                }
            }

        }.runTaskLater(Samurai.getInstance(), 20 * 15);
    }

    @AllArgsConstructor
    @Getter
    private static class CombatLogger {

        private String name;
        private UUID uuid;
        private ItemStack[] armor, contents;
        private Villager villager;
        private long deathbanSeconds;
        private InetSocketAddress address;

    }

}
