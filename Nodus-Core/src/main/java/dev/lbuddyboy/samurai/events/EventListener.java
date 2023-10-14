package dev.lbuddyboy.samurai.events;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import dev.lbuddyboy.samurai.custom.vaults.VaultHandler;
import dev.lbuddyboy.samurai.events.events.EventActivatedEvent;
import dev.lbuddyboy.samurai.events.events.EventCapturedEvent;
import dev.lbuddyboy.samurai.events.events.EventDeactivatedEvent;
import dev.lbuddyboy.samurai.api.HourEvent;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.events.koth.KOTH;
import dev.lbuddyboy.samurai.events.koth.events.KOTHControlLostEvent;
import dev.lbuddyboy.samurai.lunar.LunarListener;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.InventoryUtils;
import dev.lbuddyboy.samurai.util.LocationSerializer;
import dev.lbuddyboy.samurai.util.discord.DiscordLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventListener implements Listener {

	public EventListener() {
		Bukkit.getLogger().info("Creating indexes...");
		DBCollection mongoCollection = Samurai.getInstance().getMongoPool().getDB(Samurai.MONGO_DB_NAME).getCollection("KOTHCaptures");
		mongoCollection.createIndex(new BasicDBObject("Capper", 1));
		mongoCollection.createIndex(new BasicDBObject("CapperTeam", 1));
		mongoCollection.createIndex(new BasicDBObject("EventName", 1));
		Bukkit.getLogger().info("Creating indexes done.");
	}

	@EventHandler
	public void onHour(HourEvent event) {
		if (event.getHour() % 2 == 0) {

		}
	}

	@EventHandler
	public void onKOTHActivated(EventActivatedEvent event) {

		for (Player player : Bukkit.getOnlinePlayers()) {
			LunarListener.updateWaypoints(player);
		}

		if (event.getEvent().isHidden()) {
			return;
		}

		try {
			DiscordLogger.logEventStart(event.getEvent());
		} catch (IOException e) {
			e.printStackTrace();
		}

		String[] messages;

		switch (event.getEvent().getName()) {
			case "EOTW":
				messages = new String[]{
						ChatColor.RED + "███████",
						ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█" + " " + ChatColor.DARK_RED + "[EOTW]",
						ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED.toString() + ChatColor.BOLD + "The cap point at spawn",
						ChatColor.RED + "█" + ChatColor.DARK_RED + "████" + ChatColor.RED + "██" + " " + ChatColor.RED.toString() + ChatColor.BOLD + "is now active.",
						ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.DARK_RED + "EOTW " + ChatColor.GOLD + "can be contested now.",
						ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█",
						ChatColor.RED + "███████"
				};

				for (Player player : Samurai.getInstance().getServer().getOnlinePlayers()) {
					player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1F, 1F);
				}

				break;
			case "Vault":
				messages = new String[]{
						ChatColor.GRAY + "███████",
						ChatColor.GRAY + "██" + ChatColor.GOLD + "█" + ChatColor.GRAY + "█" + ChatColor.GOLD + "█" + ChatColor.GRAY + "██",
						ChatColor.GRAY + "██" + ChatColor.GOLD + "█" + ChatColor.GRAY + "█" + ChatColor.GOLD + "█" + ChatColor.GRAY + "██" + ChatColor.GOLD + " [Vault]",
						ChatColor.GRAY + "██" + ChatColor.GOLD + "█" + ChatColor.GRAY + "█" + ChatColor.GOLD + "█" + ChatColor.GRAY + "██ " + ChatColor.YELLOW + event.getEvent().getName(),
						ChatColor.GRAY + "██" + ChatColor.GOLD + "█" + ChatColor.GRAY + "█" + ChatColor.GOLD + "█" + ChatColor.GRAY + "██" + ChatColor.GOLD + " can be contested now.",
						ChatColor.GRAY + "██" + ChatColor.GOLD + "█" + ChatColor.GRAY + "█" + ChatColor.GOLD + "█" + ChatColor.GRAY + "██",
						ChatColor.GRAY + "███" + ChatColor.GOLD + "█" + ChatColor.GRAY + "█" + ChatColor.GRAY + "██",
						ChatColor.GRAY + "███████"
				};

				break;

			case "Citadel":
			case "Nether-Citadel":
			case "Overworld-Citadel":
				messages = new String[]{
						ChatColor.GRAY + "███████",
						ChatColor.GRAY + "██" + ChatColor.DARK_PURPLE + "████" + ChatColor.GRAY + "█",
						ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + ChatColor.GOLD + "[Citadel]",
						ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + ChatColor.DARK_PURPLE + event.getEvent().getName(),
						ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + ChatColor.GOLD + "can be contested now.",
						ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████",
						ChatColor.GRAY + "██" + ChatColor.DARK_PURPLE + "████" + ChatColor.GRAY + "█",
						ChatColor.GRAY + "███████"
				};

				break;

			default:
				messages = new String[]{
						ChatColor.GRAY + "███████",
						ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "███" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "█",
						ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "██" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "██" + " " + ChatColor.GOLD + "[King of the Hill]",
						ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "███" + ChatColor.GRAY + "███" + " " + ChatColor.YELLOW + event.getEvent().getName() + " KOTH",
						ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "██" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "██" + " " + ChatColor.GOLD + "can be contested now.",
						ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "███" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "█",
						ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "███" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "█",
						ChatColor.GRAY + "███████"
				};

				break;
		}

		if (event.getEvent().getType() == EventType.DTC) {
			messages = new String[]{
					ChatColor.RED + "███████",
					ChatColor.RED + "█" + ChatColor.GOLD + "█████" + ChatColor.RED + "█" + " " + ChatColor.GOLD + "[Event]",
					ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████" + " " + ChatColor.YELLOW + "DTC",
					ChatColor.RED + "█" + ChatColor.GOLD + "████" + ChatColor.RED + "██" + " " + ChatColor.GOLD + "can be contested now.",
					ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████",
					ChatColor.RED + "█" + ChatColor.GOLD + "█████" + ChatColor.RED + "█",
					ChatColor.RED + "███████"
			};
		}

		final String[] messagesFinal = messages;

		new BukkitRunnable() {

			@Override
			public void run() {
				for (Player player : Samurai.getInstance().getServer().getOnlinePlayers()) {
					player.sendMessage(messagesFinal);
				}
			}

		}.runTaskAsynchronously(Samurai.getInstance());

		// Can't forget console now can we
		for (String message : messages) {
			Samurai.getInstance().getLogger().info(message);
		}
	}

	@EventHandler
	public void onKOTHCaptured(final EventCapturedEvent event) {

		for (Player player : Bukkit.getOnlinePlayers()) {
			LunarListener.updateWaypoints(player);
		}

		if (event.getEvent().isHidden()) {
			return;
		}

		final Team team = Samurai.getInstance().getTeamHandler().getTeam(event.getPlayer());
		String teamName = ChatColor.GOLD + "[" + ChatColor.YELLOW + "-" + ChatColor.GOLD + "]";

		if (team != null) {
			teamName = ChatColor.GOLD + "[" + ChatColor.YELLOW + team.getName() + ChatColor.GOLD + "]";
		}

		String[] messages;

		if (event.getEvent().getName().equalsIgnoreCase("Citadel")) {
			messages = new String[]{
					ChatColor.GRAY + "███████",
					ChatColor.GRAY + "██" + ChatColor.DARK_PURPLE + "████" + ChatColor.GRAY + "█",
					ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + ChatColor.GOLD + "[Citadel]",
					ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + ChatColor.YELLOW + "controlled by",
					ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + teamName + ChatColor.WHITE + event.getPlayer().getDisplayName(),
					ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████",
					ChatColor.GRAY + "██" + ChatColor.DARK_PURPLE + "████" + ChatColor.GRAY + "█",
					ChatColor.GRAY + "███████"
			};
		} else if (event.getEvent().getName().equalsIgnoreCase("EOTW")) {
			messages = new String[]{
					ChatColor.RED + "███████",
					ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█" + " " + ChatColor.DARK_RED + "[EOTW]",
					ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED.toString() + ChatColor.BOLD + "EOTW has been",
					ChatColor.RED + "█" + ChatColor.DARK_RED + "████" + ChatColor.RED + "██" + " " + ChatColor.RED.toString() + ChatColor.BOLD + "controlled by",
					ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + teamName + ChatColor.WHITE + event.getPlayer().getDisplayName(),
					ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█",
					ChatColor.RED + "███████",
			};

		} else if (event.getEvent().getType() == EventType.DTC) {
			messages = new String[]{
					ChatColor.RED + "███████",
					ChatColor.RED + "█" + ChatColor.GOLD + "█████" + ChatColor.RED + "█" + " " + ChatColor.GOLD + "[Event]",
					ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████" + " " + ChatColor.YELLOW.toString() + ChatColor.BOLD + "DTC has been",
					ChatColor.RED + "█" + ChatColor.GOLD + "████" + ChatColor.RED + "██" + " " + ChatColor.YELLOW.toString() + ChatColor.BOLD + "controlled by",
					ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████" + " " + teamName + ChatColor.WHITE + event.getPlayer().getDisplayName(),
					ChatColor.RED + "█" + ChatColor.GOLD + "█████" + ChatColor.RED + "█",
					ChatColor.RED + "███████",
			};

			ItemStack rewardKey = InventoryUtils.generateKOTHRewardKey(event.getEvent().getName() + " DTC");
			ItemStack kothSign = Samurai.getInstance().getServerHandler().generateKOTHSign(event.getEvent().getName(), team == null ? event.getPlayer().getName() : team.getName(), EventType.DTC);

			event.getPlayer().getInventory().addItem(rewardKey);
			event.getPlayer().getInventory().addItem(kothSign);

			if (!event.getPlayer().getInventory().contains(rewardKey)) {
				event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), rewardKey);
			}

			if (!event.getPlayer().getInventory().contains(kothSign)) {
				event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), kothSign);
			}
		} else {
			messages = new String[]{
					ChatColor.GOLD + "[King of the Hill] " + ChatColor.BLUE + event.getEvent().getName() + ChatColor.YELLOW + " has been controlled by " + teamName + ChatColor.WHITE + event.getPlayer().getDisplayName() + ChatColor.YELLOW + "!",
					ChatColor.GOLD + "[King of the Hill] " + ChatColor.YELLOW + "Awarded" + ChatColor.BLUE + " KOTH Key" + ChatColor.YELLOW + " to " + teamName + ChatColor.WHITE + event.getPlayer().getDisplayName() + ChatColor.YELLOW + "."
			};

			KOTH koth = (KOTH) event.getEvent();
			if (koth.getName().equals(VaultHandler.TEAM_NAME)) {

				messages = new String[]{
						CC.translate(VaultHandler.PREFIX) + " " + ChatColor.GOLD + event.getEvent().getName() + ChatColor.YELLOW + " has been controlled by " + teamName + ChatColor.WHITE + event.getPlayer().getDisplayName() + ChatColor.YELLOW + "!",
						CC.translate(VaultHandler.PREFIX) + " " + ChatColor.YELLOW + "Awarded" + ChatColor.GOLD + " Vault Key" + ChatColor.YELLOW + " to " + teamName + ChatColor.WHITE + event.getPlayer().getDisplayName() + ChatColor.YELLOW + "."
				};

				Team playerTeam = Samurai.getInstance().getTeamHandler().getTeam(event.getPlayer());
				if (playerTeam != null) {
					playerTeam.setVaultCaptures(playerTeam.getVaultCaptures() + 1);
				}

				ItemStack key = Samurai.getInstance().getVaultHandler().getKey();

				event.getPlayer().getInventory().addItem(key);

				if (!event.getPlayer().getInventory().contains(key)) {
					event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), key);
				}

				ItemStack kothSign = Samurai.getInstance().getServerHandler().generateKOTHSign(event.getEvent().getName(), team == null ? event.getPlayer().getName() : team.getName(), EventType.KOTH);

				event.getPlayer().getInventory().addItem(kothSign);

				if (!event.getPlayer().getInventory().contains(kothSign)) {
					event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), kothSign);
				}

				final String[] messagesFinal = messages;

				new BukkitRunnable() {

					@Override
					public void run() {
						for (Player player : Samurai.getInstance().getServer().getOnlinePlayers()) {
							player.sendMessage("");
							player.sendMessage(messagesFinal);
							player.sendMessage("");
						}
					}

				}.runTaskAsynchronously(Samurai.getInstance());

				// Can't forget console now can we
				// but we don't want to give console the filler.
				for (String message : messages) {
					Samurai.getInstance().getLogger().info(message);
				}

				final BasicDBObject dbObject = new BasicDBObject();

				dbObject.put("EventName", event.getEvent().getName());
				dbObject.put("EventType", event.getEvent().getType().name());
				dbObject.put("CapturedAt", new Date());
				dbObject.put("Capper", event.getPlayer().getUniqueId().toString().replace("-", ""));
				dbObject.put("CapperTeam", team == null ? null : team.getUniqueId().toString());
				if (event.getEvent().getType() == EventType.KOTH) {
					dbObject.put("EventLocation", LocationSerializer.serialize(((KOTH) event.getEvent()).getCapLocation().toLocation(event.getPlayer().getWorld())));
				}

				new BukkitRunnable() {

					@Override
					public void run() {
						DBCollection kothCapturesCollection = Samurai.getInstance().getMongoPool().getDB(Samurai.MONGO_DB_NAME).getCollection("KOTHCaptures");
						kothCapturesCollection.insert(dbObject);
					}

				}.runTaskAsynchronously(Samurai.getInstance());
				return;
			}

			ItemStack rewardKey = InventoryUtils.generateKOTHRewardKey(event.getEvent().getName() + " KOTH");
			ItemStack kothSign = Samurai.getInstance().getServerHandler().generateKOTHSign(event.getEvent().getName(), team == null ? event.getPlayer().getName() : team.getName(), EventType.KOTH);

			event.getPlayer().getInventory().addItem(rewardKey);
			event.getPlayer().getInventory().addItem(kothSign);

			if (!event.getPlayer().getInventory().contains(rewardKey)) {
				event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), rewardKey);
			}

			if (!event.getPlayer().getInventory().contains(kothSign)) {
				event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), kothSign);
			}

			Team playerTeam = Samurai.getInstance().getTeamHandler().getTeam(event.getPlayer());
			if (playerTeam != null) {
				playerTeam.setKothCaptures(playerTeam.getKothCaptures() + 1);
			}
		}

		final String[] messagesFinal = messages;

		new BukkitRunnable() {

			@Override
			public void run() {
				for (Player player : Samurai.getInstance().getServer().getOnlinePlayers()) {
					player.sendMessage("");
					player.sendMessage(messagesFinal);
					player.sendMessage("");
				}
			}

		}.runTaskAsynchronously(Samurai.getInstance());

		// Can't forget console now can we
		// but we don't want to give console the filler.
		for (String message : messages) {
			Samurai.getInstance().getLogger().info(message);
		}

		final BasicDBObject dbObject = new BasicDBObject();

		dbObject.put("EventName", event.getEvent().getName());
		dbObject.put("EventType", event.getEvent().getType().name());
		dbObject.put("CapturedAt", new Date());
		dbObject.put("Capper", event.getPlayer().getUniqueId().toString().replace("-", ""));
		dbObject.put("CapperTeam", team == null ? null : team.getUniqueId().toString());
		if (event.getEvent().getType() == EventType.KOTH) {
			dbObject.put("EventLocation", LocationSerializer.serialize(((KOTH) event.getEvent()).getCapLocation().toLocation(event.getPlayer().getWorld())));
		}

		new BukkitRunnable() {

			@Override
			public void run() {
				DBCollection kothCapturesCollection = Samurai.getInstance().getMongoPool().getDB(Samurai.MONGO_DB_NAME).getCollection("KOTHCaptures");
				kothCapturesCollection.insert(dbObject);
			}

		}.runTaskAsynchronously(Samurai.getInstance());
	}

	@EventHandler
	public void onKOTHControlLost(final KOTHControlLostEvent event) {
		if (event.getKOTH().getRemainingCapTime() <= (event.getKOTH().getCapTime() - 30)) {
			if (event.getKOTH().getName().equals(VaultHandler.TEAM_NAME)) {
				Samurai.getInstance().getServer().broadcastMessage(CC.translate(VaultHandler.PREFIX) + " Control of " + ChatColor.YELLOW + event.getKOTH().getName() + ChatColor.GOLD + " lost.");
			} else {
				Samurai.getInstance().getServer().broadcastMessage(ChatColor.GOLD + "[King of the Hill] Control of " + ChatColor.YELLOW + event.getKOTH().getName() + ChatColor.GOLD + " lost.");
			}
		}
	}

	@EventHandler
	public void onKOTHDeactivated(EventDeactivatedEvent event) {

		for (Player player : Bukkit.getOnlinePlayers()) {
			LunarListener.updateWaypoints(player);
		}

		// activate koths every 10m on the kitmap
		if (!Samurai.getInstance().getMapHandler().isKitMap()) {
			return;
		}

		Bukkit.getScheduler().runTaskLater(Samurai.getInstance(), () -> {
			dev.lbuddyboy.samurai.events.EventHandler eventHandler = Samurai.getInstance().getEventHandler();
			List<Event> localEvents = new ArrayList<>(eventHandler.getEvents());

			if (localEvents.isEmpty()) {
				return;
			}

			List<KOTH> koths = new ArrayList<>();
			// don't start a koth while another is active
			for (Event localEvent : localEvents) {
				if (localEvent.isActive()) {
					return;
				} else if (localEvent.getType() == EventType.KOTH) {
					koths.add((KOTH) localEvent);
				}
			}

			KOTH selected = koths.get(Samurai.RANDOM.nextInt(koths.size()));
			selected.activate();
		}, 20 * 5);
	}

}
