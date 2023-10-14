package dev.lbuddyboy.samurai.events.citadel;

import com.google.gson.JsonParser;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import dev.lbuddyboy.samurai.util.FileHelper;
import dev.lbuddyboy.samurai.util.ItemStackSerializer;
import dev.lbuddyboy.samurai.util.LocationSerializer;
import dev.lbuddyboy.samurai.util.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.events.Event;
import dev.lbuddyboy.samurai.events.citadel.events.CitadelCapturedEvent;
import dev.lbuddyboy.samurai.events.citadel.listeners.CitadelListener;
import dev.lbuddyboy.samurai.events.citadel.tasks.CitadelSaveTask;
import dev.lbuddyboy.samurai.events.koth.KOTH;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.claims.Claim;
import dev.lbuddyboy.samurai.team.claims.LandBoard;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import dev.lbuddyboy.samurai.util.object.CuboidRegion;
import org.bson.types.ObjectId;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class CitadelHandler {

	public static final String PREFIX = ChatColor.DARK_PURPLE + "[Citadel]";
	public static final String TITLE = ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Citadel";

	private File citadelInfo;
	@Getter
	private Set<ObjectId> cappers;
	@Getter
	private Date lootable;

	@Getter
	private static Location apparatusStrength;
	@Getter
	private static Location apparatusResistance;
	@Getter
	@Setter
	private static int deathStage = 1;
	@Getter
	private static Location death1Apparatus;
	@Getter
	private static Location death2Apparatus;
	@Getter
	@Setter
	private static long cappedAt;

	@Getter
	private Set<Location> citadelChests = new HashSet<>();
	@Getter
	private List<ItemStack> citadelLoot = new ArrayList<>();

	public CitadelHandler() {
		citadelInfo = new File(Samurai.getInstance().getDataFolder(), "citadelInfo.json");

		loadCitadelInfo();
		Samurai.getInstance().getServer().getPluginManager().registerEvents(new CitadelListener(), Samurai.getInstance());

		(new CitadelSaveTask()).runTaskTimerAsynchronously(Samurai.getInstance(), 0L, 20 * 60 * 5);

		Team team = Samurai.getInstance().getTeamHandler().getTeam("Citadel");
		if (team != null) {
			for (Claim claim : team.getClaims()) {
				for (Location location : new CuboidRegion("Citadel", claim.getMinimumPoint(), claim.getMaximumPoint())) {
					if (location.getBlock().getType().equals(Material.YELLOW_WOOL)) {
						apparatusStrength = location;
					}
					if (location.getBlock().getType().equals(Material.BLUE_WOOL)) {
						apparatusResistance = location;
					}
					if (location.getBlock().getType().equals(Material.GOLD_BLOCK)) {
						death2Apparatus = location;
					}
					if (location.getBlock().getType().equals(Material.IRON_BLOCK)) {
						death1Apparatus = location;
					}
				}
			}
		}

		saveCitadelInfo();

	}

	public KOTH getEvent() {
		for (Event event : Samurai.getInstance().getEventHandler().getEvents()) {
			if (event instanceof KOTH && event.getName().equalsIgnoreCase("Citadel")) {
				return (KOTH) event;
			}
		}
		return null;
	}

	public boolean isActive() {
		return getEvent() != null && getEvent().isActive();
	}

	public boolean canUsePvPClass(Player player) {
		if (true)
			return true;
		//todo uhm why ?
		if (!isActive()) {
			return true;
		}

		World world = Bukkit.getWorld(getEvent().getWorld());
		Location capLocation = LocationUtil.fromVector(world, getEvent().getCapLocation());

		if (world == null || capLocation == null) {
			return true;
		}

		Claim claim = LandBoard.getInstance().getClaim(capLocation);
		return !claim.contains(player);
	}

	public void loadCitadelInfo() {
		try {
			if (!citadelInfo.exists() && citadelInfo.createNewFile()) {
				BasicDBObject dbo = new BasicDBObject();

				dbo.put("cappers", new HashSet<>());
				dbo.put("lootable", new Date());
				dbo.put("chests", new BasicDBList());
				dbo.put("loot", new BasicDBList());

				FileHelper.writeFile(citadelInfo, Samurai.GSON.toJson(new JsonParser().parse(dbo.toString())));
			}

			BasicDBObject dbo = (BasicDBObject) JSON.parse(FileHelper.readFile(citadelInfo));

			if (dbo != null) {
				this.cappers = new HashSet<>();

				// Conversion
				if (dbo.containsField("cappedAt")) {
					cappedAt = dbo.getLong("cappedAt");
				}
				if (dbo.containsField("death1")) {
					death1Apparatus = LocationSerializer.deserialize((BasicDBObject) dbo.get("death1"));
				}
				if (dbo.containsField("death2")) {
					death2Apparatus = LocationSerializer.deserialize((BasicDBObject) dbo.get("death2"));
				}
				if (dbo.containsField("appStr")) {
					apparatusStrength = LocationSerializer.deserialize((BasicDBObject) dbo.get("appStr"));
				}
				if (dbo.containsField("appRes")) {
					apparatusResistance = LocationSerializer.deserialize((BasicDBObject) dbo.get("appRes"));
				}

				if (dbo.containsField("capper")) {
					cappers.add(new ObjectId(dbo.getString("capper")));
				}

				for (String capper : (List<String>) dbo.get("cappers")) {
					cappers.add(new ObjectId(capper));
				}

				this.lootable = dbo.getDate("lootable");

				BasicDBList chests = (BasicDBList) dbo.get("chests");

				for (Object chestObj : chests) {
					BasicDBObject chest = (BasicDBObject) chestObj;
					citadelChests.add(LocationSerializer.deserialize((BasicDBObject) chest.get("location")));
				}

				citadelLoot.addAll(List.of(Objects.requireNonNull(ItemStackSerializer.itemStackArrayFromBase64(dbo.getString("loot")))));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveCitadelInfo() {
		try {
			BasicDBObject dbo = new BasicDBObject();

			dbo.put("cappers", cappers.stream().map(ObjectId::toString).collect(Collectors.toList()));
			dbo.put("cappedAt", cappedAt);
			dbo.put("lootable", lootable);
			dbo.put("death1", LocationSerializer.serialize(death1Apparatus));
			dbo.put("death2", LocationSerializer.serialize(death2Apparatus));
			dbo.put("appRes", LocationSerializer.serialize(apparatusResistance));
			dbo.put("appStr", LocationSerializer.serialize(apparatusStrength));

			BasicDBList chests = new BasicDBList();

			for (Location citadelChest : citadelChests) {
				BasicDBObject chest = new BasicDBObject();
				chest.put("location", LocationSerializer.serialize(citadelChest));
				chests.add(chest);
			}

			dbo.put("chests", chests);
			dbo.put("loot", ItemStackSerializer.itemStackArrayToBase64(citadelLoot.toArray(new ItemStack[0])));

			citadelInfo.delete();
			FileHelper.writeFile(citadelInfo, Samurai.GSON.toJson(new JsonParser().parse(dbo.toString())));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void resetCappers() {
		this.cappers.clear();
	}

	public void addCapper(ObjectId capper) {
		this.cappers.add(capper);
		this.lootable = generateLootableDate();

		Samurai.getInstance().getServer().getPluginManager().callEvent(new CitadelCapturedEvent(capper));
		saveCitadelInfo();
	}

	public boolean canLootCitadel(Player player) {
		if (cappers == null) {
			return false;
		}
		if (cappers.isEmpty()) {
			return false;
		}
		Team team = Samurai.getInstance().getTeamHandler().getTeam(player);
		return ((team != null && cappers.contains(team.getUniqueId())) && cappedAt > System.currentTimeMillis());
	}

	// Credit to http://stackoverflow.com/a/3465656 on StackOverflow.
	private Date generateLootableDate() {
		Calendar date = Calendar.getInstance();
		int diff = Calendar.TUESDAY - date.get(Calendar.DAY_OF_WEEK);

		if (diff <= 0) {
			diff += 7;
		}

		date.add(Calendar.DAY_OF_MONTH, diff);

		// 11:59 PM
		date.set(Calendar.HOUR_OF_DAY, 23);
		date.set(Calendar.MINUTE, 59);
		date.set(Calendar.SECOND, 59);

		return (date.getTime());
	}

	public void scanLoot() {
		citadelChests.clear();

		for (Team team : Samurai.getInstance().getTeamHandler().getTeams()) {
			if (team.getOwner() != null) {
				continue;
			}

			if (team.hasDTRBitmask(DTRBitmask.CITADEL)) {
				for (Claim claim : team.getClaims()) {
					for (Location location : new CuboidRegion("Citadel", claim.getMinimumPoint(), claim.getMaximumPoint())) {
						if (location.getBlock().getType().name().contains("CHEST")) {
							citadelChests.add(location);
						}
					}
				}
			}
		}
	}

	public int respawnCitadelChests() {
		int respawned = 0;

		for (Location chest : citadelChests) {
			if (respawnCitadelChest(chest)) {
				respawned++;
			}
		}

		return (respawned);
	}

	public boolean respawnCitadelChest(Location location) {
		BlockState blockState = location.getBlock().getState();

		if (blockState instanceof Chest) {
			Chest chest = (Chest) blockState;

			location.getBlock().setType(Material.CHEST);

			chest.getBlockInventory().clear();
			chest.getBlockInventory().addItem(citadelLoot.get(Samurai.RANDOM.nextInt(citadelLoot.size())));
			return (true);
		} else {
			Samurai.getInstance().getLogger().warning("Citadel box defined at [" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + "] isn't a chest!");
			return (false);
		}
	}

}