package dev.lbuddyboy.samurai.pvpclasses.pvpclasses;

import dev.lbuddyboy.samurai.custom.ability.items.exotic.KitDisabler;
import dev.lbuddyboy.samurai.deathmessage.DeathMessageHandler;
import dev.lbuddyboy.samurai.deathmessage.trackers.ArrowTracker;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.TimeUtils;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.nametag.FrozenNametagHandler;
import dev.lbuddyboy.samurai.pvpclasses.PvPClass;
import dev.lbuddyboy.samurai.pvpclasses.PvPClassHandler;
import dev.lbuddyboy.samurai.server.SpawnTagHandler;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import dev.lbuddyboy.samurai.util.object.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class HunterClass extends PvPClass {

	private static final int MARK_SECONDS = 5;

	private static final Map<String, Long> lastSpeedUsage = new HashMap<>();
	private static final Map<String, Long> lastJumpUsage = new HashMap<>();
	@Getter
	private static final Map<String, Long> markedPlayers = new ConcurrentHashMap<>();
	@Getter
	private static final Map<String, BukkitTask> markedRunnablePlayers = new ConcurrentHashMap<>();
	@Getter
	private static final Map<String, Set<Pair<String, Long>>> markedBy = new HashMap<>();

	public HunterClass() {
		super("Hunter", 15, Arrays.asList(Material.SUGAR, Material.FEATHER));
	}

	@Override
	public boolean qualifies(PlayerInventory armor) {
		return wearingAllArmor(armor) &&
				armor.getHelmet().getType() == Material.TURTLE_HELMET &&
				armor.getChestplate().getType() == Material.LEATHER_CHESTPLATE &&
				armor.getLeggings().getType() == Material.LEATHER_LEGGINGS &&
				armor.getBoots().getType() == Material.LEATHER_BOOTS;
	}

	@Override
	public void apply(Player player) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 2));
		player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, PotionEffect.INFINITE_DURATION, 1));
		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, PotionEffect.INFINITE_DURATION, 0));
	}

	@Override
	public void tick(Player player) {
		if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 2));
		}

		if (!player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, PotionEffect.INFINITE_DURATION, 1));
		}

		if (!player.hasPotionEffect(PotionEffectType.JUMP)) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, PotionEffect.INFINITE_DURATION, 0));
		}

		super.tick(player);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityArrowHit(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
			Arrow arrow = (Arrow) event.getDamager();
			final Player victim = (Player) event.getEntity();

			if (!(arrow.getShooter() instanceof Player)) {
				return;
			}

			if (arrow.hasMetadata("CrossBow")) {

				Player shooter = (Player) arrow.getShooter();
				float pullback = arrow.getMetadata("Pullback").get(0).asFloat();

				if (!PvPClassHandler.hasKitOn(shooter, this)) {
					return;
				}

				if (KitDisabler.getDisabled().onCooldown(shooter.getUniqueId())) {
					shooter.sendMessage(CC.translate("&c[Kit Disabler] Your kit ability is disabled for " + KitDisabler.getDisabled().getRemaining(shooter) + "."));
					return;
				}

				// 2 hearts for a marked shot
				// 1.5 hearts for a marking / unmarked shot.
				int damage = isMarked(victim) ? 4 : 3; // Ternary for getting damage!

				// If the bow isn't 100% pulled back we do 1 heart no matter what.
				if (pullback < 0.5F) {
					damage = 2; // 1 heart
				}

				if (victim.getHealth() - damage <= 0D) {
					event.setCancelled(true);
				} else {
					event.setDamage(0D);
				}

				// The 'ShotFromDistance' metadata is applied in the deathmessage module.
				Location shotFrom = (Location) arrow.getMetadata("ShotFromDistance").get(0).value();
				double distance = shotFrom.distance(victim.getLocation());

				DeathMessageHandler.addDamage(victim, new ArrowTracker.ArrowDamageByPlayer(victim.getName(), damage, ((Player) arrow.getShooter()).getName(), shotFrom, distance));
				victim.setHealth(Math.max(0D, victim.getHealth() - damage));

				if (PvPClassHandler.hasKitOn(victim, this)) {
					shooter.sendMessage(ChatColor.YELLOW + "[" + ChatColor.DARK_PURPLE + "Arrow Range" + ChatColor.YELLOW + " (" + ChatColor.RED + (int) distance + ChatColor.YELLOW + ")] " + ChatColor.RED + "Cannot mark other Hunters. " + ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "(" + damage / 2 + " heart" + ((damage / 2 == 1) ? "" : "s") + ")");
				} else if (pullback >= 0.5F) {
					shooter.sendMessage(ChatColor.YELLOW + "[" + ChatColor.DARK_PURPLE + "Arrow Range" + ChatColor.YELLOW + " (" + ChatColor.RED + (int) distance + ChatColor.YELLOW + ")] " + ChatColor.YELLOW + "Marked player for " + MARK_SECONDS + " seconds. " + ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "(" + damage / 2 + " heart" + ((damage / 2 == 1) ? "" : "s") + ")");

					// Only send the message if they're not already marked.
					if (!isMarked(victim)) {
						victim.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Marked! " + ChatColor.YELLOW + "A hunter has shot you (Bleed Effect) for " + MARK_SECONDS + " seconds.");
						victim.getWorld().playEffect(victim.getEyeLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
					}

					PotionEffect invis = null;

					for (PotionEffect potionEffect : victim.getActivePotionEffects()) {
						if (potionEffect.getType().equals(PotionEffectType.INVISIBILITY)) {
							invis = potionEffect;
							break;
						}
					}

					if (invis != null) {
						PvPClass playerClass = PvPClassHandler.getPvPClass(victim);

						victim.removePotionEffect(invis.getType());

						final PotionEffect invisFinal = invis;

						/* Handle returning their invisibility after the archer tag is done */
						if (playerClass instanceof MinerClass) {
							/* Queue player to have invis returned. (MinerClass takes care of this) */
							((MinerClass) playerClass).getInvis().put(victim.getName(), MARK_SECONDS);
						} else {
							/* player has no class but had invisibility, return it after their tag expires */
							new BukkitRunnable() {

								@Override
								public void run() {
									if (invisFinal.getDuration() > 1_000_000) {
										return;
									}

									victim.addPotionEffect(invisFinal);
								}

							}.runTaskLater(Samurai.getInstance(), (MARK_SECONDS * 20) + 5);
						}
					}

					getMarkedPlayers().put(victim.getName(), System.currentTimeMillis() + (MARK_SECONDS * 1000));

					getMarkedBy().putIfAbsent(shooter.getName(), new HashSet<>());
					getMarkedBy().get(shooter.getName()).add(new Pair<>(victim.getName(), System.currentTimeMillis() + (MARK_SECONDS * 1000)));

					if (getMarkedRunnablePlayers().containsKey(victim.getName())) {
						getMarkedRunnablePlayers().get(victim.getName()).cancel();
					}

					FrozenNametagHandler.reloadPlayer(victim);

					new BukkitRunnable() {
						@Override
						public void run() {
							if (victim.isOnline()) {
								FrozenNametagHandler.reloadPlayer(victim);
							}
						}
					}.runTaskLater(Samurai.getInstance(), (MARK_SECONDS * 20) + 5);

					getMarkedRunnablePlayers().put(victim.getName(), new BukkitRunnable() {
						@Override
						public void run() {
							if (!victim.isOnline()) {
								cancel();
								return;
							}
							if (victim.isDead()) {
								cancel();
								return;
							}
							if (isMarked(victim)) {
								victim.getWorld().playEffect(victim.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
								victim.damage(1.25);
							} else {
								cancel();
							}
						}
					}.runTaskTimer(Samurai.getInstance(), 20, 30));
				} else {
					shooter.sendMessage(ChatColor.YELLOW + "[" + ChatColor.DARK_PURPLE + "Arrow Range" + ChatColor.YELLOW + " (" + ChatColor.RED + (int) distance + ChatColor.YELLOW + ")] " + ChatColor.RED + "Bow wasn't fully drawn back. " + ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "(" + damage / 2 + " heart" + ((damage / 2 == 1) ? "" : "s") + ")");
				}
			}
		}
	}

	@EventHandler
	public void onEntityShootBow(EntityShootBowEvent event) {
		if (event.getBow() == null) return;
		if (event.getBow().getType() == Material.CROSSBOW) {
			event.getProjectile().setMetadata("CrossBow", new FixedMetadataValue(Samurai.getInstance(), true));
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
			return (true);
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
			return (false);
		}
	}

	public static boolean isMarked(Player player) {
		return (getMarkedPlayers().containsKey(player.getName()) && getMarkedPlayers().get(player.getName()) > System.currentTimeMillis());
	}

}
