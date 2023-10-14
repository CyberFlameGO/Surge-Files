package dev.lbuddyboy.samurai.persist.maps;

import dev.lbuddyboy.samurai.map.shards.ShardHandler;
import dev.lbuddyboy.samurai.persist.PersistMap;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ShardMap extends PersistMap<Long> {

    public ShardMap() {
        super("Shards", "shards");
    }

    public long addShards(UUID uuid, long amount, boolean bypass) {
        if (!bypass) {
            amount = ShardHandler.isDoubleGem() ? amount * 2 : amount;
        }
        Long value = getValue(uuid);
        if (value == null)
            value = amount;
        else
            value += amount;

        updateValueAsync(uuid, value);
        return amount;
    }

    public void addShardsSync(UUID uuid, long amount) {
        Long value = getValue(uuid);
        if (value == null)
            value = amount;
        else
            value += amount;

        updateValueSync(uuid, value);
    }

    public long addShards(UUID uuid, long amount) {
        return addShards(uuid, amount, false);
    }

    public boolean removeShards(UUID uuid, long amount) {
        Long value = getValue(uuid);
        if (value == null)
            return false;
        else
            value -= amount;

        boolean update = value >= 0;
        if (update)
            updateValueAsync(uuid, value);

        return update;
    }

    public long getShards(Player player) {
        return wrappedMap.getOrDefault(player.getUniqueId(), 0L);
    }

    public long getShards(UUID uuid) {
        return wrappedMap.getOrDefault(uuid, 0L);
    }

    public void setValue(Player player, long value) {
        updateValueSync(player.getUniqueId(), value);
    }

    public void setValue(UUID uuid, long value) {
        updateValueAsync(uuid, value);
    }

    @Override
    public String getRedisValue(Long shards) {
        return String.valueOf(shards);
    }

    @Override
    public Object getMongoValue(Long shards) {
        return shards.intValue();
    }

    @Override
    public Long getJavaObject(String str) {
        return Long.parseLong(str);
    }
}
