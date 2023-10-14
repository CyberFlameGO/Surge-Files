package dev.lbuddyboy.samurai.persist.maps;

import dev.lbuddyboy.samurai.persist.PersistMap;

import java.util.UUID;

public class ToggleDeathMessageMap extends PersistMap<Boolean> {

    public ToggleDeathMessageMap() {
        super("DeathMessageToggles", "DeathMessageEnabled");
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

    public void setDeathMessagesEnabled(UUID update, boolean toggled) {
        updateValueAsync(update, toggled);
    }

    public boolean areDeathMessagesEnabled(UUID check) {
        return (contains(check) ? getValue(check) : true);
    }

}
