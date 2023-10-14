package dev.lbuddyboy.samurai.events.citadel.listeners;

import dev.lbuddyboy.samurai.events.citadel.CitadelHandler;
import dev.lbuddyboy.samurai.events.citadel.events.CitadelActivatedEvent;
import dev.lbuddyboy.samurai.events.citadel.events.CitadelCapturedEvent;
import dev.lbuddyboy.samurai.api.HourEvent;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.events.events.EventActivatedEvent;
import dev.lbuddyboy.samurai.events.events.EventCapturedEvent;
import dev.lbuddyboy.samurai.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitTask;

import java.text.SimpleDateFormat;

public class CitadelListener implements Listener {

	private BukkitTask strengthTask;
	private BukkitTask resTask;
	private BukkitTask death1Task;
	private BukkitTask death2Task;

	@EventHandler
	public void onKOTHActivated(EventActivatedEvent event) {
		if (event.getEvent().getName().equalsIgnoreCase("Citadel")) {
			Samurai.getInstance().getServer().getPluginManager().callEvent(new CitadelActivatedEvent());
		}
	}
//
//	@EventHandler
//	public void onProjectileHit(ProjectileHitEvent event) {
//		Projectile projectile = event.getEntity();
//		if (projectile.getShooter() instanceof Player) {
//			if (projectile instanceof Arrow) {
//
//				Player player = (Player) event.getEntity().getShooter();
//
//				if (player == null) return;
//
//				Block hitBlock = event.getHitBlock();
//
//				if (hitBlock == null) return;
//
//				Team hitAt = LandBoard.getInstance().getTeam(hitBlock.getLocation());
//				if (hitAt != null && hitAt.hasDTRBitmask(DTRBitmask.CITADEL)) {
//					if (hitBlock.getLocation().distance(CitadelHandler.getApparatusStrength()) <= 0.3) {
//						if (strengthTask != null) {
//							player.sendMessage(CC.translate("&cThat apparatus is already occupied. You need to wait, until you can try to control it."));
//							return;
//						}
//						Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
//						if (team != null) {
//							if (team.getCitadelKills() >= 3) {
//								team.setCitadelKills(team.getCitadelKills() - 3);
//								team.sendMessage(CC.translate(CitadelHandler.PREFIX + " &aYour team has just got the &cStrength Apparatus&a! You now all have &cStrength I&a for &a1 minute&f!"));
//								team.updateMemberEffects(ApparatusStage.FIRST);
//
//								hitBlock.setType(Material.BEACON);
//
//								strengthTask = Bukkit.getScheduler().runTaskTimer(Foxtrot.getInstance(), () -> {
//									for (int i = 0; i < 15; i++) {
//										Location fixed = hitBlock.getLocation().clone().add(0, i, 0);
//										fixed.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, fixed, 1);
//									}
//								}, 10, 10);
//
//								new BukkitRunnable() {
//									@Override
//									public void run() {
//										strengthTask.cancel();
//										strengthTask = null;
//									}
//								}.runTaskLater(Foxtrot.getInstance(), 20 * 60);
//							}
//						}
//					} else if (hitBlock.getLocation().distance(CitadelHandler.getApparatusResistance()) <= 0.3) {
//						if (resTask != null) {
//							player.sendMessage(CC.translate("&cThat apparatus is already occupied. You need to wait, until you can try to control it."));
//							return;
//						}
//						Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
//						if (team != null) {
//							if (team.getCitadelKills() >= 5) {
//								team.setCitadelKills(team.getCitadelKills() - 5);
//								team.sendMessage(CC.translate(CitadelHandler.PREFIX + " &aYour team has just got the &bResistance Apparatus&a! You now all have &bResistance II&a for &a45 seconds&f!"));
//								team.updateMemberEffects(ApparatusStage.SECOND);
//
//								hitBlock.setType(Material.BEACON);
//
//								resTask = Bukkit.getScheduler().runTaskTimer(Foxtrot.getInstance(), () -> {
//									for (int i = 0; i < 15; i++) {
//										Location fixed = hitBlock.getLocation().clone().add(0, i, 0);
//										fixed.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, fixed, 1);
//									}
//								}, 10, 10);
//
//								new BukkitRunnable() {
//									@Override
//									public void run() {
//										resTask.cancel();
//										resTask = null;
//									}
//								}.runTaskLater(Foxtrot.getInstance(), 20 * 30);
//							}
//						}
//					}
//				}
//			}
//		}
//	}

//	@EventHandler
//	public void onDeath(PlayerDeathEvent event) {
//		if (Foxtrot.getInstance().getCitadelHandler().isActive()) {
//			Player player = event.getEntity();
//			Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
//			if (team != null) {
//				team.setCitadelDeaths(team.getCitadelDeaths() + 1);
//
//				if (team.getCitadelDeaths() >= 2) {
//					if (CitadelHandler.getDeathStage() == 0) {
//						CitadelHandler.setDeathStage(1);
//						team.updateMemberEffects(ApparatusStage.THIRD);
//
//						team.sendMessage(CC.translate("&cYour team has just went over 3 citadel deaths. You all now have been effected negatively."));
//						team.setCitadelDeaths(0);
//
//						CitadelHandler.getDeath1Apparatus().getBlock().setType(Material.BEACON);
//
//						death1Task = Bukkit.getScheduler().runTaskTimer(Foxtrot.getInstance(), () -> {
//							for (int i = 0; i < 15; i++) {
//								Location fixed = CitadelHandler.getDeath1Apparatus().getBlock().getLocation().clone().add(0, i, 0);
//								fixed.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, fixed, 1);
//							}
//						}, 10, 10);
//
//						new BukkitRunnable() {
//							@Override
//							public void run() {
//								death1Task.cancel();
//								death1Task = null;
//							}
//						}.runTaskLater(Foxtrot.getInstance(), 20 * 30);
//					}
//				}
//				if (team.getCitadelDeaths() >= 3) {
//					if (CitadelHandler.getDeathStage() == 1) {
//						CitadelHandler.setDeathStage(0);
//						team.updateMemberEffects(ApparatusStage.NONE);
//						team.sendMessage(CC.translate("&cYour team has just went over 5 citadel deaths. You all now have been effected negatively."));
//						team.setCitadelDeaths(0);
//
//						CitadelHandler.getDeath2Apparatus().getBlock().setType(Material.BEACON);
//
//						death2Task = Bukkit.getScheduler().runTaskTimer(Foxtrot.getInstance(), () -> {
//							for (int i = 0; i < 15; i++) {
//								Location fixed = CitadelHandler.getDeath2Apparatus().getBlock().getLocation().clone().add(0, i, 0);
//								fixed.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, fixed, 1);
//							}
//						}, 10, 10);
//
//						new BukkitRunnable() {
//							@Override
//							public void run() {
//								death2Task.cancel();
//								death2Task = null;
//							}
//						}.runTaskLater(Foxtrot.getInstance(), 20 * 30);
//					}
//				}
//			}
//			Player killer = player.getKiller();
//			if (killer != null) {
//				Team killerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(killer);
//				if (killerTeam != null) {
//					killerTeam.setCitadelKills(killerTeam.getCitadelKills() + 1);
//					killerTeam.sendMessage(CC.translate(CitadelHandler.PREFIX + " &eYou have just earned your team a Citadel Kill! &7(New Amount: " + killerTeam.getCitadelKills() + ")"));
//				}
//			}
//		}
//	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onKOTHCaptured(EventCapturedEvent event) {
		if (event.getEvent().getName().equalsIgnoreCase("Citadel")) {
			Team playerTeam = Samurai.getInstance().getTeamHandler().getTeam(event.getPlayer());

			if (playerTeam != null) {
				Samurai.getInstance().getCitadelHandler().addCapper(playerTeam.getUniqueId());
				playerTeam.setCitadelsCapped(playerTeam.getCitadelsCapped() + 1);
			}
		}
	}

	@EventHandler
	public void onCitadelActivated(CitadelActivatedEvent event) {
		Samurai.getInstance().getCitadelHandler().resetCappers();
	}

	@EventHandler
	public void onCitadelCaptured(CitadelCapturedEvent event) {
		Samurai.getInstance().getServer().broadcastMessage(CitadelHandler.PREFIX + " " + ChatColor.RED + "Citadel" + ChatColor.YELLOW + " is " + ChatColor.RED + "closed " + ChatColor.YELLOW + "until " + ChatColor.WHITE + (new SimpleDateFormat()).format(Samurai.getInstance().getCitadelHandler().getLootable()) + ChatColor.YELLOW + ".");
		CitadelHandler.setCappedAt(System.currentTimeMillis() + ((60_000L * 60) * 12));
		Samurai.getInstance().getCitadelHandler().getCappers().add(event.getCapper());
	}

	@EventHandler(priority = EventPriority.MONITOR) // The monitor is here so we get called 'after' most join events.
	public void onPlayerJoin(PlayerJoinEvent event) {
		Team playerTeam = Samurai.getInstance().getTeamHandler().getTeam(event.getPlayer());

		if (playerTeam != null && Samurai.getInstance().getCitadelHandler().getCappers().contains(playerTeam.getUniqueId())) {
			event.getPlayer().sendMessage(CitadelHandler.PREFIX + " " + ChatColor.DARK_GREEN + "Your team currently controls Citadel.");
		}
	}


	@EventHandler
	public void onHour(HourEvent event) {
		if (CitadelHandler.getCappedAt() + ((60_000L * 60) * 3) > System.currentTimeMillis()) {
			int respawned = Samurai.getInstance().getCitadelHandler().respawnCitadelChests();

			if (respawned != 0) {
				Samurai.getInstance().getServer().broadcastMessage(CitadelHandler.PREFIX + " " + ChatColor.GREEN + "Citadel loot boxes have respawned!");
			}
		}
	}

}