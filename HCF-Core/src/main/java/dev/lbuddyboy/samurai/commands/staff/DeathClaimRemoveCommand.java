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
import dev.lbuddyboy.samurai.economy.uuid.FrozenUUIDCache;
import dev.lbuddyboy.samurai.util.ItemStackSerializer;
import dev.lbuddyboy.samurai.util.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.UUID;

@CommandAlias("deathclaimremove")
@CommandPermission("foxtrot.deathrefund")
public class DeathClaimRemoveCommand extends BaseCommand {

	public static final Gson GSON = new Gson();

	@Default
	public static void def(Player sender, @Name("id") String id) {
		Samurai.getInstance().getServer().getScheduler().runTaskAsynchronously(Samurai.getInstance(), () -> {
			DBCollection mongoCollection = Samurai.getInstance().getMongoPool().getDB(Samurai.MONGO_DB_NAME).getCollection("Deaths");
			DBObject object = mongoCollection.findOne(id);

			if (object != null) {
				BasicDBObject basicDBObject = (BasicDBObject) object;
				UUID uuid = UUIDfromString(object.get("uuid").toString());
				Player player = Bukkit.getPlayer(uuid);

				if (!sender.isOp()) {
					if (player != null) {
						sender.sendMessage(ChatColor.RED + "Player is already online.");
						return;
					}
				}

				ItemStack[] contents = ItemStackSerializer.itemStackArrayFromBase64(basicDBObject.getString("playerInventory"));
				ItemStack[] armor = ItemStackSerializer.itemStackArrayFromBase64(basicDBObject.getString("playerArmor"));

				Samurai.getInstance().getOfflineHandler().removeReviveContents(uuid, Arrays.stream(contents).filter(i -> i != null && i.getType() != Material.AIR).toArray(ItemStack[]::new));
				Samurai.getInstance().getOfflineHandler().removeReviveContents(uuid, Arrays.stream(armor).filter(i -> i != null && i.getType() != Material.AIR).toArray(ItemStack[]::new));

				basicDBObject.put("refundedBy", sender.getUniqueId().toString().replace("-", ""));
				basicDBObject.put("refundedAt", System.currentTimeMillis());

				mongoCollection.save(basicDBObject);

				sender.sendMessage(ChatColor.GREEN + "Successfully removed refund inventory from " + UUIDUtils.name(uuid) + "'s deaths claim.");

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