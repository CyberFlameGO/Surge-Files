package dev.lbuddyboy.samurai.custom.feature;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 19/02/2022 / 1:30 AM
 * SteelHCF-master / dev.lbuddyboy.samurai.custom.feature
 */

@Getter
public class FeatureHandler {

	private final Map<Feature, Boolean> disabledFeatures;
	private final MongoCollection<Document> collection;

	public FeatureHandler() {
		this.disabledFeatures = new HashMap<>();
		this.collection = Samurai.getInstance().getMongoPool().getDatabase(Samurai.MONGO_DB_NAME).getCollection("DisabledFeatures");

		loadAll();
	}

	public void save() {

		for (Map.Entry<Feature, Boolean> entry : this.disabledFeatures.entrySet()) {

			Document document = new Document();

			document.put("_id", entry.getKey().name());
			document.put(entry.getKey().name(), entry.getValue());

			this.collection.replaceOne(Filters.eq(entry.getKey().name()), document, new ReplaceOptions().upsert(true));

		}

	}

	public void loadAll() {

		for (Document document : this.collection.find()) {
			if (document == null) continue;
			for (String key : document.keySet()) {
				if (key.contains("_id")) continue;
				try {
					this.disabledFeatures.put(Feature.valueOf(key), document.getBoolean(key));
				} catch (Exception e) {
					System.out.println("Error with feature names...");
				}
			}
		}

	}

	public boolean isDisabled(Feature feature) {
		return this.disabledFeatures.getOrDefault(feature, false);
	}

}
