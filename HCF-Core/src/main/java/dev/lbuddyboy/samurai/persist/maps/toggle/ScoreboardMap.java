package dev.lbuddyboy.samurai.persist.maps.toggle;

import dev.lbuddyboy.samurai.persist.PersistMap;

import java.util.UUID;

public class ScoreboardMap extends PersistMap<Boolean> {

    public ScoreboardMap() {
        super("Scoreboard", "Scoreboard");
    }

    @Override
    public String getRedisValue(Boolean toggled) {
        return String.valueOf(toggled);
    }

    @Override
    public Object getMongoValue(Boolean toggled) {
        return String.valueOf(toggled);
    }

    @Override
    public Boolean getJavaObject(String str) {
        return Boolean.valueOf(str);
    }

    public void setToggled(UUID update, boolean toggled) {
        updateValueAsync(update, toggled);
    }

    public boolean isToggled(UUID check) {
        return (contains(check) ? getValue(check) : true);
    }

}
