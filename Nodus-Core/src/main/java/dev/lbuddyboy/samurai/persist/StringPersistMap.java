package dev.lbuddyboy.samurai.persist;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.api.FoxtrotConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class StringPersistMap<T> {

    protected Map<String, T> wrappedMap = new ConcurrentHashMap<>();

    private String keyPrefix;
    private String mongoName;

    public StringPersistMap(String keyPrefix, String mongoName) {
        this.keyPrefix = keyPrefix;
        this.mongoName = mongoName;

        loadFromRedis();
    }

    public void loadFromRedis() {

            Samurai.getInstance().runRedisCommand(redis -> {
                Map<String, String> results = redis.hgetAll(Samurai.getMONGO_DB_NAME() + ".data." + keyPrefix);

                for (Map.Entry<String, String> resultEntry : results.entrySet()) {
                    T object = getJavaObjectSafe(resultEntry.getKey(), resultEntry.getValue());

                    if (object != null) {
                        wrappedMap.put(resultEntry.getKey(), object);
                    }
                }

                return (null);
            });
    }

    protected void wipeValues() {
        wrappedMap.clear();

        Samurai.getInstance().runRedisCommand(redis -> {
            redis.del(Samurai.getMONGO_DB_NAME() + ":data:" + keyPrefix);
            return (null);
        });
    }

    protected void updateValueSync(final String key, final T value) {
        wrappedMap.put(key, value);

        Samurai.getInstance().runRedisCommand(redis -> {
            redis.hset(Samurai.getMONGO_DB_NAME() + ":data:" + keyPrefix, key, getRedisValue(getValue(key)));

            DBCollection playersCollection = Samurai.getInstance().getMongoPool().getDB(Samurai.MONGO_DB_NAME).getCollection("Players");
            BasicDBObject player = new BasicDBObject("_id", key.replace("-", ""));

            playersCollection.update(player, new BasicDBObject("$set", new BasicDBObject(mongoName, getMongoValue(getValue(key)))), true, false);
            return (null);
        });
    }

    protected void updateValueAsync(final String key, T value) {
        wrappedMap.put(key, value);

        new BukkitRunnable() {

            @Override
            public void run() {
                Samurai.getInstance().runRedisCommand(redis -> {
                    redis.hset(Samurai.getMONGO_DB_NAME() + ":data:" + keyPrefix, key, getRedisValue(getValue(key)));

                    DBCollection playersCollection = Samurai.getInstance().getMongoPool().getDB(Samurai.MONGO_DB_NAME).getCollection("Players");
                    BasicDBObject player = new BasicDBObject("_id", key.replace("-", ""));

                    playersCollection.update(player, new BasicDBObject("$set", new BasicDBObject(mongoName, getMongoValue(getValue(key)))), true, false);
                    return (null);
                });
            }

        }.runTaskAsynchronously(Samurai.getInstance());
    }

    protected T getValue(String key) {
        return (wrappedMap.get(key));
    }

    protected boolean contains(String key) {
        return (wrappedMap.containsKey(key));
    }

    public abstract String getRedisValue(T t);

    public abstract Object getMongoValue(T t);

    public T getJavaObjectSafe(String key, String redisValue) {
        try {
            return (getJavaObject(redisValue));
        } catch (Exception e) {
            System.out.println("Error parsing Redis result.");
            System.out.println(" - Prefix: " + Samurai.getMONGO_DB_NAME() + ":data:" + keyPrefix);
            System.out.println(" - Key: " + key);
            System.out.println(" - Value: " + redisValue);
            return (null);
        }
    }

    public abstract T getJavaObject(String str);

}