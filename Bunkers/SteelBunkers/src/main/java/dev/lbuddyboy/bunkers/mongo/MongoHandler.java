package dev.lbuddyboy.bunkers.mongo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.*;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import dev.lbuddyboy.bunkers.Bunkers;
import dev.lbuddyboy.bunkers.util.bukkit.CC;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 07/09/2021 / 4:39 PM
 * lCore / me.lbuddyboy.core.database
 */

@Getter
public class MongoHandler {

	private MongoClient mongoClient;
	private com.mongodb.client.MongoClient mongoClientURI;
	private MongoDatabase mongoDatabase;
	private final Gson GSON = new Gson();

	private final Type LIST_STRING_TYPE = new TypeToken<List<String>>() {}.getType();
	private final Type LIST_UUID_TYPE = new TypeToken<List<UUID>>() {}.getType();

	public MongoHandler() {
//		 if (!InventoryCache.init()) return;

		FileConfiguration config = Bunkers.getInstance().getConfig();

		if (config.getBoolean("mongo.use-uri")) {

			String username = config.getString("mongo.auth.username");
			String password = config.getString("mongo.auth.password");

			String database = config.getString("mongo.database");
			String host = config.getString("mongo.host");
			int port = config.getInt("mongo.port");

			ConnectionString connectionString = new ConnectionString("mongodb://" + username + ":" + password + "@" + host + ":" + port + "/" + database + "?authSource=admin");
			MongoClientSettings settings = MongoClientSettings.builder()
					.applyConnectionString(connectionString)
					.build();

			this.mongoClientURI = MongoClients.create(settings);
			this.mongoDatabase = mongoClientURI.getDatabase(database);
			return;
		}

		boolean auth = config.getBoolean("mongo.auth.enabled");
		String username = config.getString("mongo.auth.username");
		String password = config.getString("mongo.auth.password");

		String database = config.getString("mongo.database");
		String host = config.getString("mongo.host");
		int port = config.getInt("mongo.port");

		if (!auth) {
			mongoClient = new MongoClient(host, port);
		} else {
			mongoClient = new MongoClient(new ServerAddress(host, port), MongoCredential.createCredential(username, database, password.toCharArray()),
					MongoClientOptions.builder().build());

		}
		mongoDatabase = this.mongoClient.getDatabase(database);
		Bukkit.getConsoleSender().sendMessage(CC.translate("&fSuccessfully connected the &6Mongo Database"));
	}

}
