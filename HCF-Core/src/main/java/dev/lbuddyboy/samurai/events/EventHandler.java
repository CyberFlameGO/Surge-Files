package dev.lbuddyboy.samurai.events;

import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import dev.lbuddyboy.samurai.events.koth.listeners.KOTHListener;
import lombok.Getter;
import lombok.Setter;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.events.dtc.DTC;
import dev.lbuddyboy.samurai.events.dtc.DTCListener;
import dev.lbuddyboy.samurai.events.koth.KOTH;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.util.FileHelper;
import dev.lbuddyboy.samurai.util.modsuite.ModUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class EventHandler {

	@Getter private final Set<Event> events = new HashSet<>();
	@Getter private final Map<EventScheduledTime, String> EventSchedule = new TreeMap<>();

	@Getter
	@Setter
	private boolean scheduleEnabled;

	public EventHandler() {
		loadEvents();
		loadSchedules();

		Samurai.getInstance().getServer().getPluginManager().registerEvents(new KOTHListener(), Samurai.getInstance());
		Samurai.getInstance().getServer().getPluginManager().registerEvents(new DTCListener(), Samurai.getInstance());
		Samurai.getInstance().getServer().getPluginManager().registerEvents(new EventListener(), Samurai.getInstance());


		new BukkitRunnable() {
			@Override
			public void run() {
				for (Event event : events) {
					if (event.isActive()) {
						event.tick();
					}
				}
			}
		}.runTaskTimer(Samurai.getInstance(), 5L, 20L);

		Samurai.getInstance().getServer().getScheduler().runTaskTimer(Samurai.getInstance(), () -> {
			terminateKOTHs();
			activateKOTHs();
		}, 20 * 30L, 20 * 30L);
		// The initial delay of 5 ticks is to 'offset' us with the scoreboard handler.
	}

	public void loadEvents() {
		try {
			File eventsBase = new File(Samurai.getInstance().getDataFolder(), "events");

			if (!eventsBase.exists()) {
				eventsBase.mkdir();
			}

			for (EventType eventType : EventType.values()) {
				File subEventsBase = new File(eventsBase, eventType.name().toLowerCase());

				if (!subEventsBase.exists()) {
					subEventsBase.mkdir();
				}

				for (File eventFile : subEventsBase.listFiles()) {
					if (eventFile.getName().endsWith(".json")) {
						events.add(Samurai.GSON.fromJson(FileHelper.readFile(eventFile), eventType == EventType.KOTH ? KOTH.class : DTC.class));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// look for a previously active Event, if present deactivate and start it after 15 seconds
		events.stream().filter(Event::isActive).findFirst().ifPresent((event) -> {
			event.setActive(false);
			Bukkit.getScheduler().runTaskLater(Samurai.getInstance(), () -> {
				// if anyone had started a Event within the last 15 seconds,
				// don't activate previously active one
				if (events.stream().noneMatch(Event::isActive)) {
					event.activate();
				}
			}, 300L);
		});
	}

	public void fillSchedule() {
		List<String> allevents = new ArrayList<>();

		for (Event event : getEvents()) {
			if (event.isHidden() || event.getName().equalsIgnoreCase("EOTW") || event.getName().equalsIgnoreCase("Citadel")) {
				continue;
			}

			allevents.add(event.getName());
		}

		for (int minute = 0; minute < 60; minute++) {
			for (int hour = 0; hour < 24; hour++) {
				this.EventSchedule.put(new EventScheduledTime(Calendar.getInstance().get(Calendar.DAY_OF_YEAR), (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + hour) % 24, minute), allevents.get(0));
			}
		}
	}

	public void loadSchedules() {
		EventSchedule.clear();

		try {
			File eventSchedule = new File(Samurai.getInstance().getDataFolder(), "eventSchedule.json");

			if (!eventSchedule.exists()) {
				eventSchedule.createNewFile();
				BasicDBObject schedule = new BasicDBObject();
				int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
				List<String> allevents = new ArrayList<>();

				for (Event event : getEvents()) {
					if (event.isHidden() || event.getName().equalsIgnoreCase("EOTW") || event.getName().equalsIgnoreCase("Citadel")) {
						continue;
					}

					allevents.add(event.getName());
				}

				for (int dayOffset = 0; dayOffset < 21; dayOffset++) {
					int day = (currentDay + dayOffset) % 365;
					EventScheduledTime[] times = new EventScheduledTime[]{

							new EventScheduledTime(day, 0, 1), // 00:30am EST
							new EventScheduledTime(day, 2, 0), // 03:30am EST
							new EventScheduledTime(day, 4, 0), // 03:30am EST
							new EventScheduledTime(day, 6, 0), // 03:30am EST
							new EventScheduledTime(day, 8, 30), // 06:30am EST
							new EventScheduledTime(day, 10, 30), // 09:30am EST
							new EventScheduledTime(day, 12, 30), // 12:30pm EST
							new EventScheduledTime(day, 14, 30), // 15:30pm EST
							new EventScheduledTime(day, 15, 30), // 18:30pm EST
							new EventScheduledTime(day, 16, 30), // 21:30pm EST
							new EventScheduledTime(day, 17, 30), // 21:30pm EST
							new EventScheduledTime(day, 18, 30), // 21:30pm EST
							new EventScheduledTime(day, 19, 30), // 21:30pm EST
							new EventScheduledTime(day, 20, 30), // 21:30pm EST
							new EventScheduledTime(day, 21, 30), // 21:30pm EST
							new EventScheduledTime(day, 23, 30) // 21:30pm EST

					};

					if (Samurai.getInstance().getMapHandler().isKitMap()) {
						times = new EventScheduledTime[]{

								new EventScheduledTime(day, 0, 0), // 00:15am EST
								new EventScheduledTime(day, 0, 15), // 00:15am EST
								new EventScheduledTime(day, 0, 30), // 00:30am EST
								new EventScheduledTime(day, 0, 45), // 00:30am EST

								new EventScheduledTime(day, 1, 0), // 00:30am EST
								new EventScheduledTime(day, 1, 15), // 00:30am EST
								new EventScheduledTime(day, 1, 30), // 00:30am EST
								new EventScheduledTime(day, 1, 45), // 00:30am EST

								new EventScheduledTime(day, 2, 0), // 00:30am EST
								new EventScheduledTime(day, 2, 15), // 00:30am EST
								new EventScheduledTime(day, 2, 30), // 00:30am EST
								new EventScheduledTime(day, 2, 45), // 00:30am EST

								new EventScheduledTime(day, 3, 0), // 00:30am EST
								new EventScheduledTime(day, 3, 15), // 00:30am EST
								new EventScheduledTime(day, 3, 30), // 00:30am EST
								new EventScheduledTime(day, 3, 45), // 00:30am EST

								new EventScheduledTime(day, 4, 0), // 00:30am EST
								new EventScheduledTime(day, 4, 15), // 00:30am EST
								new EventScheduledTime(day, 4, 30), // 00:30am EST
								new EventScheduledTime(day, 4, 45), // 00:30am EST

								new EventScheduledTime(day, 5, 0), // 00:30am EST
								new EventScheduledTime(day, 5, 15), // 00:30am EST
								new EventScheduledTime(day, 5, 30), // 00:30am EST
								new EventScheduledTime(day, 5, 45), // 00:30am EST

								new EventScheduledTime(day, 6, 0), // 00:30am EST
								new EventScheduledTime(day, 6, 15), // 00:30am EST
								new EventScheduledTime(day, 6, 30), // 00:30am EST
								new EventScheduledTime(day, 6, 45), // 00:30am EST

								new EventScheduledTime(day, 7, 0), // 00:30am EST
								new EventScheduledTime(day, 7, 15), // 00:30am EST
								new EventScheduledTime(day, 7, 30), // 00:30am EST
								new EventScheduledTime(day, 7, 45), // 00:30am EST

								new EventScheduledTime(day, 8, 0), // 00:30am EST
								new EventScheduledTime(day, 8, 15), // 00:30am EST
								new EventScheduledTime(day, 8, 30), // 00:30am EST
								new EventScheduledTime(day, 8, 45), // 00:30am EST

								new EventScheduledTime(day, 9, 0), // 00:30am EST
								new EventScheduledTime(day, 9, 15), // 00:30am EST
								new EventScheduledTime(day, 9, 30), // 00:30am EST
								new EventScheduledTime(day, 9, 45), // 00:30am EST

								new EventScheduledTime(day, 10, 0), // 00:30am EST
								new EventScheduledTime(day, 10, 15), // 00:30am EST
								new EventScheduledTime(day, 10, 30), // 00:30am EST
								new EventScheduledTime(day, 10, 45), // 00:30am EST

								new EventScheduledTime(day, 11, 0), // 00:30am EST
								new EventScheduledTime(day, 11, 15), // 00:30am EST
								new EventScheduledTime(day, 11, 30), // 00:30am EST
								new EventScheduledTime(day, 11, 45), // 00:30am EST

								new EventScheduledTime(day, 12, 0), // 00:30am EST
								new EventScheduledTime(day, 12, 15), // 00:30am EST
								new EventScheduledTime(day, 12, 30), // 00:30am EST
								new EventScheduledTime(day, 12, 45), // 00:30am EST

								new EventScheduledTime(day, 13, 0), // 00:30am EST
								new EventScheduledTime(day, 13, 15), // 00:30am EST
								new EventScheduledTime(day, 13, 30), // 00:30am EST
								new EventScheduledTime(day, 13, 45), // 00:30am EST

								new EventScheduledTime(day, 14, 0), // 00:30am EST
								new EventScheduledTime(day, 14, 15), // 00:30am EST
								new EventScheduledTime(day, 14, 30), // 00:30am EST
								new EventScheduledTime(day, 14, 45), // 00:30am EST

								new EventScheduledTime(day, 15, 0), // 00:30am EST
								new EventScheduledTime(day, 15, 15), // 00:30am EST
								new EventScheduledTime(day, 15, 30), // 00:30am EST
								new EventScheduledTime(day, 15, 45), // 00:30am EST

								new EventScheduledTime(day, 16, 0), // 00:30am EST
								new EventScheduledTime(day, 16, 15), // 00:30am EST
								new EventScheduledTime(day, 16, 30), // 00:30am EST
								new EventScheduledTime(day, 16, 45), // 00:30am EST

								new EventScheduledTime(day, 17, 0), // 00:30am EST
								new EventScheduledTime(day, 17, 15), // 00:30am EST
								new EventScheduledTime(day, 17, 30), // 00:30am EST
								new EventScheduledTime(day, 17, 45), // 00:30am EST

								new EventScheduledTime(day, 18, 0), // 00:30am EST
								new EventScheduledTime(day, 18, 15), // 00:30am EST
								new EventScheduledTime(day, 18, 30), // 00:30am EST
								new EventScheduledTime(day, 18, 45), // 00:30am EST

								new EventScheduledTime(day, 19, 0), // 00:30am EST
								new EventScheduledTime(day, 19, 15), // 00:30am EST
								new EventScheduledTime(day, 19, 30), // 00:30am EST
								new EventScheduledTime(day, 19, 45), // 00:30am EST

								new EventScheduledTime(day, 20, 0), // 00:30am EST
								new EventScheduledTime(day, 20, 15), // 00:30am EST
								new EventScheduledTime(day, 20, 30), // 00:30am EST
								new EventScheduledTime(day, 20, 45), // 00:30am EST

								new EventScheduledTime(day, 21, 0), // 00:30am EST
								new EventScheduledTime(day, 21, 15), // 00:30am EST
								new EventScheduledTime(day, 21, 30), // 00:30am EST
								new EventScheduledTime(day, 21, 45), // 00:30am EST

								new EventScheduledTime(day, 22, 0), // 00:30am EST
								new EventScheduledTime(day, 22, 15), // 00:30am EST
								new EventScheduledTime(day, 22, 30), // 00:30am EST
								new EventScheduledTime(day, 22, 45), // 00:30am EST

								new EventScheduledTime(day, 23, 0), // 00:30am EST
								new EventScheduledTime(day, 23, 15), // 00:30am EST
								new EventScheduledTime(day, 23, 30), // 00:30am EST
								new EventScheduledTime(day, 23, 45), // 00:30am EST

						};
					}

					Collections.shuffle(allevents);

					if (!allevents.isEmpty()) {
						for (int eventTimeIndex = 0; eventTimeIndex < times.length; eventTimeIndex++) {
							EventScheduledTime eventTime = times[eventTimeIndex];
							String eventName = allevents.get(eventTimeIndex % allevents.size());

							schedule.put(eventTime.toString(), eventName);
						}
					}
				}

				FileHelper.writeFile(eventSchedule, Samurai.GSON.toJson(new JsonParser().parse(schedule.toString())));
			}

			BasicDBObject dbo = (BasicDBObject) JSON.parse(FileHelper.readFile(eventSchedule));

			if (dbo != null) {
				for (Map.Entry<String, Object> entry : dbo.entrySet()) {
					EventScheduledTime scheduledTime = EventScheduledTime.parse(entry.getKey());
					this.EventSchedule.put(scheduledTime, String.valueOf(entry.getValue()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveEvents() {
		try {
			File eventsBase = new File(Samurai.getInstance().getDataFolder(), "events");

			if (!eventsBase.exists()) {
				eventsBase.mkdir();
			}

			for (EventType eventType : EventType.values()) {

				File subEventsBase = new File(eventsBase, eventType.name().toLowerCase());

				if (!subEventsBase.exists()) {
					subEventsBase.mkdir();
				}

				for (File eventFile : subEventsBase.listFiles()) {
					eventFile.delete();
				}
			}

			for (Event event : events) {
				File eventFile = new File(new File(eventsBase, event.getType().name().toLowerCase()), event.getName() + ".json");
				FileHelper.writeFile(eventFile, Samurai.GSON.toJson(event));
				Bukkit.getLogger().info("Writing " + event.getName() + " to " + eventFile.getAbsolutePath());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Event getEvent(String name) {
		for (Event event : events) {
			if (event.getName().equalsIgnoreCase(name)) {
				return (event);
			}
		}

		return (null);
	}

	private void activateKOTHs() {
		// Don't start a KOTH during EOTW.
		if (Samurai.getInstance().getServerHandler().isPreEOTW()) {
			return;
		}

		// Don't start a KOTH if another one is active.
		for (Event koth : Samurai.getInstance().getEventHandler().getEvents()) {
			if (koth.isActive()) {
				return;
			}
		}

		EventScheduledTime scheduledTime = EventScheduledTime.parse(new Date());

		if (Samurai.getInstance().getEventHandler().getEventSchedule().containsKey(scheduledTime)) {
			String resolvedName = Samurai.getInstance().getEventHandler().getEventSchedule().get(scheduledTime);
			Event resolved = Samurai.getInstance().getEventHandler().getEvent(resolvedName);

			if (scheduledTime.getHour() == 15 && scheduledTime.getMinutes() == 30 && resolvedName.equals("Conquest")) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "conquestadmin start");
				return;
			}

			if (resolved == null) {
				Samurai.getInstance().getLogger().warning("The event scheduler has a schedule for an event named " + resolvedName + ", but the event does not exist.");
				return;
			}

			List<Team> onlineTeams = new ArrayList<>();

			// Sort of weird way of getting player counts, but it does it in the least iterations (1), which is what matters!
			for (Player player : Samurai.getInstance().getServer().getOnlinePlayers()) {
				if (ModUtils.isInvisible(player)) {
					continue;
				}

				Team playerTeam = Samurai.getInstance().getTeamHandler().getTeam(player);

				if (playerTeam != null) {
					onlineTeams.add(playerTeam);
				}
			}

			if (onlineTeams.size() < 4) {
				Bukkit.broadcastMessage(CC.translate("&cCould not start a scheduled event due to there not being at least 4 teams online."));
				return;
			}

			resolved.activate();
		}
	}

	private void terminateKOTHs() {
		EventScheduledTime nextScheduledTime = EventScheduledTime.parse(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30)));

		if (Samurai.getInstance().getEventHandler().getEventSchedule().containsKey(nextScheduledTime)) {
			// We have a KOTH about to start. Prepare for it.
			for (Event activeEvent : Samurai.getInstance().getEventHandler().getEvents()) {
				if (activeEvent.getType() != EventType.KOTH) {
					continue;
				}

				KOTH activeKoth = (KOTH) activeEvent;
				if (!activeKoth.isHidden() && activeKoth.isActive() && !activeKoth.getName().equalsIgnoreCase("Citadel") && !activeKoth.getName().equals("EOTW")) {
					if (activeKoth.getCurrentCapper() != null) {
						if (!activeKoth.isTerminate()) {
							activeKoth.setTerminate(true);
							Samurai.getInstance().getServer().broadcastMessage(ChatColor.GOLD + "[King of the Hill] " + ChatColor.BLUE + activeKoth.getName() + ChatColor.YELLOW + " will be terminated if knocked.");
						}
						continue;
					}

					if (activeKoth.isTerminate()) {
						activeKoth.deactivate();
						Samurai.getInstance().getServer().broadcastMessage(ChatColor.GOLD + "[King of the Hill] " + ChatColor.BLUE + activeKoth.getName() + ChatColor.YELLOW + " has been terminated.");
					}

				}
			}
		}
	}

}
