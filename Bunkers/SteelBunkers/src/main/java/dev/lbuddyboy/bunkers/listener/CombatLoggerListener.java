package dev.lbuddyboy.bunkers.listener;

import com.mojang.authlib.GameProfile;
import dev.lbuddyboy.bunkers.Bunkers;
import dev.lbuddyboy.bunkers.game.user.GameUser;
import dev.lbuddyboy.bunkers.team.Team;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.player.EntityHuman;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftHumanEntity;
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
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 26/01/2022 / 12:37 AM
 * SteelHCF-master / net.frozenorb.Bunkers.listener
 */
public class CombatLoggerListener implements Listener {

    public static final String COMBAT_LOGGER_METADATA = "Bunkers_LOGGER";
    private final Map<UUID, CombatLogger> combatLoggers = new ConcurrentHashMap<>();

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        Entity loggerEntity = event.getEntity();
        if (!combatLoggers.containsKey(loggerEntity.getUniqueId())) return;
        CombatLogger logger = this.combatLoggers.get(loggerEntity.getUniqueId());
        Location location = loggerEntity.getLocation();
        Player killer = logger.getVillager().getKiller();

        combatLoggers.remove(loggerEntity.getUniqueId());

        if (!logger.getName().equals(event.getEntity().getCustomName())) {
            Bunkers.getInstance().getLogger().warning("Combat logger name doesn't match metadata for " + logger.getName() + " (" + event.getEntity().getCustomName() + ")");
        }

        Bunkers.getInstance().getLogger().info(logger.getName() + "'s combat logger at (" + event.getEntity().getLocation().getBlockX() + ", " + event.getEntity().getLocation().getBlockY() + ", " + event.getEntity().getLocation().getBlockZ() + ") died.");

        Team team = Bunkers.getInstance().getTeamHandler().getTeam(logger.getUuid());

        GameUser victimUser = Bunkers.getInstance().getGameHandler().getGameUser(logger.getUuid());

        victimUser.setDeaths(victimUser.getDeaths() + 1);
        team.setDtr(team.getDtr() - 1);

        // Drop the player's items.
        for (ItemStack item : logger.getArmor()) {
            event.getDrops().add(item);
        }
        for (ItemStack item : logger.getContents()) {
            event.getDrops().add(item);
        }

        // store the death amount -- we'll use this later on.
        int victimKills = victimUser.getKills();

        if (event.getEntity().getKiller() != null) {
            // give them a kill
            GameUser killerUser = Bunkers.getInstance().getGameHandler().getGameUser(logger.getUuid());

            killerUser.setKills(killerUser.getKills() + 1);

            // store the kill amount -- we'll use this later on.
            int killerKills = killerUser.getKills();

            String deathMessage = ChatColor.RED + logger.getName() + ChatColor.DARK_RED + "[" + victimKills + "]" + ChatColor.GRAY + " (Combat-Logger)" + ChatColor.YELLOW + " was slain by " + ChatColor.RED + event.getEntity().getKiller().getName() + ChatColor.DARK_RED + "[" + killerKills + "]" + ChatColor.YELLOW + ".";

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(deathMessage);
            }
        } else {
            String deathMessage = ChatColor.RED + logger.getName() + ChatColor.DARK_RED + "[" + victimKills + "]" + ChatColor.GRAY + " (Combat-Logger)" + ChatColor.YELLOW + " died.";

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(deathMessage);
            }
        }

        Player target = Bunkers.getInstance().getServer().getPlayer(logger.getUuid());

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

            target.getInventory().clear();
            target.getInventory().setArmorContents(null);
            target.saveData();
        }

        event.getEntity().remove();

        loggerEntity.getWorld().strikeLightning(location);

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

            if (Bunkers.getInstance().getGameHandler().isGracePeriod()) {
                event.setCancelled(true);
                return;
            }

            Team team = Bunkers.getInstance().getTeamHandler().getTeam(logger.getUuid());

            if (team != null && team.getMembers().contains(damager.getUniqueId())) {
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

        if (Bunkers.getInstance().getSpectatorHandler().isSpectator(player)) return;

        CombatLogger logger = new CombatLogger(
                player.getName(),
                player.getUniqueId(),
                player.getInventory().getArmorContents(),
                player.getInventory().getStorageContents(),
                player.getWorld().spawn(player.getLocation(), Villager.class),
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

                    GameUser user = Bunkers.getInstance().getGameHandler().getGameUser(player.getUniqueId());

                    // Increment players deaths
                    user.setDeaths(user.getDeaths() + 1);

                    Team team = Bunkers.getInstance().getTeamHandler().getTeam(logger.getUuid());

                    team.setDtr(team.getDtr() - 1);

                    // store the death amount -- we'll use this later on.
                    int victimKills = user.getKills();

                    String deathMessage = ChatColor.RED + logger.getName() + ChatColor.DARK_RED + "[" + victimKills + "]" + ChatColor.GRAY + " (Combat-Logger)" + ChatColor.YELLOW + " died.";
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(deathMessage);
                    }

                    Player target = Bunkers.getInstance().getServer().getPlayer(logger.getUuid());

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

                        target.saveData();
                    }

                    cancel();
                    villager.remove();
                    combatLoggers.remove(villager.getUniqueId());
                    SpawnTagHandler.removeTag(logger.getName());
                }

            }.runTaskTimer(Bunkers.getInstance(), 0L, 20L);
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                if (!villager.isDead()) {
                    villager.remove();
                    combatLoggers.remove(villager.getUniqueId());
                }
            }

        }.runTaskLater(Bunkers.getInstance(), 20 * 120);
    }

    @AllArgsConstructor
    @Getter
    private static class CombatLogger {

        private String name;
        private UUID uuid;
        private ItemStack[] armor, contents;
        private Villager villager;
        private InetSocketAddress address;

    }

}
