package dev.lbuddyboy.samurai.persist.maps.statistics;

import dev.lbuddyboy.samurai.persist.PersistMap;

import java.util.UUID;

public class BaseToggleMap extends PersistMap<Boolean> {

    public BaseToggleMap(String statistic) {
        super(statistic, "BoolStatistics." + statistic);
    }

    @Override
    public String getRedisValue(Boolean statistic) {
        return (String.valueOf(statistic));
    }

    @Override
    public Boolean getJavaObject(String str) {
        return (Boolean.parseBoolean(str));
    }

    @Override
    public Object getMongoValue(Boolean statistic) {
        return (statistic);
    }

    public Boolean isActive(UUID check) {
        return (contains(check) ? getValue(check) : false);
    }

    public void setActive(UUID update, Boolean time) {
        updateValueAsync(update, time);
    }

}