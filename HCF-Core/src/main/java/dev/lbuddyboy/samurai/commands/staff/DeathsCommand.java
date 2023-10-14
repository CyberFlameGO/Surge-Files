package dev.lbuddyboy.samurai.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.client.model.DBCollectionFindOptions;
import dev.lbuddyboy.samurai.economy.uuid.FrozenUUIDCache;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.commands.menu.DeathsMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

@CommandAlias("deaths")
@CommandPermission("foxtrot.deaths")
public class DeathsCommand extends BaseCommand {

	public static final Gson GSON = new Gson();

	@Default
	@CommandCompletion("@players")
	public static void def(Player sender, @Name("player") UUID player) {
		sender.sendMessage("");
		sender.sendMessage(ChatColor.GRAY + "Grabbing latest deaths of " + FrozenUUIDCache.name(player) + "...");
		sender.sendMessage("");

		DBCollection mongoCollection = Samurai.getInstance().getMongoPool().getDB(Samurai.MONGO_DB_NAME).getCollection("Deaths");

		List<DBObject> objectList = mongoCollection.find(new BasicDBObject("uuid", player.toString().replace("-", "")), new DBCollectionFindOptions()).sort(new BasicDBObject("when", -1)).toArray();

		if (objectList.isEmpty()) {
			sender.sendMessage(ChatColor.RED + FrozenUUIDCache.name(player) + " has no deaths to display.");
		} else {
			Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> {
				new DeathsMenu(objectList).openMenu(sender);
			});
		}
	}

}