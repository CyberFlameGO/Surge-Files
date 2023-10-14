package dev.lbuddyboy.samurai.persist.maps.statistics;

import dev.lbuddyboy.samurai.persist.PersistMap;

import java.util.UUID;

public class BaseTimeMap extends PersistMap<Long> {

    public BaseTimeMap(String statistic) {
        super(statistic, "TimeStatistics." + statistic);
    }

    @Override
    public String getRedisValue(Long statistic) {
        return (String.valueOf(statistic));
    }

    @Override
    public Long getJavaObject(String str) {
        return (Long.parseLong(str));
    }

    @Override
    public Object getMongoValue(Long statistic) {
        return (statistic);
    }

    public Long getStatistic(UUID check) {
        return (contains(check) ? getValue(check) : 0);
    }

    public void setTime(UUID update, Long time) {
        updateValueAsync(update, time);
    }

}