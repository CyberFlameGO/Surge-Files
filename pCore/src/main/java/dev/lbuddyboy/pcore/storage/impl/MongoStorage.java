package dev.lbuddyboy.pcore.storage.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lbuddyboy.pcore.mines.PrivateMine;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.essential.locator.ItemLocation;
import dev.lbuddyboy.pcore.essential.offline.OfflineData;
import dev.lbuddyboy.pcore.essential.rollback.PlayerDeath;
import dev.lbuddyboy.pcore.robots.pRobot;
import dev.lbuddyboy.pcore.storage.IStorage;
import dev.lbuddyboy.pcore.user.MineUser;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.ItemUtils;
import dev.lbuddyboy.pcore.util.gson.GSONUtils;
import dev.lbuddyboy.pcore.util.Tasks;
import lombok.Getter;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Getter
public class MongoStorage implements IStorage {

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private JsonParser parser;

    private MongoCollection<Document> playerDataCollection, robotCollection, factionCollection, mineUserCollection, offlineDataCollection, enderChestCollection, plotCollection, privateMineCollection, itemLocatorCollection;

    @Override
    public String getId() {
        return "MONGO";
    }

    @Override
    public MongoStorage initialize() {
        FileConfiguration config = pCore.getInstance().getConfig();

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
        mineUserCollection = mongoDatabase.getCollection("MineUsers");
        playerDataCollection = mongoDatabase.getCollection("PlayerData");
        robotCollection = mongoDatabase.getCollection("Robots");
        offlineDataCollection = mongoDatabase.getCollection("OfflineData");
        enderChestCollection = mongoDatabase.getCollection("EnderChest");
        plotCollection = mongoDatabase.getCollection("Plot");
        itemLocatorCollection = mongoDatabase.getCollection("ItemLocations");
        factionCollection = mongoDatabase.getCollection("Factions");
        privateMineCollection = mongoDatabase.getCollection("PrivateMines");

        Bukkit.getConsoleSender().sendMessage(CC.translate("&fStorage Type: &6MONGO"));

        return this;
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
    public MineUser loadMineUser(UUID uuid, boolean async) {
        Bson query = Filters.eq("uuid", uuid.toString());
        Document document = this.mineUserCollection.find(query).first();

        if (document == null || !document.containsKey("user")) {
            return null;
        }
        MineUser user = MineUser.deserialize(parser.parse(document.getString("user")).getAsJsonObject());

        pCore.getInstance().getMineUserHandler().getUsers().put(user.getUuid(), user);

        return user;
    }

    @Override
    public void saveMineUser(UUID uuid, MineUser user, boolean async) {
        if (async) {
            Tasks.runAsync(() -> saveMineUser(uuid, user, false));
            return;
        }

        Bson query = Filters.eq("uuid", uuid.toString());
        Document document = this.mineUserCollection.find(query).first();
        boolean insert = false;

        if (document == null) {
            document = new Document();
            insert = true;
        }

        document.put("uuid", uuid.toString());
        document.put("user", user.serialize().toString());

        if (insert) {
            this.mineUserCollection.insertOne(document);
            return;
        }

        this.mineUserCollection.replaceOne(query, document, new ReplaceOptions().upsert(true));
    }

    @Override
    public void wipeMineUser(UUID uuid, boolean async) {
        if (async) {
            Tasks.runAsync(() -> wipeMineUser(uuid, false));
            return;
        }
        Bson query = Filters.eq("uuid", uuid.toString());
        Document document = this.mineUserCollection.find(query).first();

        if (document == null) return;

        this.mineUserCollection.deleteOne(query);
    }

    @Override
    public List<pRobot> loadRobots(boolean async) {
        List<pRobot> robots = new ArrayList<>();

        for (Document document : robotCollection.find()) {
            robots.add(pRobot.deserialize(parser.parse(document.getString("robot")).getAsJsonObject()));
        }

        return robots;
    }

    @Override
    public void insertRobots(List<pRobot> robots, boolean async) {
        if (!async) {
            for (pRobot robot : robots) {
                Document document = new Document();

                document.put("id", robot.getId().toString());
                document.put("owner", robot.getOwner().toString());
                document.put("robot", robot.serialize().toString());

                this.robotCollection.replaceOne(Filters.eq("id", robot.getId()), document, new ReplaceOptions().upsert(true));
                robot.setUpdated(false);
            }
            return;
        }

        Tasks.runAsync(() -> {
            insertRobots(robots, false);
        });
    }

    @Override
    public void removeRobots(List<pRobot> robots, boolean async) {
        for (pRobot robot : robots) {
            this.robotCollection.deleteOne(Filters.eq("id", robot.getId().toString()));
        }
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
    public Map<UUID, ?> loadData(String key, Type type, boolean async) {
        Map<UUID, ?> map = new HashMap<>();
        if (async) {
            return loadData(key, type, false);
        }
        for (Document document : this.playerDataCollection.find()) {
            if (!document.containsKey(key)) continue;
            map.put(UUID.fromString(document.getString("uuid")), GSONUtils.getGSON().fromJson(document.getString(key), type));
        }

        return map;
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
