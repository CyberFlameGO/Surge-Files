package dev.lbuddyboy.samurai.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.client.model.DBCollectionFindOptions;
import dev.lbuddyboy.samurai.economy.uuid.FrozenUUIDCache;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.commands.menu.KillsMenu;
import dev.lbuddyboy.samurai.map.stats.StatsEntry;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 10/01/2022 / 2:39 AM
 * SteelHCF-master / dev.lbuddyboy.samurai.commands.staff
 */

@CommandAlias("kills")
@CommandPermission("foxtrot.kills")
public class KillsCommand extends BaseCommand {

	@Default
	@CommandCompletion("@players")
	public static void kills(Player sender, @Name("player") UUID player) {
		sender.sendMessage("");
		sender.sendMessage(ChatColor.GRAY + "Grabbing latest kills of " + FrozenUUIDCache.name(player) + "...");
		sender.sendMessage("");

		DBCollection mongoCollection = Samurai.getInstance().getMongoPool().getDB(Samurai.MONGO_DB_NAME).getCollection("Deaths");

		List<DBObject> objectList = mongoCollection.find(new BasicDBObject("killerUUID", player.toString().replace("-", "")), new DBCollectionFindOptions()).sort(new BasicDBObject("when", -1)).toArray();

		if (objectList.isEmpty()) {
			sender.sendMessage(ChatColor.RED + FrozenUUIDCache.name(player) + " has no kills to display.");
		} else {
			Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> {
				new KillsMenu(objectList).openMenu(sender);
			});
		}
	}

	@Subcommand("realkills")
	public static void realKills(CommandSender sender, @Name("player") UUID target) {

		StatsEntry entry = Samurai.getInstance().getMapHandler().getStatsHandler().getStats(target);

		DBCollection mongoCollection = Samurai.getInstance().getMongoPool().getDB(Samurai.MONGO_DB_NAME).getCollection("Deaths");

		List<DBObject> objectList = mongoCollection.find(new BasicDBObject("killerUUID", target.toString().replace("-", "")), new DBCollectionFindOptions()).sort(new BasicDBObject("when", -1)).toArray();

		sender.sendMessage(CC.translate(FrozenUUIDCache.name(target) + "'s Information"));
		sender.sendMessage(CC.translate(" &f● &gNon-Calculated Kills:&f " + entry.getKills()));

		Map<UUID, KillDupe> killDupedMap = new HashMap<>();
		for (int i = 0; i < objectList.size(); i++) {
			try {
				BasicDBObject object = (BasicDBObject) objectList.get(i);
				BasicDBObject nextObject = (BasicDBObject) objectList.get(i + 1);
				BasicDBObject nextNextObject = (BasicDBObject) objectList.get(i + 2);

				if (object != null) {
					UUID currentKiller = UUIDfromString(object.get("uuid").toString());
					if (nextObject != null) {
						UUID nextKiller = UUIDfromString(nextObject.get("uuid").toString());

						if (nextNextObject != null) {
							UUID nextNextKiller = UUIDfromString(nextNextObject.get("uuid").toString());
							if (currentKiller.toString().equals(nextKiller.toString()) && currentKiller.toString().equals(nextNextKiller.toString())) {
								KillDupe killDupe = killDupedMap.getOrDefault(currentKiller, new KillDupe());

								if (killDupedMap.containsKey(currentKiller)) {
									killDupe.setAmountDuped(killDupe.getAmountDuped() + 1);
								} else {
									killDupe.setDuped(currentKiller);
									killDupe.setAmountDuped(1);
									killDupe.setDupedInitialTime(nextObject.getDate("when").getTime());
								}

								killDupedMap.put(nextKiller, killDupe);
							}
						}

						Team currentTeam = Samurai.getInstance().getTeamHandler().getTeam(currentKiller);
						Team nextTeam = Samurai.getInstance().getTeamHandler().getTeam(nextKiller);

						if (currentTeam != null && nextTeam != null && currentTeam.getName().equals(nextTeam.getName())) {
							if (nextNextObject != null) {
								UUID nextNextKiller = UUIDfromString(nextNextObject.get("uuid").toString());
								Team nextNextTeam = Samurai.getInstance().getTeamHandler().getTeam(nextNextKiller);
								if (nextNextTeam != null && currentTeam.getName().equals(nextNextTeam.getName())) {

									KillDupe killDupe = killDupedMap.getOrDefault(currentKiller, new KillDupe());

									if (killDupedMap.containsKey(currentKiller)) {
										killDupe.setAmountDuped(killDupe.getAmountDuped() + 1);
									} else {
										killDupe.setDuped(currentKiller);
										killDupe.setAmountDuped(1);
										killDupe.setDupedInitialTime(nextObject.getDate("when").getTime());
									}

									killDupedMap.put(nextKiller, killDupe);
								}
							}

						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		int killsToSubtract = 0;
		for (Map.Entry<UUID, KillDupe> killDupeEntry : killDupedMap.entrySet()) {
			killsToSubtract += killDupeEntry.getValue().getAmountDuped();
		}

		sender.sendMessage(CC.translate(" &f● &gCalculated Kills:&f " + (entry.getKills() - killsToSubtract)));

	}

	private static UUID UUIDfromString(String string) {
		return UUID.fromString(
				string.replaceFirst(
						"(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
				)
		);
	}

	@lombok.Data
	private static class KillDupe {

		private UUID duped;
		private long dupedInitialTime;
		private int amountDuped;

	}

}
