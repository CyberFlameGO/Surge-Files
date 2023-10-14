package dev.lbuddyboy.bunkers.team;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import dev.lbuddyboy.bunkers.Bunkers;
import dev.lbuddyboy.bunkers.game.shop.Shop;
import dev.lbuddyboy.bunkers.nametag.FrozenNametagHandler;
import dev.lbuddyboy.bunkers.util.Cuboid;
import dev.lbuddyboy.bunkers.util.LocationSerializer;
import dev.lbuddyboy.bunkers.util.bukkit.CC;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 19/03/2022 / 7:09 PM
 * SteelBunkers / com.steelpvp.bunkers.team
 */

@Data
public class Team {

	private final ChatColor color;
	private final List<UUID> members;
	private double dtr;
	private Location corner1, corner2, home, buildShopSpawn, combatShopSpawn, sellShopSpawn, enchantShopSpawn, rally;
	private Map<Material, List<Location>> locations = new HashMap<>();
	private List<Shop> shops = new ArrayList<>();
	private BukkitTask rallyTask;

	public Team(ChatColor color, List<UUID> members, double dtr) {
		this.color = color;
		this.members = members;
		this.dtr = dtr;
	}

	public void load() {

		String abs = "teams." + this.color.name().toLowerCase() + ".";

		String claim1 = Bunkers.getInstance().getTeamHandler().getConfig().getString(abs + "claim-1");
		if (claim1 != null) {
			this.corner1 = LocationSerializer.deserializeString(claim1);
		}

		String claim2 = Bunkers.getInstance().getTeamHandler().getConfig().getString(abs + "claim-2");
		if (claim2 != null) {
			this.corner2 = LocationSerializer.deserializeString(claim2);
		}

		if (Bunkers.getInstance().getTeamHandler().getConfig().contains(abs + "home")) {
			this.home = LocationSerializer.deserializeString(Bunkers.getInstance().getTeamHandler().getConfig().getString(abs + "home"));
		}

		if (Bunkers.getInstance().getTeamHandler().getConfig().contains(abs + "buildShopSpawn")) {
			this.buildShopSpawn = LocationSerializer.deserializeString(Bunkers.getInstance().getTeamHandler().getConfig().getString(abs + "buildShopSpawn"));
		}

		if (Bunkers.getInstance().getTeamHandler().getConfig().contains(abs + "combatShopSpawn")) {
			this.combatShopSpawn = LocationSerializer.deserializeString(Bunkers.getInstance().getTeamHandler().getConfig().getString(abs + "combatShopSpawn"));
		}

		if (Bunkers.getInstance().getTeamHandler().getConfig().contains(abs + "sellShopSpawn")) {
			this.sellShopSpawn = LocationSerializer.deserializeString(Bunkers.getInstance().getTeamHandler().getConfig().getString(abs + "sellShopSpawn"));
		}

		if (Bunkers.getInstance().getTeamHandler().getConfig().contains(abs + "enchantShopSpawn")) {
			this.enchantShopSpawn = LocationSerializer.deserializeString(Bunkers.getInstance().getTeamHandler().getConfig().getString(abs + "enchantShopSpawn"));
		}

		this.shops.add(new Shop(Villager.Type.JUNGLE, Shop.SELL_SLOTS, this.sellShopSpawn));
		this.shops.add(new Shop(Villager.Type.TAIGA, Shop.COMBAT_SLOTS, this.combatShopSpawn));
		this.shops.add(new Shop(Villager.Type.SWAMP, Shop.BUY_SLOTS, this.buildShopSpawn));
		this.shops.add(new Shop(Villager.Type.SAVANNA, Shop.ENCHANTMENT_SLOTS, this.enchantShopSpawn));

		for (String key : Bunkers.getInstance().getTeamHandler().getConfig().getConfigurationSection("teams." + this.color.name().toLowerCase()).getKeys(false)) {
			Material material = Material.getMaterial(key);
			if (material == null) continue;

			List<Location> locations = new ArrayList<>();
			JsonParser parser = new JsonParser();
			for (JsonElement element : parser.parse(Bunkers.getInstance().getTeamHandler().getConfig().getString(abs + material.name())).getAsJsonArray()) {
				locations.add(LocationSerializer.getLocation(element.getAsJsonObject()));
			}

			this.locations.put(material, locations);
		}

		Bukkit.getConsoleSender().sendMessage(CC.translate(Bunkers.PREFIX + "Loaded " + getDisplay() + " Team&f."));
	}

