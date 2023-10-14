package dev.lbuddyboy.samurai.persist.maps;

import dev.lbuddyboy.samurai.persist.PersistMap;

import java.util.UUID;

public class ToggleClaimDisplayMap extends PersistMap<Boolean> {

    public ToggleClaimDisplayMap() {
        super("ClaimDisplayToggles", "ClaimDisplayEnabled");
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

    public void setClaimDisplayEnabled(UUID update, boolean toggled) {
        updateValueAsync(update, toggled);
    }

    public boolean isClaimDisplayEnabled(UUID check) {
        return (contains(check) ? getValue(check) : true);
    }

}
