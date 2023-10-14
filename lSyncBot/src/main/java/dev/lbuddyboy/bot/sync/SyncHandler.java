package dev.lbuddyboy.bot.sync;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.lbuddyboy.bot.Bot;
import dev.lbuddyboy.bot.IHandler;
import dev.lbuddyboy.bot.sync.cache.SyncInformation;
import dev.lbuddyboy.bot.utils.gson.GSONUtils;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.UserSnowflake;
import org.bson.Document;
import org.simpleyaml.configuration.file.FileConfiguration;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class SyncHandler implements IHandler {

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> usersCollection, invitesCollection, ticketsCollection;

    private final Gson GSON = new Gson();
    private final Type LIST_LONG_TYPE = new TypeToken<List<Long>>() {}.getType();
    private final Type LIST_STRING_TYPE = new TypeToken<List<String>>() {}.getType();
    private List<SyncCode> syncCodes;

    public SyncHandler() {
        this.syncCodes = new ArrayList<>();
    }

    @Override
    public void load(Bot instance) {
        FileConfiguration config = instance.getConfig();

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

        System.out.println("Connected to the mongo database");

        mongoDatabase = mongoClient.getDatabase(config.getString("mongo.database"));
        usersCollection = mongoDatabase.getCollection("Users");
        invitesCollection = mongoDatabase.getCollection("DiscordInvites");
        ticketsCollection = mongoDatabase.getCollection("Tickets");

        try (Jedis jedis = Bot.getInstance().getRedisHandler().getJedisPool().getResource()) {
            for (Map.Entry<String, String> entry : jedis.hgetAll("SyncCodes").entrySet()) {
                SyncCode code = GSONUtils.getGSON().fromJson(entry.getValue(), GSONUtils.SYNC_CODE);

                this.syncCodes.add(code);
            }
        }

        System.out.println("[Sync Handler] Loaded " + syncCodes.size() + " sync codes.");
    }

    @Override
    public void unload(Bot instance) {

    }

    public boolean isSynced(long id) {
        try (Jedis jedis = Bot.getInstance().getRedisHandler().getJedisPool().getResource()) {
            return jedis.hget("Syncs", String.valueOf(id)) != null;
        }
    }

    public SyncInformation getSyncInformation(long id) {
        if (!isSynced(id)) return null;
        try (Jedis jedis = Bot.getInstance().getRedisHandler().getJedisPool().getResource()) {
            return GSONUtils.getGSON().fromJson(jedis.hget("Syncs", String.valueOf(id)), GSONUtils.SYNC_INFORMATION);
        }
    }

    public void setSynced(Guild guild, UserSnowflake user, SyncInformation information) {

        try (Jedis jedis = Bot.getInstance().getRedisHandler().getJedisPool().getResource()) {
            jedis.hset("Syncs", user.getId(), GSONUtils.getGSON().toJson(information, GSONUtils.SYNC_INFORMATION));
            jedis.hdel("SyncCodes", information.getPlayerUUID().toString());
        }

        try {
            guild.addRoleToMember(user, guild.getRolesByName("synced", true).get(0)).queue();
        } catch (Exception ignored) {
            System.out.println("Tried to add the 'Synced' role to " + user.getAsMention() + ", but it doesn't exist!");
        }
    }

    public void removeSynced(Guild guild, UserSnowflake user) {

        try (Jedis jedis = Bot.getInstance().getRedisHandler().getJedisPool().getResource()) {
            jedis.hdel("Syncs", user.getId());
        }

        try {
            guild.removeRoleFromMember(user, guild.getRolesByName("synced", true).get(0)).queue();
        } catch (Exception ignored) {
            System.out.println("Tried to add the 'Synced' role to " + user.getAsMention() + ", but it doesn't exist!");
        }
    }

    public SyncCode getSyncCodeByCode(int code) {
        for (SyncCode syncCode : this.syncCodes) {
            if (syncCode.getCode() == code) return syncCode;
        }
        return null;
    }

    public Map<Long, String> getRankConversion() {
        Map<Long, String> conversion = new HashMap<>();
        for (String key : Bot.getInstance().getConfig().getStringList("conversion")) {
            String[] args = key.split(":");
            conversion.put(Long.valueOf(args[0]), args[1]);
        }
        return conversion;
    }

}
