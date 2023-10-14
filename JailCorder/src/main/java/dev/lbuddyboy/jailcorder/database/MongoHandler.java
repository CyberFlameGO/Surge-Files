package dev.lbuddyboy.jailcorder.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.lbuddyboy.jailcorder.JailCorder;
import dev.lbuddyboy.jailcorder.record.Recording;
import dev.lbuddyboy.jailcorder.util.CC;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class MongoHandler {

	public static JsonParser PARSER;

	MongoClient mongoClient;
	MongoClientURI mongoClientURI;

	private final MongoCollection<Document> userCollection, cacheCollection;
	private final MongoDatabase mongoDatabase;
	private final Gson GSON;
	private final Type STRING_TYPE, RECORDINGS_TYPE;
	private final Map<String, UUID> cache;

	public MongoHandler() {
		FileConfiguration config = JailCorder.getInstance().getConfig();

		if (config.getBoolean("mongo.auth.enabled")) {
			MongoCredential credential = MongoCredential.createCredential(
					config.getString("mongo.auth.username"),
					config.getString("mongo.auth.auth-db"),
					config.getString("mongo.auth.password").toCharArray()
			);

			mongoClient = new MongoClient(new ServerAddress(config.getString("mongo.host"),
					config.getInt("mongo.port")), credential, MongoClientOptions.builder().build());
		} else {
			mongoClient = new MongoClient(config.getString("mongo.host"),
					config.getInt("mongo.port"));
		}

		mongoDatabase = mongoClient.getDatabase(config.getString("mongo.database"));

		Bukkit.getConsoleSender().sendMessage(CC.translate("&fSuccessfully connected the &6Mongo Database"));

		this.userCollection = getMongoDatabase().getCollection("Recordings");
		this.cacheCollection = getMongoDatabase().getCollection("Cache");
		this.GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().enableComplexMapKeySerialization().create();
		this.STRING_TYPE = new TypeToken<List<String>>() {}.getType();
		this.RECORDINGS_TYPE = new TypeToken<List<Recording>>() {}.getType();
		this.cache = new ConcurrentHashMap<>();

		for (Document document : this.cacheCollection.find()) {
			this.cache.put(document.getString("name").toLowerCase(), UUID.fromString(document.getString("uuid")));
		}
	}

	static {
		PARSER = new JsonParser();
	}

}
