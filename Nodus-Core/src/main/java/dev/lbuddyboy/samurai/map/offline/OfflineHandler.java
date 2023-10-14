package dev.lbuddyboy.samurai.map.offline;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lbuddyboy.flash.util.bukkit.ItemUtils;
import dev.lbuddyboy.samurai.map.offline.listener.OfflineInventoryListener;
import dev.lbuddyboy.samurai.map.offline.listener.OfflineListener;
import dev.lbuddyboy.samurai.util.ItemStackSerializer;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 01/03/2022 / 10:59 AM
 * SteelHCF-master / dev.lbuddyboy.samurai.map.offline
 */

@Getter
public class OfflineHandler {

	private final Map<UUID, ItemStack[]> deathsClaim;
	private final Map<UUID, OfflineInventory> offlineInventories;
	private final Map<UUID, Inventory> editMap;
	private final MongoCollection<Document> claimCollection, offlineCollection;

	public OfflineHandler() {
		deathsClaim = new ConcurrentHashMap<>();
		offlineInventories = new ConcurrentHashMap<>();
		editMap = new HashMap<>();
		claimCollection = Samurai.getInstance().getMongoPool().getDatabase(Samurai.getMONGO_DB_NAME()).getCollection("DeathClaims");
		offlineCollection = Samurai.getInstance().getMongoPool().getDatabase(Samurai.getMONGO_DB_NAME()).getCollection("Offline");
		/*Bukkit.getPluginManager().registerEvents(new OfflineInventoryListener(), Samurai.getInstance());*/
		Bukkit.getPluginManager().registerEvents(new OfflineListener(), Samurai.getInstance());

/*		for (Document document : offlineCollection.find()) {
			OfflineInventory inventory = new OfflineInventory(document);

			this.offlineInventories.put(inventory.getUuid(), inventory);
		}*/
	}

	public void addReviveContents(UUID target, ItemStack[] contents) {
		if (deathsClaim.containsKey(target)) {
			List<ItemStack> contentList = new ArrayList<>(Arrays.asList(contents));
			contentList.addAll(Arrays.asList(this.deathsClaim.get(target)));

			this.deathsClaim.put(target, contentList.toArray(new ItemStack[0]));
			return;
		}

		contents = filterAir(contents).toArray(new ItemStack[0]);
		Document document = claimCollection.find(Filters.eq("uuid", target.toString())).first();

		if (document == null) {
			document = new Document();
		}

		document.put("uuid", target.toString());
		List<ItemStack> contentList = new ArrayList<>(Arrays.asList(contents));
		if (document.containsKey("contents")) {
			contentList.addAll(Arrays.asList(ItemUtils.itemStackArrayFromBase64(document.getString("contents"))));
		}
		document.put("contents", ItemUtils.itemStackArrayToBase64(contentList.toArray(new ItemStack[0])));

		claimCollection.replaceOne(Filters.eq("uuid", target.toString()), document, new ReplaceOptions().upsert(true));
	}

	public void removeReviveContents(UUID target, ItemStack[] contents) {
		if (deathsClaim.containsKey(target)) {
			List<ItemStack> contentList = new ArrayList<>(Arrays.asList(this.deathsClaim.get(target)));

			contentList.removeAll(Arrays.asList(contents));

			this.deathsClaim.put(target, contentList.toArray(new ItemStack[0]));
			return;
		}
		contents = filterAir(contents).toArray(new ItemStack[0]);
		Document document = claimCollection.find(Filters.eq("uuid", target.toString())).first();

		if (document == null) {
			document = new Document();
		}

		document.put("uuid", target.toString());
		List<ItemStack> contentList = new ArrayList<>();
		if (document.containsKey("contents")) {
			contentList.addAll(Arrays.asList(ItemUtils.itemStackArrayFromBase64(document.getString("contents"))));
		}
		contentList.removeAll(Arrays.asList(contents));
		document.put("contents", ItemUtils.itemStackArrayToBase64(contentList.toArray(new ItemStack[0])));

		claimCollection.replaceOne(Filters.eq("uuid", target.toString()), document, new ReplaceOptions().upsert(true));
	}

	public void setReviveContents(UUID target, ItemStack[] contents) {
		if (deathsClaim.containsKey(target)) {
			this.deathsClaim.put(target, contents);
			return;
		}
		contents = filterAir(contents).toArray(new ItemStack[0]);
		Document document = claimCollection.find(Filters.eq("uuid", target.toString())).first();

		if (document == null) {
			document = new Document();
		}

		document.put("uuid", target.toString());
		document.put("contents", ItemUtils.itemStackArrayToBase64(contents));

		claimCollection.replaceOne(Filters.eq("uuid", target.toString()), document, new ReplaceOptions().upsert(true));
	}

	public ItemStack[] getReviveContents(UUID target) {
		Document document = claimCollection.find(Filters.eq("uuid", target.toString())).first();

		if (document == null) {
			return new ItemStack[0];
		}

		return ItemStackSerializer.itemStackArrayFromBase64(document.getString("contents"));
	}

	public void playerQuit(Player player) {
		OfflineInventory offlineInventory = new OfflineInventory(player.getUniqueId(), player.getInventory());

		this.offlineInventories.put(player.getUniqueId(), offlineInventory);

		Bukkit.getScheduler().runTaskAsynchronously(Samurai.getInstance(), () -> {
			saveOffline(offlineInventory);
		});
	}

	public void playerQuitEmpty(Player player) {
		OfflineInventory offlineInventory = new OfflineInventory(player.getUniqueId(), null);

		this.offlineInventories.put(player.getUniqueId(), offlineInventory);

		Bukkit.getScheduler().runTaskAsynchronously(Samurai.getInstance(), () -> {
			saveOffline(offlineInventory);
		});
	}

	public void saveOffline(OfflineInventory inventory) {
		offlineCollection.replaceOne(Filters.eq("uuid", inventory.getUuid().toString()), inventory.toDocument(), new ReplaceOptions().upsert(true));
	}

	public void playerJoin(Player player) {
		if (!this.offlineInventories.containsKey(player.getUniqueId())) return;

		OfflineInventory offlineInventory = this.offlineInventories.get(player.getUniqueId());

		player.getInventory().setArmorContents(offlineInventory.getArmor());
		player.getInventory().setStorageContents(offlineInventory.getContents());
		player.getInventory().setExtraContents(offlineInventory.getExtras());
		player.updateInventory();
	}

	public void delete() {

	}

	public void disable() {
		Bukkit.getOnlinePlayers().forEach(player -> {

			if (player.hasMetadata("gaming")) {
				Samurai.getInstance().getOfflineHandler().playerQuitEmpty(player);
				return;
			}

			if (Samurai.getInstance().getInDuelPredicate().test(player)) {
				Samurai.getInstance().getOfflineHandler().playerQuitEmpty(player);
				return;
			}

			if (Samurai.getInstance().getArenaHandler().isDeathbanned(player.getUniqueId())) {
				Samurai.getInstance().getOfflineHandler().playerQuitEmpty(player);
				return;
			}

			saveOffline(new OfflineInventory(player.getUniqueId(), player.getInventory()));
		});
	}

	public void load(UUID uuid) {
		deathsClaim.put(uuid, getReviveContents(uuid));
	}

	public List<ItemStack> filterAir(ItemStack[] contents) {
		List<ItemStack> stacks = new ArrayList<>(Arrays.asList(contents));

		for (ItemStack content : contents) {
			if (content == null || content.getType() == Material.AIR) {
				stacks.remove(content);
			}
		}

		return stacks;
	}

}
