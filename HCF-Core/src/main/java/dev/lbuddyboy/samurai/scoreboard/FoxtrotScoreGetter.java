/*
package dev.lbuddyboy.samurai.scoreboard;

import dev.lbuddyboy.samurai.Foxtrot;
import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.custom.ability.profile.AbilityProfile;
import dev.lbuddyboy.samurai.custom.airdrop.listener.AirdropListener;
import dev.lbuddyboy.samurai.commands.staff.EOTWCommand;
import dev.lbuddyboy.samurai.events.Event;
import dev.lbuddyboy.samurai.events.EventType;
import dev.lbuddyboy.samurai.events.dtc.DTC;
import dev.lbuddyboy.samurai.events.koth.KOTH;
import dev.lbuddyboy.samurai.listener.GoldenAppleListener;
import dev.lbuddyboy.samurai.map.duel.Duel;
import dev.lbuddyboy.samurai.map.game.Game;
import dev.lbuddyboy.samurai.map.game.GameHandler;
import dev.lbuddyboy.samurai.map.stats.StatsEntry;
import dev.lbuddyboy.samurai.custom.power.listener.PowerListener;
import dev.lbuddyboy.samurai.pvpclasses.pvpclasses.ArcherClass;
import dev.lbuddyboy.samurai.pvpclasses.pvpclasses.BardClass;
import dev.lbuddyboy.samurai.pvpclasses.pvpclasses.HunterClass;
import dev.lbuddyboy.samurai.server.ServerHandler;
import dev.lbuddyboy.samurai.server.SpawnTagHandler;
import dev.lbuddyboy.samurai.server.pearl.EnderpearlCooldownHandler;
import dev.lbuddyboy.samurai.custom.supplydrops.SupplyCrate;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.claims.LandBoard;
import dev.lbuddyboy.samurai.team.commands.team.TeamCommands;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.ProgressBarUtil;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import dev.lbuddyboy.samurai.util.object.Logout;
import dev.lbuddyboy.samurai.util.object.ScoreFunction;
import dev.lbuddyboy.samurai.custom.vaults.VaultHandler;
import dev.lbuddyboy.samurai.util.qlib.autoreboot.AutoRebootHandler;
import dev.lbuddyboy.samurai.util.qlib.util.LinkedList;
import dev.lbuddyboy.samurai.util.qlib.util.TimeUtils;
import dev.lbuddyboy.samurai.util.qlib.util.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import dev.lbuddyboy.samurai.scoreboard.assemble.AssembleAdapter;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FoxtrotScoreGetter implements AssembleAdapter {

	@Override
	public String getTitle(Player player) {
		return CC.translate(Foxtrot.getInstance().getAnimationHandler().getTitle().replace("%online%", "" + Foxtrot.getInstance().getScheduleHandler().getRealPlayers()));
	}

	@Override
	public List<String> getLines(Player player) {
		LinkedList<String> scores = new LinkedList<>();
		if (!Foxtrot.getInstance().getScoreboardMap().isToggled(player.getUniqueId())) {
			return scores;
		}
		if (Foxtrot.getInstance().getMapHandler().getGameHandler() != null && Foxtrot.getInstance().getMapHandler().getGameHandler().isOngoingGame()) {
			Game ongoingGame = Foxtrot.getInstance().getMapHandler().getGameHandler().getOngoingGame();
			if (ongoingGame.isPlayingOrSpectating(player.getUniqueId())) {
				ongoingGame.getScoreboardLines(player, scores);
				scores.addFirst("&f&m⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯");
				scores.add("&f&m⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯");
				return CC.translate(scores);
			}
		}

		if (Foxtrot.getInstance().getMapHandler().getDuelHandler().isInDuel(player)) {
			Duel duel = Foxtrot.getInstance().getMapHandler().getDuelHandler().getDuel(player);

			scores.add(" &gOpponent: &f" + UUIDUtils.name(duel.getOpponent(player.getUniqueId())));

			scores.addFirst("&f&m⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯");
			scores.add("&f&m⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯");

			return CC.translate(scores);
		}

		String spawnTagScore = getSpawnTagScore(player);
		String enderpearlScore = getEnderpearlScore(player);
		String pvpTimerScore = getPvPTimerScore(player);
		String archerMarkScore = getArcherMarkScore(player);
		String bardEffectScore = getBardEffectScore(player);
		String bardEnergyScore = getBardEnergyScore(player);
		String fstuckScore = getFStuckScore(player);
		String logoutScore = getLogoutScore(player);
		String homeScore = getHomeScore(player);
		String appleScore = getAppleScore(player);

		if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
			StatsEntry stats = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(player.getUniqueId());

			String kills = " &g&lKills";
			String deaths = " &g&lDeaths";

			scores.add(" &g&l" + player.getName());
			scores.add("  &7" + SymbolUtil.UNICODE_ARROW_RIGHT + kills + "&7: &f" + stats.getKills());
			scores.add("  &7" + SymbolUtil.UNICODE_ARROW_RIGHT + deaths + "&7: &f" + stats.getDeaths());
		}

		if (Foxtrot.getInstance().getToggleClaimDisplayMap().isClaimDisplayEnabled(player.getUniqueId())) {
			String claim = "&g&lClaim";
			Team team = LandBoard.getInstance().getTeam(player.getLocation());
			if (team != null) {
				scores.add(" " + claim + "&7: &x&e&2&5&e&5&e" + team.getName(player));
			} else {
				if (Foxtrot.getInstance().getServerHandler().isWarzone(player.getLocation())) {
					scores.add(" " + claim + "&7: &x&e&2&5&e&5&eWarzone");
				} else {
					scores.add(" " + claim + "&7: &7Wilderness");
				}
			}
		}

		if (spawnTagScore != null) {
			scores.add(" &x&e&2&5&e&5&e&lSpawn Tag&7: &x&e&2&5&e&5&e" + spawnTagScore);
		}

		if (homeScore != null) {
			scores.add(" &x&5&2&a&3&e&2&lHome &x&5&2&a&3&e&2" + homeScore);
		}

		if (appleScore != null) {
			scores.add(" &x&e&2&b&b&2&1&lApple &x&e&2&b&b&2&1" + appleScore);
		}

		if (enderpearlScore != null) {
			scores.add(" &x&5&5&2&e&e&2&lEnderpearl&7: &x&e&2&5&e&5&e" + enderpearlScore);
		}

		if (pvpTimerScore != null) {
			if (Foxtrot.getInstance().getStartingPvPTimerMap().get(player.getUniqueId())) {
				scores.add(" &x&0&0&a&7&4&6&lStarting Timer&7: &x&e&2&5&e&5&e" + pvpTimerScore);
			} else {
				scores.add(" &x&0&0&a&7&4&6&lPvP Timer&7: &x&e&2&5&e&5&e" + pvpTimerScore);
			}
		}

		Iterator<Map.Entry<String, Long>> iterator = SOTWCommand.getCustomTimers().entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, Long> timer = iterator.next();
			if (timer.getValue() < System.currentTimeMillis()) {
				iterator.remove();
				continue;
			}

			if (timer.getKey().equals("&a&lSOTW")) {
				if (SOTWCommand.hasSOTWEnabled(player.getUniqueId())) {
					scores.add(" &x&0&4&f&f&3&f&l&mSOTW &x&0&4&f&f&3&f&mends in &l&m" + getTimerScore(timer));
				} else {
					scores.add(" &x&0&4&f&f&3&f&lSOTW &x&0&4&f&f&3&fends in &l" + getTimerScore(timer));
				}
			}

		}

*/
/*		CustomTimer timer = Foxtrot.getInstance().getAnimationHandler().getCurrentTimer();
		if (timer != null) {
			if (timer.getTime() < System.currentTimeMillis()) {
				if (!timer.isCmdSent()) {
					CustomTimer.customTimers.remove(timer);
					Bukkit.getScheduler().runTask(Foxtrot.getInstance(), () -> {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), timer.getCommand());
						timer.setCmdSent(true);
					});
					Foxtrot.getInstance().getAnimationHandler().setCurrentTimer(null);
				}
			} else {
				scores.add(CC.translate(" " + timer.getName() + "&7: &x&e&2&5&e&5&e" + getScore(timer.getTime())));
			}
		}*//*


//		if (Foxtrot.getInstance().getServer().getPluginManager().getPlugin("sExtras") != null && Foxtrot.getInstance().getServer().getPluginManager().getPlugin("sExtras").isEnabled()) {
//			for (GlobalTimer globalTimer : Core.getInstance().getNetworkHandler().getTimerHandler().getActiveGlobalTimers()) {
//				if (globalTimer.getId().equals("SOTW")) continue;
//				scores.add(" " + globalTimer.getDisplay() + "&7: &x&e&2&5&e&5&e" + TimeUtils.formatIntoHHMMSS((int) (globalTimer.getTimeLeft() / 1000)));
//			}
//		}

		if (EOTWCommand.realFFAStarted()) {
			long lockedIn = AbilityProfile.byUUID(player.getUniqueId()).getEotwLastDamaged();
			long difference = (lockedIn + 60_000L) - System.currentTimeMillis();
			if (difference > 0) {
				scores.add("&4&lFFA");
				scores.add(" &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &cLocked In&7: &c" + (difference / 1000) + "s");
				scores.add(" &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &7Kill the player");
				scores.add(" &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &7that is visible!");
			}
		}

		for (Event event : Foxtrot.getInstance().getEventHandler().getEvents()) {
			if (!event.isActive() || event.isHidden()) {
				continue;
			}

			String displayName;

			switch (event.getName()) {
				case "EOTW":
					displayName = CC.translate("&x&c&1&0&9&2&7&lEOTW");
					break;
				case "Nether-Citadel":
				case "Citadel":
				case "Overworld-Citadel":
					displayName = CC.translate("&x&c&4&1&8&f&b&lCitadel");
					break;
				default:
					displayName = CC.translate("&x&6&0&7&7&f&b&l" + event.getName());
					break;
			}

			if (event.getType() == EventType.DTC) {
				scores.add(" " + displayName + "&7: &x&e&2&5&e&5&e" + ((DTC) event).getCurrentPoints());
			} else {
				if (event.getName().equals(VaultHandler.TEAM_NAME)) {
					displayName = CC.translate("&x&7&a&9&9&c&1&lVault Post");
				}
				KOTH koth = (KOTH) event;
				scores.add(" " + displayName + "&7: &x&e&2&5&e&5&e" + ScoreFunction.TIME_SIMPLE.apply((float) (koth.getRemainingCapTime())));
				if (event.getName().equals(VaultHandler.TEAM_NAME)) {
					Team team = Foxtrot.getInstance().getVaultHandler().getSystemTeam();
					if (team != null) {
						if (player.getWorld().getEnvironment() == World.Environment.NORMAL) {
							if (team.getHQ() != null) {
								if (team.getHQ().distance(player.getLocation()) < 50) {
									if (Foxtrot.getInstance().getVaultHandler().isContested()) {
										scores.add("  &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &x&7&a&9&9&c&1&lStage&7: &x&e&2&5&e&5&eCONTESTED");
									} else {
										scores.add("  &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &x&7&a&9&9&c&1&lStage&7: &x&e&2&5&e&5&e" + Foxtrot.getInstance().getVaultHandler().getVaultStage().getStageName());
									}
									scores.add("  &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " " + ProgressBarUtil.getProgressBar(koth.getCapTime() - koth.getRemainingCapTime(), koth.getCapTime()));
								}
							}
						}
					}
				}
			}
		}

		if (EOTWCommand.isFfaEnabled()) {
			long ffaEnabledAt = EOTWCommand.getFfaActiveAt();
			if (System.currentTimeMillis() < ffaEnabledAt) {
				long difference = ffaEnabledAt - System.currentTimeMillis();
				scores.add(" &x&e&2&b&b&2&1&lFFA&7: &x&e&2&5&e&5&e" + ScoreFunction.TIME_SIMPLE.apply(difference / 1000F));
			}
		}

		if (AbilityItem.isOnGlobalPackageCooldown(player)) {
			String globalCooldownTime = AbilityItem.getGlobalCooldownTimeFormatted(player);
			scores.add(" &x&3&e&9&4&f&f&lAbility Item&7: &x&e&2&5&e&5&e" + globalCooldownTime + "s");
		}

		if (archerMarkScore != null) {
			scores.add(" &x&e&2&b&b&2&1&lArcher Mark&7: &x&e&2&5&e&5&e" + archerMarkScore);
		}

		if (getHunterMarkScore(player) != null) {
			scores.add(" &5&lHunter Mark&7: &x&e&2&5&e&5&e" + getHunterMarkScore(player));
		}

		if (bardEffectScore != null) {
			scores.add(" &x&0&0&f&f&7&a&lBard Effect&7: &x&e&2&5&e&5&e" + bardEffectScore);
		}

		if (bardEnergyScore != null) {
			scores.add(" &x&3&e&9&4&f&f&lBard Energy&7: &x&e&2&5&e&5&e" + bardEnergyScore);
		}

		if (fstuckScore != null) {
			scores.add(" &x&d&f&2&f&1&3&lStuck&7: &x&e&2&5&e&5&e" + fstuckScore);
		}

		if (logoutScore != null) {
			scores.add(" &x&d&f&2&f&1&3&lLogout&7: &x&e&2&5&e&5&e" + logoutScore);
		}

		if (AutoRebootHandler.isRebooting()) {
			scores.add(" &x&d&f&2&f&1&3&lRebooting: " + TimeUtils.formatIntoMMSS(AutoRebootHandler.getRebootSecondsRemaining()));
		}

		if (Foxtrot.getInstance().getPowerMap().isToggled(player.getUniqueId())) {
			if (PowerListener.powerCooldown.onCooldown(player)) {
				scores.add(" &x&f&b&5&3&0&0&lPower&7: &c" + PowerListener.powerCooldown.getRemaining(player));
			}
		}

		if (AirdropListener.countdown.onCooldown(player)) {
			scores.add(" &4&lKoTH TP&7: &c" + AirdropListener.countdown.getRemaining(player));
		}

		GameHandler gameHandler = Foxtrot.getInstance().getMapHandler().getGameHandler();
		if (gameHandler != null) {
			if (gameHandler.isJoinable())
				scores.add(" &x&e&2&b&b&2&1&lEvent&7: " + Foxtrot.getInstance().getMapHandler().getGameHandler().getOngoingGame().getGameType().getDisplayName() + " (/join)");
			else if (gameHandler.isHostCooldown())
				scores.add(" &x&e&2&b&b&2&1&lEvent Cooldown&7: " + TimeUtils.formatIntoMMSS(gameHandler.getCooldownSeconds()));
		}

		if (Foxtrot.getInstance().getSupplyDropHandler().getActiveSupplyCrate() != null) {
			if (Foxtrot.getInstance().getSupplyCrateSBMap().isToggled(player.getUniqueId())) {
				SupplyCrate crate = Foxtrot.getInstance().getSupplyDropHandler().getActiveSupplyCrate();
				scores.add(" &x&c&a&3&2&d&f&lSupply Crate");
				scores.add(" &7 " + SymbolUtil.UNICODE_ARROW_RIGHT + " Coords: &f" + crate.getEndLocation().getBlockX() + ", " + crate.getEndLocation().getBlockZ());
			}
		}
		if (Foxtrot.getInstance().getToggleFocusDisplayMap().isFocusDisplayEnabled(player.getUniqueId())) {
			Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
			Team focusedTeam = team == null ? null : team.getFocusedTeam();

			if (focusedTeam != null) {

				if (!scores.isEmpty()) {
					scores.add("  ");
				}

				Location hqLoc = focusedTeam.getHQ();
				String hq = hqLoc == null ? "None" : String.format("%d, %d", hqLoc.getBlockX(), hqLoc.getBlockZ());

				scores.add(" &g&lTeam&7: &x&e&2&5&e&5&e" + focusedTeam.getName());


				if (focusedTeam.getOwner() != null) {
					scores.add(" &f ● &g&lDTR&7: &x&e&2&5&e&5&e" + focusedTeam.getDTRColor() + Team.DTR_FORMAT.format(focusedTeam.getDTR()) + focusedTeam.getDTRSuffix());
				}

				scores.add(" &f ● &g&lOnline&7: &x&e&2&5&e&5&e" + focusedTeam.getOnlineMemberAmount() + "/" + focusedTeam.getMembers().size());
				scores.add(" &f ● &g&lHQ&7: &x&e&2&5&e&5&e" + hq);
			}
		}

//		ConquestGame conquest = Foxtrot.getInstance().getConquestHandler().getGame();
//
//		if (conquest != null) {
//			if (scores.size() != 0) {
//				scores.add("  ");
//			}
//
//			scores.add(" &x&9&8&d&f&2&b&lConquest:");
//			int displayed = 0;
//
//			for (Map.Entry<ObjectId, Integer> entry : conquest.getTeamPoints().entrySet()) {
//				Team resolved = Foxtrot.getInstance().getTeamHandler().getTeam(entry.getKey());
//
//				if (resolved != null) {
//					scores.add("  &f● " + resolved.getName(player) + "&7: &f" + entry.getValue());
//					displayed++;
//				}
//
//				if (displayed == 3) {
//					break;
//				}
//			}
//
//			if (displayed == 0) {
//				scores.add("   &7No scores yet");
//			}
//		}

		if (!scores.isEmpty()) {
			// 'Top' and bottom.
			scores.addFirst("&f&m⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯");
			scores.add("  ");
			scores.add(" " + Foxtrot.getInstance().getAnimationHandler().getFooter());
			scores.add("&f&m⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯");
		}

		return CC.translate(scores);
	}

	public String getAppleScore(Player player) {
		if (GoldenAppleListener.getCrappleCooldown().containsKey(player.getUniqueId()) && GoldenAppleListener.getCrappleCooldown().get(player.getUniqueId()) >= System.currentTimeMillis()) {
			float diff = GoldenAppleListener.getCrappleCooldown().get(player.getUniqueId()) - System.currentTimeMillis();

			if (diff >= 0) {
				return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
			}
		}

		return (null);
	}

	public String getHomeScore(Player player) {
		if (ServerHandler.getHomeTimer().containsKey(player.getName()) && ServerHandler.getHomeTimer().get(player.getName()) >= System.currentTimeMillis()) {
			float diff = ServerHandler.getHomeTimer().get(player.getName()) - System.currentTimeMillis();

			if (diff >= 0) {
				return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
			}
		}

		return (null);
	}

	public String getFStuckScore(Player player) {
		if (TeamCommands.getWarping().containsKey(player.getName())) {
			float diff = TeamCommands.getWarping().get(player.getName()) - System.currentTimeMillis();

			if (diff >= 0) {
				return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
			}
		}

		return null;
	}

	public String getLogoutScore(Player player) {
		Logout logout = ServerHandler.getTasks().get(player.getName());

		if (logout != null) {
			float diff = logout.getLogoutTime() - System.currentTimeMillis();

			if (diff >= 0) {
				return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
			}
		}

		return null;
	}

	public String getSpawnTagScore(Player player) {
		if (SpawnTagHandler.isTagged(player)) {
			float diff = SpawnTagHandler.getTag(player);

			if (diff >= 0) {
				return (ScoreFunction.TIME_SIMPLE.apply(diff / 1000F));
			}
		}

		return (null);
	}

	public String getEnderpearlScore(Player player) {
		if (EnderpearlCooldownHandler.getEnderpearlCooldown().containsKey(player.getName()) && EnderpearlCooldownHandler.getEnderpearlCooldown().get(player.getName()) >= System.currentTimeMillis()) {
			float diff = EnderpearlCooldownHandler.getEnderpearlCooldown().get(player.getName()) - System.currentTimeMillis();

			if (diff >= 0) {
				return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
			}
		}

		return (null);
	}

	public String getPvPTimerScore(Player player) {
		if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
			int secondsRemaining = Foxtrot.getInstance().getPvPTimerMap().getSecondsRemaining(player.getUniqueId());

			if (secondsRemaining >= 0) {
				return (ScoreFunction.TIME_SIMPLE.apply((float) secondsRemaining));
			}
		}

		return (null);
	}

	public static String getScore(Long timer) {
		long diff = timer - System.currentTimeMillis();

		if (diff > 0) {
			return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
		} else {
			return (null);
		}
	}

	public String getTimerScore(Map.Entry<String, Long> timer) {
		long diff = timer.getValue() - System.currentTimeMillis();

		if (diff > 0) {
			return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
		} else {
			return (null);
		}
	}

	public String getHunterMarkScore(Player player) {
		if (HunterClass.isMarked(player)) {
			long diff = HunterClass.getMarkedPlayers().get(player.getName()) - System.currentTimeMillis();

			if (diff > 0) {
				return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
			}
		}

		return (null);
	}

	public String getArcherMarkScore(Player player) {
		if (ArcherClass.isMarked(player)) {
			long diff = ArcherClass.getMarkedPlayers().get(player.getName()) - System.currentTimeMillis();

			if (diff > 0) {
				return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
			}
		}

		return (null);
	}

	public String getBardEffectScore(Player player) {
		if (BardClass.getLastEffectUsage().containsKey(player.getName()) && BardClass.getLastEffectUsage().get(player.getName()) >= System.currentTimeMillis()) {
			float diff = BardClass.getLastEffectUsage().get(player.getName()) - System.currentTimeMillis();

			if (diff > 0) {
				return (ScoreFunction.TIME_SIMPLE.apply(diff / 1000F));
			}
		}

		return (null);
	}

	public String getBardEnergyScore(Player player) {
		if (BardClass.getEnergy().containsKey(player.getName())) {
			float energy = BardClass.getEnergy().get(player.getName());

			if (energy > 0) {
				// No function here, as it's a "raw" value.
				return (String.valueOf(BardClass.getEnergy().get(player.getName())));
			}
		}

		return (null);
	}

}*/
