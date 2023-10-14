package dev.lbuddyboy.communicate;

import com.google.gson.JsonObject;
import lombok.Data;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 14/04/2022 / 9:20 PM
 * BunkersCommunicator / dev.lbuddyboy.communicate
 */

@Data
public class FinalGame {

	private String server;
	private String winner;
	private String team;
	private int kills, deaths;
	private long startedAt, endedAt;

	public JsonObject serialize() {
		JsonObject object = new JsonObject();

		object.addProperty("server", this.server);
		object.addProperty("winner", this.winner);
		object.addProperty("team", this.team);
		object.addProperty("kills", this.kills);
		object.addProperty("deaths", this.deaths);
		object.addProperty("startedAt", this.startedAt);
		object.addProperty("endedAt", this.endedAt);

		return object;
	}

	public static FinalGame deserialize(JsonObject object) {
		FinalGame finalGame = new FinalGame();

		finalGame.setServer(object.get("server").getAsString());
		finalGame.setWinner(object.get("winner").getAsString());
		finalGame.setTeam(object.get("team").getAsString());
		finalGame.setKills(object.get("kills").getAsInt());
		finalGame.setDeaths(object.get("deaths").getAsInt());
		finalGame.setStartedAt(object.get("startedAt").getAsLong());
		finalGame.setEndedAt(object.get("endedAt").getAsLong());

		return finalGame;
	}

}