	public void save() {

		YamlConfiguration config = Bunkers.getInstance().getTeamHandler().getConfig();

		String abs = "teams." + this.color.name().toLowerCase() + ".";

		if (!config.contains("teams." + this.color.name().toLowerCase())) {
			config.createSection("teams." + this.color.name().toLowerCase());
		}

		if (this.corner1 != null) {
			config.set(abs + "claim-1", LocationSerializer.serializeString(this.corner1));
		}

		if (this.corner2 != null) {
			config.set(abs + "claim-2", LocationSerializer.serializeString(this.corner2));
		}

		if (this.home != null) {
			config.set(abs + "home", LocationSerializer.serializeString(this.home));
		}

		if (this.buildShopSpawn != null) {
			config.set(abs + "buildShopSpawn", LocationSerializer.serializeString(this.buildShopSpawn));
		}

		if (this.enchantShopSpawn != null) {
			config.set(abs + "enchantShopSpawn", LocationSerializer.serializeString(this.enchantShopSpawn));
		}

		if (this.combatShopSpawn != null) {
			config.set(abs + "combatShopSpawn", LocationSerializer.serializeString(this.combatShopSpawn));
		}

		if (this.sellShopSpawn != null) {
			config.set(abs + "sellShopSpawn", LocationSerializer.serializeString(this.sellShopSpawn));
		}

		for (Map.Entry<Material, List<Location>> entry : this.locations.entrySet()) {
			JsonArray array = new JsonArray();
			entry.getValue().forEach(location -> array.add(LocationSerializer.toJsonObject(location)));
			config.set(abs + entry.getKey().name(), array.toString());
		}

		try {
			Bunkers.getInstance().getTeamHandler().getYamlDoc().save();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public Cuboid getCuboid() {
		if (this.corner1 == null || this.corner2 == null) return null;

		return new Cuboid(this.corner1, this.corner2);
	}

	public void setCuboid(Cuboid cuboid) {
		this.corner1 = cuboid.getUpperSW();
		this.corner2 = cuboid.getLowerNE();
		save();
	}

	public String getDisplay() {
		return this.color + ChatColor.BOLD.toString() + getName();
	}

	public String getName() {
		if (color == ChatColor.RED) {
			return "ʀᴇᴅ";
		}
		if (color == ChatColor.YELLOW) {
			return "ʏᴇʟʟᴏᴡ";
		}
		if (color == ChatColor.GREEN) {
			return "ɢʀᴇᴇɴ";
		}
		if (color == ChatColor.BLUE) {
			return "ʙʟᴜᴇ";
		}

		return "ᴄᴇɴᴛʀᴀʟ";
	}

	public List<Player> getOnlineMembers() {
		return this.members.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).filter(p -> !p.hasMetadata("modmode") && !p.hasMetadata("spectator")).collect(Collectors.toList());
	}

	public void removePlayer(Player player) {
		getMembers().remove(player.getUniqueId());
		setDtr(getDtr() - 1.1);
		FrozenNametagHandler.reloadPlayer(player);
	}

	public void addPlayer(Player player) {
		Bukkit.getScheduler().runTask(Bunkers.getInstance(), () -> {
			player.sendMessage(CC.translate("&aYou have just joined the " + getDisplay() + "&a."));
			this.members.add(player.getUniqueId());

			this.setDtr(this.getDtr() + 1.1);
			FrozenNametagHandler.reloadPlayer(player);

			player.setPlayerListName(CC.translate(this.color + player.getName()));
		});
	}

	public void resetClaim() {
		for (Map.Entry<Material, List<Location>> entry : getLocations().entrySet()) {
			for (Location location : entry.getValue()) {
				location.getBlock().setType(entry.getKey());
			}
		}
	}

	public String getDTRFormatted() {
		ChatColor chatColor = ChatColor.GREEN;
		if (this.dtr <= 1.0) chatColor = ChatColor.YELLOW;
		if (this.dtr <= 0) chatColor = ChatColor.DARK_RED;
		return chatColor + new DecimalFormat("0.0").format(this.dtr);
	}

	public boolean isRaidable() {
		return this.dtr < 0;
	}

	public Shop getShop(Villager.Type type) {
		for (Shop shop : this.shops) {
			if (shop.getVillagerType() == type) return shop;
		}
		return null;
	}

}
