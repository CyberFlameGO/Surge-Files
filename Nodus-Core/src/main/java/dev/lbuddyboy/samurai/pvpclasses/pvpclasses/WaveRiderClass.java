package dev.lbuddyboy.samurai.pvpclasses.pvpclasses;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.api.FoxtrotConfiguration;
import dev.lbuddyboy.samurai.custom.ability.items.Rocket;
import dev.lbuddyboy.samurai.custom.ability.items.exotic.KitDisabler;
import dev.lbuddyboy.samurai.deathmessage.DeathMessageHandler;
import dev.lbuddyboy.samurai.deathmessage.trackers.ArrowTracker;
import dev.lbuddyboy.samurai.deathmessage.trackers.TridentTracker;
import dev.lbuddyboy.samurai.nametag.FrozenNametagHandler;
import dev.lbuddyboy.samurai.pvpclasses.PvPClass;
import dev.lbuddyboy.samurai.pvpclasses.PvPClassHandler;
import dev.lbuddyboy.samurai.server.SpawnTagHandler;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.TimeUtils;
import dev.lbuddyboy.samurai.util.object.Pair;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class WaveRiderClass extends PvPClass {

    private static final int MARK_SECONDS = 5;

    @Getter private static Map<String, Long> lastEffectUsage = new ConcurrentHashMap<>();
    @Getter private static Map<String, Float> energy = new ConcurrentHashMap<>();
    public static final int EFFECT_COOLDOWN = 10 * 1000;
    public static final float MAX_ENERGY = 100;
    public static final float ENERGY_REGEN_PER_SECOND = 1;
    private static final Map<String, Long> lastSpeedUsage = new HashMap<>();
    private static final Map<String, Long> lastJumpUsage = new HashMap<>();

    public WaveRiderClass() {
        super("WaveRider", 15, Arrays.asList(Material.SUGAR, Material.FEATHER));

        new BukkitRunnable() {

            @Override
            public void run() {
                for (Player player : Samurai.getInstance().getServer().getOnlinePlayers()) {
                    if (!PvPClassHandler.hasKitOn(player, WaveRiderClass.this) || Samurai.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
                        continue;
                    }

                    if (energy.containsKey(player.getName())) {
                        if (energy.get(player.getName()) == MAX_ENERGY) {
                            continue;
                        }

                        energy.put(player.getName(), Math.min(MAX_ENERGY, energy.get(player.getName()) + ENERGY_REGEN_PER_SECOND));
                    } else {
                        energy.put(player.getName(), 0F);
                    }

                    int manaInt = energy.get(player.getName()).intValue();

                    if (manaInt % 10 == 0) {
                        player.sendMessage(ChatColor.AQUA + "Rider Energy: " + ChatColor.GREEN + manaInt);
                    }
                }
            }

        }.runTaskTimer(Samurai.getInstance(), 15L, 20L);
    }

    @Override
    public boolean qualifies(PlayerInventory armor) {
        return wearingAllArmor(armor) &&
                armor.getHelmet().getType() == Material.LEATHER_HELMET &&
                armor.getChestplate().getType() == Material.DIAMOND_CHESTPLATE &&
                armor.getLeggings().getType() == Material.DIAMOND_LEGGINGS &&
                armor.getBoots().getType() == Material.LEATHER_BOOTS;
    }

    @Override
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, PotionEffect.INFINITE_DURATION, 0));
    }

    @Override
    public void tick(Player player) {
        if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 1));
        }

        if (!player.hasPotionEffect(PotionEffectType.REGENERATION)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, PotionEffect.INFINITE_DURATION, 0));
        }

        super.tick(player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityArrowHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player victim && event.getDamager() instanceof Trident trident) {
            if (!(trident.getShooter() instanceof Player shooter)) return;

            if (!PvPClassHandler.hasKitOn(shooter, this)) {
                return;
            }

            if (!canUseMark(shooter, victim)) return;

            // 2 hearts for a marked shot
            // 1.5 hearts for a marking / unmarked shot.
            int damage = 6; // Ternary for getting damage!

            if (victim.getHealth() - damage <= 0D) {
                event.setCancelled(true);
            } else {
                event.setDamage(0D);
            }

            // The 'ShotFromDistance' metadata is applied in the deathmessage module.
            Location shotFrom = (Location) trident.getMetadata("ShotFromDistance").get(0).value();
            double distance = shotFrom.distance(victim.getLocation());

            DeathMessageHandler.addDamage(victim, new TridentTracker.TridentDamageByPlayer(victim.getName(), damage, ((Player) trident.getShooter()).getName(), shotFrom, distance));
            victim.setHealth(Math.max(0D, victim.getHealth() - damage));
        }
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Trident trident) {
            if (!(trident.getShooter() instanceof Player shooter)) return;

            if (!PvPClassHandler.hasKitOn(shooter, this)) {
                return;
            }

            if (!shooter.isSneaking()) return;

            shooter.setVelocity(new Vector(
                    shooter.getLocation().getDirection().getX() * 2.15,
                    shooter.getLocation().getDirection().getY() * 2.15,
                    shooter.getLocation().getDirection().getZ() * 2.15));
            Rocket.rocket.add(shooter.getUniqueId());
        }
    }

    @Override
    public boolean itemConsumed(Player player, Material material) {
        if (material == Material.SUGAR) {
            if (lastSpeedUsage.containsKey(player.getName()) && lastSpeedUsage.get(player.getName()) > System.currentTimeMillis()) {
                long millisLeft = lastSpeedUsage.get(player.getName()) - System.currentTimeMillis();
                String msg = TimeUtils.formatIntoDetailedString((int) millisLeft / 1000);

                player.sendMessage(ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
                return (false);
            }

            lastSpeedUsage.put(player.getName(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 3), true);
        } else {
            if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
                player.sendMessage(ChatColor.RED + "You can't use this in spawn!");
                return (false);
            }

            if (lastJumpUsage.containsKey(player.getName()) && lastJumpUsage.get(player.getName()) > System.currentTimeMillis()) {
                long millisLeft = lastJumpUsage.get(player.getName()) - System.currentTimeMillis();
                String msg = TimeUtils.formatIntoDetailedString((int) millisLeft / 1000);

                player.sendMessage(ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
                return (false);
            }

            lastJumpUsage.put(player.getName(), System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 5, 6));

            SpawnTagHandler.addPassiveSeconds(player, SpawnTagHandler.getMaxTagTime());
        }

        return (true);
    }

    private boolean canUseMark(Player player, Player victim) {
        if (KitDisabler.getDisabled().onCooldown(player.getUniqueId())) {
            player.sendMessage(CC.translate("&c[Kit Disabler] Your kit ability is disabled for " + KitDisabler.getDisabled().getRemaining(player) + "."));
            return false;
        }
        if (Samurai.getInstance().getTeamHandler().getTeam(player) != null) {
            Team team = Samurai.getInstance().getTeamHandler().getTeam(player);

            int amount = 0;
            for (Player member : team.getOnlineMembers()) {
                if (PvPClassHandler.hasKitOn(member, this)) {
                    amount++;

                    if (amount > 1) {
                        break;
                    }
                }
            }

            if (amount > 1) {
                player.sendMessage(ChatColor.RED + "Your team has too many wave riders. Trident damage was not effective.");
                return false;
            }
        }

        return true;
    }

}
