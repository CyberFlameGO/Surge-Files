package dev.lbuddyboy.samurai.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.ItemStackSerializer;
import dev.lbuddyboy.samurai.economy.uuid.FrozenUUIDCache;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@CommandAlias("deathrefund")
@CommandPermission("foxtrot.deathrefund")
public class DeathRefundCommand extends BaseCommand {

	public static final Gson GSON = new Gson();

	@Default
	public static void def(Player sender, @Name("id") String id) {
		Samurai.getInstance().getServer().getScheduler().runTaskAsynchronously(Samurai.getInstance(), () -> {
			DBCollection mongoCollection = Samurai.getInstance().getMongoPool().getDB(Samurai.MONGO_DB_NAME).getCollection("Deaths");
			DBObject object = mongoCollection.findOne(id);

			if (object != null) {
				BasicDBObject basicDBObject = (BasicDBObject) object;
				Player player = Bukkit.getPlayer(UUIDfromString(object.get("uuid").toString()));

				if (basicDBObject.containsKey("refundedBy")) {
					sender.sendMessage(ChatColor.RED + "This death was already refunded by " + FrozenUUIDCache.name(UUIDfromString(basicDBObject.getString("refundedBy"))) + ".");
					return;
				}

				if (player == null) {
					sender.sendMessage(ChatColor.RED + "Player isn't on to receive items.");
					return;
				}

				ItemStack[] contents = ItemStackSerializer.itemStackArrayFromBase64(basicDBObject.getString("playerInventory"));
				ItemStack[] armor = ItemStackSerializer.itemStackArrayFromBase64(basicDBObject.getString("playerArmor"));

				if (contents != null) {
					player.getInventory().setContents(contents);
				}

				if (armor != null) {
					player.getInventory().setArmorContents(armor);
				}

				basicDBObject.put("refundedBy", sender.getUniqueId().toString().replace("-", ""));
				basicDBObject.put("refundedAt", System.currentTimeMillis());

				mongoCollection.save(basicDBObject);

				player.sendMessage(ChatColor.GREEN + "Your inventory has been reset to an inventory from a previous life.");
				sender.sendMessage(ChatColor.GREEN + "Successfully refunded inventory to " + player.getName() + ".");

			} else {
				sender.sendMessage(ChatColor.RED + "Death not found.");
			}

		});
	}

	private static UUID UUIDfromString(String string) {
		return UUID.fromString(
				string.replaceFirst(
						"(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
				)
		);
	}

}