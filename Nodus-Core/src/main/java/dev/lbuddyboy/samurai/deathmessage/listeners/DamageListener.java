package dev.lbuddyboy.samurai.deathmessage.listeners;

import com.google.common.collect.Maps;
import dev.lbuddyboy.samurai.deathmessage.DeathMessageHandler;
import dev.lbuddyboy.samurai.deathmessage.event.CustomPlayerDamageEvent;
import dev.lbuddyboy.samurai.deathmessage.event.PlayerKilledEvent;
import dev.lbuddyboy.samurai.deathmessage.objects.Damage;
import dev.lbuddyboy.samurai.deathmessage.objects.PlayerDamage;
import dev.lbuddyboy.samurai.deathmessage.util.UnknownDamage;
import dev.lbuddyboy.samurai.util.InventoryUtils;
import dev.lbuddyboy.samurai.util.modsuite.PlayerUtils;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.commands.staff.EOTWCommand;
import dev.lbuddyboy.samurai.map.killstreaks.Killstreak;
import dev.lbuddyboy.samurai.map.killstreaks.PersistentKillstreak;
import dev.lbuddyboy.samurai.map.stats.StatsEntry;
import dev.lbuddyboy.samurai.server.event.PlayerIncreaseKillEvent;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.util.CC;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DamageListener implements Listener {

	// kit-map only
	@Getter private static Map<UUID, String> lastKilled = Maps.newHashMap();
	@Getter private static Map<UUID, ObjectId> lastKilledTeam = Maps.newHashMap();
	@Getter private static Map<UUID, Integer> boosting = Maps.newHashMap();

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player player) {
			CustomPlayerDamageEvent MiniEvent = new CustomPlayerDamageEvent(event, new UnknownDamage(player.getName(), event.getDamage()));

			Samurai.getInstance().getServer().getPluginManager().callEvent(MiniEvent);
			DeathMessageHandler.addDamage(player, MiniEvent.getTrackerDamage());
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		DeathMessageHandler.clearDamage(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		List<Damage> record = DeathMessageHandler.getDamage(event.getEntity());

		if (Samurai.getInstance().getInDuelPredicate().test(event.getEntity())) {
			event.setDeathMessage(null);
			return;
		}

		if (Samurai.getInstance().getTeamWarHandler().getPlayers().contains(event.getEntity())) {
			event.setDeathMessage(null);
			return;
		}

		if (event.getEntity().hasMetadata("gaming")) {
			event.setDeathMessage(null);
			return;
		}

		event.setDeathMessage(null);

		String deathMessage;

		if (record != null) {
			Damage deathCause = record.get(record.size() - 1);

			// Hacky NMS to change the player's killer
			// System.out.println("The milliseconds since death is: " +
			// deathCause.getTimeDifference() + " this should be less than " +
			// TimeUnit.MINUTES.toMillis(1) );
			if (deathCause instanceof PlayerDamage) {
				// System.out.println("Its a playerdamage thing");
				String killerName = ((PlayerDamage) deathCause).getDamager();
				Player killer = Samurai.getInstance().getServer().getPlayerExact(killerName);

				if (killer != null) {
					((CraftPlayer) event.getEntity()).getHandle().aZ = ((CraftPlayer) killer).getHandle();

					Player victim = event.getEntity();

					// Call event
					PlayerKilledEvent killedEvent = new PlayerKilledEvent(killer, victim);
					Samurai.getInstance().getServer().getPluginManager().callEvent(killedEvent);

					// Prevent kill boosting
					// Check if the victim's UUID is the same as the killer's last victim UUID
					// Check if the victim's IP matches the killer's IP

					Team victimTeam = Samurai.getInstance().getTeamHandler().getTeam(victim);
					boolean raidableNaked = false;

					if (victimTeam != null && victimTeam.isRaidable()) {
						if (InventoryUtils.isNaked(victim)) {
							raidableNaked = true;
						}
					}
					if (lastKilled.containsKey(killer.getUniqueId()) && lastKilled.get(killer.getUniqueId()).equals(victim.getName())) {
						boosting.putIfAbsent(killer.getUniqueId(), 0);
						boosting.put(killer.getUniqueId(), boosting.get(killer.getUniqueId()) + 1);
					} else {
						boosting.put(killer.getUniqueId(), 0);
					}

					if (killer.equals(victim)) {
						StatsEntry victimStats = Samurai.getInstance().getMapHandler().getStatsHandler().getStats(victim);

						victimStats.addDeath();
					} else if (raidableNaked) {
						killer.sendMessage(ChatColor.RED + "Boost Check: You've killed a player that's naked and raidable. Point not counted.");
						StatsEntry victimStats = Samurai.getInstance().getMapHandler().getStatsHandler().getStats(victim);

						victimStats.addDeath();
					} else if (killer.getAddress() != null && killer.getAddress().equals(victim.getAddress())) {
						killer.sendMessage(ChatColor.RED + "Boost Check: You've killed a player on the same IP address as you.");

						Team killerTeam = Samurai.getInstance().getTeamHandler().getTeam(killer);

						if (killerTeam != null) {
							killerTeam.createLog(killer.getUniqueId(), "BOOSTED KILL", killer.getName() + " killed " + event.getEntity().getName() + " on the same ip.");
						}
						StatsEntry victimStats = Samurai.getInstance().getMapHandler().getStatsHandler().getStats(victim);

						victimStats.addDeath();
					} else if (boosting.containsKey(killer.getUniqueId()) && boosting.get(killer.getUniqueId()) > 1) {
						killer.sendMessage(ChatColor.RED + "Boost Check: You've killed " + victim.getName() + " " + boosting.get(killer.getUniqueId()) + " times.");

						StatsEntry victimStats = Samurai.getInstance().getMapHandler().getStatsHandler().getStats(victim);

						victimStats.addDeath();

						Team killerTeam = Samurai.getInstance().getTeamHandler().getTeam(killer);

						if (killerTeam != null) {
							killerTeam.createLog(killer.getUniqueId(), "BOOSTED KILL", killer.getName() + " killed " + event.getEntity().getName() + " " + boosting.get(killer.getUniqueId()) + " times.");
						}

					} else {
						Samurai.getInstance().getShardMap().addShards(killer.getUniqueId(), 1);

						killer.sendMessage(CC.WHITE + "You earned " + CC.GOLD + "+1" + CC.WHITE + " shard!");

						Team killerTeam = Samurai.getInstance().getTeamHandler().getTeam(killer);

						if (killerTeam != null) {
							int before = Samurai.getInstance().getTopHandler().getTotalPoints(killerTeam);
							killerTeam.setKills(killerTeam.getKills() + 1);
							int after = Samurai.getInstance().getTopHandler().getTotalPoints(killerTeam);
							killerTeam.createLog(killer.getUniqueId(), "KILL &7(" + before + " -> " + after + ")", killer.getName() + " killed " + event.getEntity().getName());
						}

						if (victimTeam != null) {
							victimTeam.setDeaths(victimTeam.getDeaths() + 1);
						}

						StatsEntry victimStats = Samurai.getInstance().getMapHandler().getStatsHandler().getStats(victim);
						StatsEntry killerStats = Samurai.getInstance().getMapHandler().getStatsHandler().getStats(killer);

						victimStats.addDeath();
						killerStats.addKill();

						lastKilled.put(killer.getUniqueId(), victim.getName());
						if (victimTeam != null) {
							lastKilledTeam.put(killer.getUniqueId(), victimTeam.getUniqueId());
						}

						if (Samurai.getInstance().getMapHandler().isKitMap()) {
							Killstreak killstreak = Samurai.getInstance().getMapHandler().getKillstreakHandler().check(killerStats.getKillstreak());

							if (killstreak != null) {
								killstreak.apply(killer);
								killstreak.apply(killer, killerStats.getKillstreak());

								if (killstreak.getName() != null) {
									Bukkit.broadcastMessage(killer.getDisplayName() + ChatColor.YELLOW + " has gotten the " + ChatColor.RED + killstreak.getName() + ChatColor.YELLOW + " killstreak!");
								}

								List<PersistentKillstreak> persistent = Samurai.getInstance().getMapHandler().getKillstreakHandler().getPersistentKillstreaks(killer, killerStats.getKillstreak());

								for (PersistentKillstreak persistentStreak : persistent) {
									if (persistentStreak.matchesExactly(killerStats.getKillstreak())) {
										if (killstreak.getName() != null) {
											Bukkit.broadcastMessage(killer.getName() + ChatColor.YELLOW + " has gotten the " + ChatColor.RED + killstreak.getName() + ChatColor.YELLOW + " killstreak!");
										}
									}

									persistentStreak.apply(killer);
								}
							}
						}

						Bukkit.getPluginManager().callEvent(new PlayerIncreaseKillEvent(killer));

						event.getDrops().add(Samurai.getInstance().getServerHandler().generateDeathSign(event.getEntity().getName(), killer.getName()));

					}
				}
			}

			deathMessage = deathCause.getDeathMessage();
		} else {
			deathMessage = new UnknownDamage(event.getEntity().getName(), 1).getDeathMessage();
		}

		Player killer = event.getEntity().getKiller();

		Bukkit.getScheduler().runTaskAsynchronously(Samurai.getInstance(), () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (Samurai.getInstance().getToggleDeathMessageMap().areDeathMessagesEnabled(player.getUniqueId())) {
					player.sendMessage(deathMessage);
				} else {
					if (Samurai.getInstance().getTeamHandler().getTeam(player) == null) {
						continue;
					}

					// send them the message if the player who died was on their team
					if (Samurai.getInstance().getTeamHandler().getTeam(event.getEntity()) != null && Samurai.getInstance().getTeamHandler().getTeam(player).equals(Samurai.getInstance().getTeamHandler().getTeam(event.getEntity()))) {
						player.sendMessage(deathMessage);
					}

					// send them the message if the killer was on their team
					if (killer != null) {
						if (Samurai.getInstance().getTeamHandler().getTeam(killer) != null && Samurai.getInstance().getTeamHandler().getTeam(player).equals(Samurai.getInstance().getTeamHandler().getTeam(killer))) {
							player.sendMessage(deathMessage);
						}
					}
				}
			}
		});

		// DeathTracker.logDeath(event.getEntity(), event.getEntity().getKiller());
		DeathMessageHandler.clearDamage(event.getEntity());
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		if (Samurai.getInstance().getMapHandler().isKitMap()) {
			checkKillstreaks(event.getPlayer());
		}
	}

	private void checkKillstreaks(Player player) {
		Bukkit.getScheduler().runTaskLater(Samurai.getInstance(), () -> {
			int killstreak = Samurai.getInstance().getMapHandler().getStatsHandler().getStats(player).getKillstreak();
			List<PersistentKillstreak> persistent = Samurai.getInstance().getMapHandler().getKillstreakHandler().getPersistentKillstreaks(player, killstreak);

			for (PersistentKillstreak persistentStreak : persistent) {
				persistentStreak.apply(player);
			}
		}, 5L);
	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {
		if (!event.getAction().name().startsWith("RIGHT_CLICK")) {
			return;
		}

		ItemStack inHand = event.getPlayer().getItemInHand();
		if (inHand == null) {
			return;
		}

		if (inHand.getType() != Material.NETHER_STAR) {
			return;
		}

		if (!inHand.hasItemMeta()
				|| !inHand.getItemMeta().hasDisplayName()
				|| !inHand.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&5&k! &d&lPotion Refill Token &5&k!"))) {
			return;
		}

		if (EOTWCommand.realFFAStarted()) {
			event.getPlayer().sendMessage(ChatColor.RED + "Potion Refill Tokens are disabled during FFA.");
			return;
		}

		event.getPlayer().setItemInHand(null);

		ItemStack pot = new ItemStack(Material.POTION, 1, (short) 16421);

		while (event.getPlayer().getInventory().addItem(pot).isEmpty()) {
		}
	}

}
