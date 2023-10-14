package dev.lbuddyboy.bunkers.team;

import com.mongodb.client.MongoCollection;
import dev.lbuddyboy.bunkers.Bunkers;
import dev.lbuddyboy.bunkers.util.ShopUtils;
import dev.lbuddyboy.bunkers.util.YamlDoc;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 19/03/2022 / 7:10 PM
 * SteelBunkers / com.steelpvp.bunkers.team
 */

@Getter
public class TeamHandler {

	private final Map<String, Team> teams;
	private final MongoCollection<Document> collection;
	private final YamlDoc yamlDoc;

	public TeamHandler() {
		this.teams = new ConcurrentHashMap<>();

		this.yamlDoc = new YamlDoc(Bunkers.getInstance().getDataFolder(), "teams.yml");
		this.collection = Bunkers.getInstance().getMongoHandler().getMongoDatabase().getCollection("Teams");
		ShopUtils.loadDefaults();

		this.teams.put("Red", new Team(ChatColor.RED, new ArrayList<>(), 0.0));
		this.teams.put("Blue", new Team(ChatColor.BLUE, new ArrayList<>(), 0.0));
		this.teams.put("Green", new Team(ChatColor.GREEN, new ArrayList<>(), 0.0));
		this.teams.put("Yellow", new Team(ChatColor.YELLOW, new ArrayList<>(), 0.0));
		this.teams.put("Central", new Team(ChatColor.LIGHT_PURPLE, new ArrayList<>(), 999.9));

		Bukkit.getScheduler().runTaskLater(Bunkers.getInstance(), () -> {
			for (Team team : this.teams.values()) {
				team.load();
			}
			Bukkit.getScheduler().runTaskLater(Bunkers.getInstance(), () -> {
				for (Team team : this.teams.values()) {
					team.save();
				}
			}, 20);
		}, 20);

	}

	public Team getTeam(Player player) {
		return getTeam(player.getUniqueId());
	}

	public Team getTeam(UUID uuid) {
		for (Team team : this.teams.values()) {
			if (team.getMembers().contains(uuid)) return team;
		}
		return null;
	}

	public Team getTeam(Location location) {
		for (Team team : this.teams.values()) {
			if (team.getCuboid() != null && team.getCuboid().contains(location)) return team;
		}
		return null;
	}

	public List<Team> getAliveTeams() {
		List<Team> teams = new ArrayList<>();
		for (Team team : this.getTeams().values()) {
			if (team.getColor() == ChatColor.LIGHT_PURPLE) continue;
			if (team.getOnlineMembers().size() <= 0) continue;
			teams.add(team);
		}
		return teams;
	}

	public Team getTeam(String team) {
		return this.teams.getOrDefault(team, null);
	}

	public YamlConfiguration getConfig() {
		return this.yamlDoc.gc();
	}

}
