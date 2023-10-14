package dev.lbuddyboy.samurai.custom.battlepass.daily;

import lombok.Getter;
import dev.lbuddyboy.samurai.custom.battlepass.challenge.Challenge;
import dev.lbuddyboy.samurai.custom.battlepass.challenge.impl.KillEntityChallenge;
import dev.lbuddyboy.samurai.custom.battlepass.challenge.impl.MineBlockChallenge;
import dev.lbuddyboy.samurai.custom.battlepass.challenge.impl.MineLogChallenge;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Getter
public class DailyChallenges {

	private final UUID identifier = UUID.randomUUID(); // used to determine if a player's daily progress is synced with this set of daily challenges
	private Map<String, Challenge> challenges = new HashMap<>();
	private long expiresAt = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1L);

	public Collection<Challenge> getChallenges() {
		return challenges.values();
	}

	public Challenge getChallenge(String id) {
		return challenges.get(id.toLowerCase());
	}

	public void generate() {

		List<Challenge> challenges = new ArrayList<>();

		challenges.add(new KillEntityChallenge("kill-cows-15", "Kill 15 Cows", 5, true, EntityType.COW, 15));

		challenges.add(new KillEntityChallenge("kill-cows-30", "Kill 30 Cows", 10, true, EntityType.COW, 30));

		challenges.add(new KillEntityChallenge("kill-endermen-10", "Kill 10 Endermen", 5, true, EntityType.ENDERMAN, 10));

		challenges.add(new KillEntityChallenge("kill-endermen-20", "Kill 20 Endermen", 10, true, EntityType.ENDERMAN, 20));

		challenges.add(new KillEntityChallenge("kill-creeper-10", "Kill 10 Creepers", 5, true, EntityType.CREEPER, 10));

		challenges.add(new KillEntityChallenge("kill-creeper-20", "Kill 20 Creepers", 10, true, EntityType.CREEPER, 20));

		challenges.add(new KillEntityChallenge("kill-players-3", "Kill 3 Players", 5, true, EntityType.PLAYER, 3));

		challenges.add(new KillEntityChallenge("kill-players-5", "Kill 5 Players", 10, true, EntityType.PLAYER, 5));

		challenges.add(new MineBlockChallenge("mine-glowstone-16", "Mine 16 Glowstone", 5, true, Material.GLOWSTONE, 16));

		challenges.add(new MineBlockChallenge("mine-glowstone-32", "Mine 32 Glowstone", 10, true, Material.GLOWSTONE, 32));

		challenges.add(new MineLogChallenge("mine-logs-64", "Mine 64 Logs", 5, true, 64));

		challenges.add(new MineLogChallenge("mine-logs-128", "Mine 128 Logs", 10, true, 128));

		challenges.add(new MineBlockChallenge("mine-sand-64", "Mine 64 Sand", 5, true, Material.SAND, 64));

		challenges.add(new MineBlockChallenge("mine-sand-128", "Mine 128 Sand", 10, true, Material.SAND, 128));

		for (Challenge challenge : challenges) {
			this.challenges.put(challenge.getId(), challenge);
		}
	}

}
