package dev.aurapvp.samurai.storage.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.essential.locator.ItemLocation;
import dev.aurapvp.samurai.essential.offline.OfflineData;
import dev.aurapvp.samurai.essential.rollback.PlayerDeath;
import dev.aurapvp.samurai.faction.Faction;
import dev.aurapvp.samurai.player.SamuraiPlayer;
import dev.aurapvp.samurai.storage.IStorage;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.gson.GSONUtils;
import dev.aurapvp.samurai.util.Tasks;
import lombok.Getter;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public class MongoStorage implements IStorage {

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private JsonParser parser;

    private MongoCollection<Document> playerDataCollection, samuraiPlayerCollection, factionCollection, offlineDataCollection, itemLocatorCollection;

    @Override
    public String getId() {
        return "MONGO";
    }

    @Override
    public MongoStorage initialize() {
        FileConfiguration config = Samurai.getInstance().getConfig();

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

        parser = new JsonParser();
        mongoDatabase = mongoClient.getDatabase(config.getString("mongo.database"));
        playerDataCollection = mongoDatabase.getCollection("PlayerData");
        samuraiPlayerCollection = mongoDatabase.getCollection("SamuraiPlayers");
        offlineDataCollection = mongoDatabase.getCollection("OfflineData");
        itemLocatorCollection = mongoDatabase.getCollection("ItemLocations");
        factionCollection = mongoDatabase.getCollection("Factions");

        Bukkit.getConsoleSender().sendMessage(CC.translate("&fStorage Type: &6MONGO"));

        return this;
    }

    @Override
    public Map<UUID, Faction> loadFactions(Map<UUID, Faction> map, String key, boolean async) {
        if (async) {
            Tasks.runAsync(() -> {
                for (Document document : this.factionCollection.find()) {
                    if (!document.containsKey(key)) continue;
                    map.put(UUID.fromString(document.getString("uuid")), GSONUtils.getGSON().fromJson(document.getString(key), GSONUtils.FACTION));
                }
            });
        } else {
            for (Document document : this.factionCollection.find()) {
                if (!document.containsKey(key)) continue;
                map.put(UUID.fromString(document.getString("uuid")), GSONUtils.getGSON().fromJson(document.getString(key), GSONUtils.FACTION));
            }
        }
        System.out.println("[Faction Handler] Loaded " + map.size() + " factions successfully!");
        return map;
    }

    @Override
    public void insertFaction(UUID uuid, String key, Object data, boolean async) {
        if (async) {
            Tasks.runAsync(() -> insertFaction(uuid, key, data, false));
            return;
        }
        Bson query = Filters.eq("uuid", uuid.toString());
        Document document = this.factionCollection.find(query).first();
        boolean insert = false;

        if (document == null) {
            document = new Document();
            insert = true;
        }

        document.put("uuid", uuid.toString());
        document.put(key, GSONUtils.getGSON().toJson(data, GSONUtils.FACTION));

        if (insert) {
            this.factionCollection.insertOne(document);
            return;
        }

        this.factionCollection.replaceOne(query, document, new ReplaceOptions().upsert(true));
    }

    @Override
    public void wipeFaction(UUID uuid, boolean async) {
        if (async) {
            Tasks.runAsync(() -> wipeData(uuid, false));
            return;
        }
        Bson query = Filters.eq("uuid", uuid.toString());
        Document document = this.factionCollection.find(query).first();

        if (document == null) return;

        this.factionCollection.deleteOne(query);
    }

    @Override
    public ItemLocation loadLocation(UUID id, boolean async) {
        Bson query = Filters.eq("id", id.toString());
        Document document = this.itemLocatorCollection.find(query).first();

        if (document == null || !document.containsKey("location")) {
            return null;
        }

        return ItemLocation.deserialize(parser.parse(document.getString("location")).getAsJsonObject());
    }

    @Override
    public void updateLocation(UUID id, ItemLocation location, boolean async) {
        if (location == null) return;

        if (async) {
            Tasks.runAsync(() -> updateLocation(id, location, false));
            return;
        }

        Bson query = Filters.eq("id", id.toString());
        Document document = this.itemLocatorCollection.find(query).first();
        boolean insert = false;

        if (document == null) {
            document = new Document();
            insert = true;
        }

        document.put("id", id.toString());
        document.put("location", location.serialize().toString());

        if (insert) {
            this.itemLocatorCollection.insertOne(document);
            return;
        }

        this.itemLocatorCollection.replaceOne(query, document, new ReplaceOptions().upsert(true));
    }

    @Override
    public void wipeLocation(UUID id, boolean async) {
        if (async) {
            Tasks.runAsync(() -> wipeLocation(id, false));
            return;
        }
        Bson query = Filters.eq("id", id.toString());
        Document document = this.itemLocatorCollection.find(query).first();

        if (document == null) return;

        this.itemLocatorCollection.deleteOne(query);
    }

    @Override
    public SamuraiPlayer loadPlayer(UUID id, boolean async) {
        Bson query = Filters.eq("uniqueId", id.toString());
        Document document = this.samuraiPlayerCollection.find(query).first();

        if (document == null) {
            return null;
        }

        return SamuraiPlayer.deserialize(document);
    }

    @Override
    public void updatePlayer(UUID id, SamuraiPlayer player, boolean async) {
        if (player == null) return;

        if (async) {
            Tasks.runAsync(() -> updatePlayer(id, player, false));
            return;
        }

        Bson query = Filters.eq("uniqueId", id.toString());
        Document document = this.samuraiPlayerCollection.find(query).first();
        boolean insert = false;

        if (document == null) {
            document = new Document();
            insert = true;
        }

        document.putAll(player.serialize());

        if (insert) {
            this.samuraiPlayerCollection.insertOne(document);
            return;
        }

        this.samuraiPlayerCollection.replaceOne(query, document, new ReplaceOptions().upsert(true));
    }

    @Override
    public void deletePlayer(UUID id, boolean async) {

    }

    @Override
    public void loadData(Map<UUID, ?> map, String key, Type type, boolean async) {
        if (async) {
            Tasks.runAsync(() -> loadData(map, key, type, false));
            return;
        }
        for (Document document : this.playerDataCollection.find()) {
            if (!document.containsKey(key)) continue;
            map.put(UUID.fromString(document.getString("uuid")), GSONUtils.getGSON().fromJson(document.getString(key), type));
        }
    }

    @Override
    public Object loadData(UUID uuid, String key, Type type, boolean async) {
        if (async) {
            Tasks.runAsync(() -> loadData(uuid, key, type, false));
            return null;
        }

        Bson query = Filters.eq("uuid", uuid.toString());
        Document document = this.playerDataCollection.find(query).first();

        if (document == null) {
            return null;
        }

        return GSONUtils.getGSON().fromJson(document.getString(key), type);
    }

    @Override
    public void insertData(UUID uuid, String key, Object data, Type type, boolean async) {
        insertData(uuid, new String[]{key}, new Object[]{data}, new Type[]{type}, async);
    }

    @Override
    public void insertData(UUID uuid, String[] key, Object[] data, Type[] types, boolean async) {
        if (async) {
            Tasks.runAsync(() -> insertData(uuid, key, data, types, false));
            return;
        }
        Bson query = Filters.eq("uuid", uuid.toString());
        Document document = this.playerDataCollection.find(query).first();
        boolean insert = false;
        int i = 0;

        if (document == null) {
            document = new Document();
            insert = true;
        }

        document.put("uuid", uuid.toString());
        for (String s : key) {
            document.put(s, GSONUtils.getGSON().toJson(data[i], types[i++]));
        }

        if (insert) {
            this.playerDataCollection.insertOne(document);
            return;
        }

        this.playerDataCollection.replaceOne(query, document, new ReplaceOptions().upsert(true));
    }

    @Override
    public void wipeData(UUID uuid, boolean async) {
        if (async) {
            Tasks.runAsync(() -> wipeData(uuid, false));
            return;
        }
        Bson query = Filters.eq("uuid", uuid.toString());
        Document document = this.playerDataCollection.find(query).first();

        if (document == null) return;

        this.playerDataCollection.deleteOne(query);
    }
}
