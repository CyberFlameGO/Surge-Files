package dev.lbuddyboy.samurai.custom.daily;

import dev.lbuddyboy.flash.util.JavaUtils;
import dev.lbuddyboy.samurai.persist.PersistMap;
import dev.lbuddyboy.samurai.persist.maps.statistics.BaseTimeMap;
import dev.lbuddyboy.samurai.persist.maps.statistics.BaseToggleMap;

import java.util.UUID;

public class DailyRewardsMap extends BaseTimeMap {

    public static long TWENTY_FOUR_HOURS = JavaUtils.parse("24h");

    public DailyRewardsMap() {
        super("DailyRewards");
    }

    public long getRemaining(UUID uuid) {
        return contains(uuid) ? getValue(uuid) + TWENTY_FOUR_HOURS - System.currentTimeMillis() : 0;
    }

    public boolean isAvailable(UUID uuid) {
        return getRemaining(uuid) <= 0;
    }

}
