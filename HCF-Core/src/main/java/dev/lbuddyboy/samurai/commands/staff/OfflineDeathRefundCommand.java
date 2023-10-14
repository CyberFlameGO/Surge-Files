/*
package dev.lbuddyboy.samurai.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Name;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.offline.OfflineHandler;
import dev.lbuddyboy.samurai.util.ItemStackSerializer;
import dev.lbuddyboy.samurai.util.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

@CommandAlias("offlinedeathrefund")
@CommandPermission("foxtrot.deathrefund")
public class OfflineDeathRefundCommand extends BaseCommand {

	public static final Gson GSON = new Gson();

	@Default
	public static void def(Player sender, @Name("id") String id) {
		Samurai.getInstance().getServer().getScheduler().runTaskAsynchronously(Samurai.getInstance(), () -> {
			DBCollection mongoCollection = Samurai.getInstance().getMongoPool().getDB(Samurai.MONGO_DB_NAME).getCollection("Deaths");
			DBObject object = mongoCollection.findOne(id);

			if (object != null) {
				BasicDBObject basicDBObject = (BasicDBObject) object;
				UUID target = UUIDfromString(object.get("uuid").toString());

				if (Bukkit.getPlayer(target) != null) {
					sender.sendMessage(ChatColor.RED + "Player is online!");
					return;
				}

				List<ItemStack> contents = ItemStackSerializer.fromBase64List(GSON.fromJson(basicDBObject.getString("playerInventory"), OfflineHandler.LIST_STRING_TYPE));
				List<ItemStack> armor = ItemStackSerializer.fromBase64List(GSON.fromJson(basicDBObject.getString("playerArmor"), OfflineHandler.LIST_STRING_TYPE));
				ItemStack[] contentsArray = new ItemStack[36];
				ItemStack[] armorArray = new ItemStack[4];
				int i = 0;
				for (ItemStack itemStack : armor) {
					armorArray[i++] = itemStack;
				}
				i = 0;
				for (ItemStack itemStack : contents) {
					contentsArray[i++] = itemStack;
				}

				Samurai.getInstance().getOfflineHandler().saveContents(target, contentsArray);
				Samurai.getInstance().getOfflineHandler().saveArmor(target, armorArray);

				Samurai.getInstance().getOfflineInvEditedMap().setToggled(target, true);

				basicDBObject.put("refundedBy", sender.getUniqueId().toString().replace("-", ""));
				basicDBObject.put("refundedAt", System.currentTimeMillis());

				mongoCollection.save(basicDBObject);

				sender.sendMessage(ChatColor.GREEN + "Successfully refunded offline inventory to " + UUIDUtils.name(target) + ".");

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

}*/
