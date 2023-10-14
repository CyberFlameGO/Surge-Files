package dev.lbuddyboy.communicate.profile;

import com.mongodb.client.MongoCollection;
import dev.lbuddyboy.communicate.BunkersCom;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 23/09/2021 / 2:31 AM
 * LBuddyBoy Development / me.lbuddyboy.core.profile
 */

@Getter
public class ProfileHandler {

	private Map<UUID, Profile> profiles;
	private MongoCollection<Document> collection;

	public ProfileHandler() {
		this.profiles = new ConcurrentHashMap<>();
		this.collection = BunkersCom.getInstance().getMongoHandler().getMongoDatabase().getCollection("profiles");

		Bukkit.getPluginManager().registerEvents(new ProfileListener(), BunkersCom.getInstance());

	}

	public Profile getByUUID(UUID toLook) {
		if (profiles.get(toLook) != null) {
			return profiles.get(toLook);
		}

		Profile profile = new Profile(toLook);
		if (!profile.isLoaded()) {
			return null;
		}

		return profile;
	}

}
