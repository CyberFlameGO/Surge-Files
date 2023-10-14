package dev.lbuddyboy.gkits.object;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lbuddyboy.gkits.lGKits;
import dev.lbuddyboy.gkits.object.kit.GKit;
import dev.lbuddyboy.gkits.object.kit.GKitInfo;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.util.*;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 09/07/2021 / 1:06 PM
 * GKits / me.lbuddyboy.gkits.object
 */

@Getter
@Setter
public class User {

	@Getter private static Map<UUID, User> users = new HashMap<>();

	private final UUID uuid;
	private List<GKitInfo> gKitInfo;
	private boolean needsSave;
	private transient long lastUpdated;

	public User(UUID uuid) {
		this.uuid = uuid;
		gKitInfo = new ArrayList<>();

		load();
	}

	public void load() {

		if (lGKits.getInstance().getConfig().getBoolean("MONGO.USE")) {
			Document document = lGKits.getInstance().getMongoDatabase().getCollection("users").find(Filters.eq("uuid", uuid.toString())).first();

			if (document == null) {
				save(true);
				document = lGKits.getInstance().getMongoDatabase().getCollection("users").find(Filters.eq("uuid", uuid.toString())).first();
				if (document == null) {
					return;
				}
			}

			for (Map.Entry<String, Object> entry : document.entrySet()) {
				String key = entry.getKey();
				if (lGKits.getInstance().getGKits().containsKey(key)) {
					long value = document.getLong(key);

					if (System.currentTimeMillis() < value) {
						this.gKitInfo.add(new GKitInfo(key, value));
					}
				}
			}
		} else {
			YamlConfiguration config = lGKits.getInstance().getUsersYML().gc();
			ConfigurationSection section = config.getConfigurationSection("users." + this.uuid);

			if (section == null) return;

			for (String key : section.getKeys(false)) {
				long value = config.getLong("users." + this.uuid + "." + key);
				if (lGKits.getInstance().getGKits().containsKey(key)) {
					if (System.currentTimeMillis() < value) {
						this.gKitInfo.add(new GKitInfo(key, value));
					}
				}
			}
		}
	}

	public void save(boolean async) {
		if (async) {
			Bukkit.getScheduler().runTaskAsynchronously(lGKits.getInstance(), () -> {
				save(false);
			});
			return;
		}
		if (lGKits.getInstance().getConfig().getBoolean("MONGO.USE")) {
			Document document = new Document();

			document.put("uuid", uuid.toString());

			for (GKitInfo info : this.gKitInfo) {
				document.put(info.getKit().getName(), info.getDuration());
			}

			lGKits.getInstance().getMongoDatabase().getCollection("users").replaceOne(Filters.eq("uuid", uuid.toString()), document, new ReplaceOptions().upsert(true));
		} else {
			YamlConfiguration config = lGKits.getInstance().getUsersYML().gc();
			config.set("users." + this.uuid, "");
			try {
				for (GKitInfo info : this.gKitInfo) {
					config.set("users." + this.uuid + "." + info.getKit().getName(), info.getDuration());
				}
			} catch (Exception ignored) {

			}
			try {
				lGKits.getInstance().getUsersYML().save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isExpired(GKitInfo info) {
		return info.getDuration() - System.currentTimeMillis() <= 0;
	}

	public GKitInfo infoByGkit(GKit gKit) {
		for (GKitInfo info : getGKitInfo()) {
			if (info.getKit().getName().equals(gKit.getName())) {
				return info;
			}
		}
		return null;
	}

	public boolean onCooldown(GKitInfo info) {
		return info.getDuration() - System.currentTimeMillis() > 0;
	}

	public void removeCooldown(GKitInfo info) {
		if (lGKits.getInstance().getConfig().getBoolean("MONGO.USE")) {
			Document document = lGKits.getInstance().getMongoDatabase().getCollection("users").find(Filters.eq("uuid", uuid.toString())).first();
			if (document != null) {
				document.put(info.getKit().getName(), 0);
			}
		} else {
			YamlConfiguration config = lGKits.getInstance().getUsersYML().gc();
			config.set("users." + this.uuid + "." + info.getKit().getName(), 0);
			try {
				lGKits.getInstance().getUsersYML().save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.gKitInfo.removeIf(gkit -> gkit.getKit().getName().equals(info.getKit().getName()));

		save(true);
	}
	public void addCooldown(GKitInfo info) {
		this.gKitInfo.removeIf(i -> i.getKit().equals(info.getKit()));
		this.gKitInfo.add(info);
		setNeedsSave(true);
	}

	public static User getByUuid(UUID uuid) {
		if (users.containsKey(uuid)) {
			return users.get(uuid);
		}

		User user = new User(uuid);
		users.put(uuid, user);
		return user;
	}

	public boolean shouldUpdate() {
		return this.lastUpdated + 5_000L < System.currentTimeMillis();
	}

}
