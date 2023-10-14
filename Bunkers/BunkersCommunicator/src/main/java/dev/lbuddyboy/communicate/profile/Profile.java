package dev.lbuddyboy.communicate.profile;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lbuddyboy.communicate.BunkersCom;
import dev.lbuddyboy.communicate.FinalGame;
import lombok.Data;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 23/09/2021 / 2:24 AM
 * LBuddyBoy Development / me.lbuddyboy.core.profile
 */

@Data
public class Profile {

	@Getter
	private static final JsonParser jsonParser = new JsonParser();

	private final UUID uniqueId;
	private String name;
	private List<FinalGame> gameHistory;
	private int oresMined, kills, deaths, wins, kothCaptures;

	private boolean loaded;

	public Profile(UUID uniqueId) {
		this.uniqueId = uniqueId;
		this.name = Bukkit.getOfflinePlayer(uniqueId).getName();
		this.gameHistory = new ArrayList<>();

		load();
	}

	public void load() {

		Document document = BunkersCom.getInstance().getMongoHandler().getMongoDatabase().getCollection("profiles").find(Filters.eq("uniqueId", this.uniqueId.toString())).first();

		if (document == null) {
			System.out.println("Detected a null profile.");
			Player player = Bukkit.getPlayer(this.uniqueId);
			if (player != null) {
				Bukkit.getScheduler().runTaskLater(BunkersCom.getInstance(), () -> player.kickPlayer("Failed to load your profile"), 10);
			}
			return;
		}

		if (document.containsKey("oresMined")) {
			this.oresMined = document.getInteger("oresMined");
		}

		if (document.containsKey("kills")) {
			this.kills = document.getInteger("kills");
		}

		if (document.containsKey("deaths")) {
			this.deaths = document.getInteger("deaths");
		}

		if (document.containsKey("wins")) {
			this.wins = document.getInteger("wins");
		}

		if (document.containsKey("kothCaptures")) {
			this.kothCaptures = document.getInteger("kothCaptures");
		}

		if (document.containsKey("gameHistory")) {
			for (JsonElement jsonElement : jsonParser.parse(document.getString("gameHistory")).getAsJsonArray()) {
				this.gameHistory.add(FinalGame.deserialize(jsonElement.getAsJsonObject()));
			}
		}

		loaded = true;
	}

	public void save() {
		Bukkit.getScheduler().runTaskAsynchronously(BunkersCom.getInstance(), () -> {
			Document document = new Document();

			document.put("uniqueId", this.uniqueId.toString());
			document.put("name", this.name);
			document.put("oresMined", this.oresMined);
			document.put("kills", this.kills);
			document.put("deaths", this.deaths);
			document.put("wins", this.wins);
			document.put("kothCaptures", this.kothCaptures);

			JsonArray gameHistory = new JsonArray();
			this.gameHistory.forEach(game -> gameHistory.add(game.serialize()));
			document.put("gameHistory", gameHistory.toString());

			BunkersCom.getInstance().getMongoHandler().getMongoDatabase().getCollection("profiles").replaceOne(Filters.eq("uniqueId", this.uniqueId.toString()), document, new ReplaceOptions().upsert(true));
		});

	}

}
