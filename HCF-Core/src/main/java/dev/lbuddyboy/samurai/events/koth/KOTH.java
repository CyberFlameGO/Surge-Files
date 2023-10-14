package dev.lbuddyboy.samurai.events.koth;

import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.custom.vaults.VaultHandler;
import dev.lbuddyboy.samurai.events.Event;
import dev.lbuddyboy.samurai.events.EventType;
import dev.lbuddyboy.samurai.events.events.EventActivatedEvent;
import dev.lbuddyboy.samurai.events.events.EventCapturedEvent;
import dev.lbuddyboy.samurai.events.events.EventDeactivatedEvent;
import dev.lbuddyboy.samurai.events.koth.events.EventControlTickEvent;
import dev.lbuddyboy.samurai.events.koth.events.KOTHControlLostEvent;
import lombok.Getter;
import lombok.Setter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.modsuite.ModUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KOTH implements Event {

	@Getter
	private String name;
	@Getter
	private BlockVector capLocation;
	@Getter
	private String world;
	@Getter
	private int capDistance;
	@Getter
	private int capTime;
	@Getter
	private boolean hidden = false;
	@Getter
	@Setter
	boolean active;

	@Getter
	private transient String currentCapper;
	@Getter
	private transient int remainingCapTime;
	@Getter
	@Setter
	private transient boolean terminate;

	@Getter
	public boolean koth = true;

	@Getter
	private EventType type = EventType.KOTH;

	public KOTH(String name, Location location) {
		this.name = name;
		this.capLocation = location.toVector().toBlockVector();
		this.world = location.getWorld().getName();
		this.capDistance = 3;
		this.capTime = 60 * 15;
		this.terminate = false;

		if (Samurai.getInstance().getEventHandler().getEvent(this.name) == null) {
			Samurai.getInstance().getEventHandler().getEvents().add(this);
		}
		Samurai.getInstance().getEventHandler().saveEvents();
	}

	public void setLocation(Location location) {
		this.capLocation = location.toVector().toBlockVector();
		this.world = location.getWorld().getName();
		Samurai.getInstance().getEventHandler().saveEvents();
	}

	public void setCapDistance(int capDistance) {
		this.capDistance = capDistance;
		Samurai.getInstance().getEventHandler().saveEvents();
	}

	public void setCapTime(int capTime) {
		int oldCapTime = this.capTime;
		this.capTime = capTime;

		if (this.remainingCapTime > capTime) {
			this.remainingCapTime = capTime;
		} else if (remainingCapTime == oldCapTime) { // this will catch the time going up
			this.remainingCapTime = capTime;
		}

		Samurai.getInstance().getEventHandler().saveEvents();
	}

	@Override
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
		Samurai.getInstance().getEventHandler().saveEvents();
	}

	@Override
	public boolean activate() {
		if (active) {
			return (false);
		}
		if (SOTWCommand.isSOTWTimer()) return false;

		Samurai.getInstance().getServer().getPluginManager().callEvent(new EventActivatedEvent(this));

		this.active = true;
		this.currentCapper = null;
		this.remainingCapTime = this.capTime;
		this.terminate = false;

		return (true);
	}

	@Override
	public boolean deactivate() {
		if (!active) {
			return (false);
		}

		Samurai.getInstance().getServer().getPluginManager().callEvent(new EventDeactivatedEvent(this));

		this.active = false;
		this.currentCapper = null;
		this.remainingCapTime = this.capTime;
		this.terminate = false;

		return (true);
	}

	public void startCapping(Player player) {
		if (currentCapper != null) {
			resetCapTime();
		}

		this.currentCapper = player.getName();
		this.remainingCapTime = capTime;
	}

	public boolean finishCapping() {
		Player capper = Samurai.getInstance().getServer().getPlayerExact(currentCapper);

		if (capper == null) {
			resetCapTime();
			return (false);
		}

		EventCapturedEvent event = new EventCapturedEvent(this, capper);
		Samurai.getInstance().getServer().getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			resetCapTime();
			return (false);
		}

		deactivate();
		return (true);
	}

	public void resetCapTime() {
		Samurai.getInstance().getServer().getPluginManager().callEvent(new KOTHControlLostEvent(this));

		this.currentCapper = null;
		this.remainingCapTime = capTime;

		if (terminate) {
			deactivate();
			Samurai.getInstance().getServer().broadcastMessage(ChatColor.GOLD + "[King of the Hill] " + ChatColor.BLUE + getName() + ChatColor.YELLOW + " has been terminated.");
		}
	}

	@Override
	public void tick() {
		if (currentCapper != null) {
			Player capper = Samurai.getInstance().getServer().getPlayerExact(currentCapper);

			if (capper == null || !onCap(capper.getLocation()) || capper.isDead() || capper.getGameMode() != GameMode.SURVIVAL || ModUtils.isInvisible(capper)) {
				resetCapTime();
			} else {
				if (remainingCapTime % 60 == 0 && remainingCapTime > 1 && !isHidden()) {
					Team team = Samurai.getInstance().getTeamHandler().getTeam(capper);

					if (team != null) {
						for (Player player : Samurai.getInstance().getServer().getOnlinePlayers()) {
							if (team.isMember(player.getUniqueId()) && capper != player) {
								player.sendMessage(ChatColor.GOLD + "[King of the Hill]" + ChatColor.YELLOW + " your team is controlling " + ChatColor.BLUE + getName() + ChatColor.YELLOW + ".");
							}
						}
					}
				}

				if (remainingCapTime % 10 == 0 && remainingCapTime > 1 && !isHidden()) {
					if (getName().equals(VaultHandler.TEAM_NAME)) {
						capper.sendMessage(CC.translate(VaultHandler.PREFIX) + "" + ChatColor.YELLOW + " Attempting to control " + ChatColor.BLUE + getName() + ChatColor.YELLOW + ".");
					} else {
						capper.sendMessage(ChatColor.GOLD + "[King of the Hill]" + ChatColor.YELLOW + " Attempting to control " + ChatColor.BLUE + getName() + ChatColor.YELLOW + ".");
					}
				}

				if (remainingCapTime <= 0) {
					finishCapping();
				} else {
					EventControlTickEvent event = new EventControlTickEvent(this);
					Samurai.getInstance().getServer().getPluginManager().callEvent(event);
					if (event.isCancelled()) {
						return;
					}
				}

				this.remainingCapTime--;
			}
		} else {
			List<Player> onCap = new ArrayList<>();

			for (Player player : Samurai.getInstance().getServer().getOnlinePlayers()) {
				if (onCap(player.getLocation())) {
					if (Samurai.getInstance().getBattlePassHandler() != null) {
						Samurai.getInstance().getBattlePassHandler().useProgress(player.getUniqueId(), progress -> {
							progress.setAttemptCaptureKoth(true);
							progress.requiresSave();

							Samurai.getInstance().getBattlePassHandler().checkCompletionsAsync(player);
						});
					}
				}
				if (onCap(player.getLocation()) && !player.isDead() && !Samurai.getInstance().getServerHandler().getSpectateManager().isSpectator(player.getUniqueId()) && player.getGameMode() == GameMode.SURVIVAL && !ModUtils.isInvisible(player) && !Samurai.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
					onCap.add(player);
				}
			}

			Collections.shuffle(onCap);

			if (onCap.size() != 0) {
				startCapping(onCap.get(0));
			}
		}
	}

	public boolean onCap(Location location) {
		if (!location.getWorld().getName().equalsIgnoreCase(world)) {
			return (false);
		}

		int xDistance = Math.abs(location.getBlockX() - capLocation.getBlockX());
		int yDistance = Math.abs(location.getBlockY() - capLocation.getBlockY());
		int zDistance = Math.abs(location.getBlockZ() - capLocation.getBlockZ());

		return xDistance <= capDistance && yDistance <= 5 && !(yDistance <= -1) && zDistance <= capDistance;
	}

}