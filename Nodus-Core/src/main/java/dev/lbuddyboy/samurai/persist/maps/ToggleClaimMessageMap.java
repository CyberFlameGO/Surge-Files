package dev.lbuddyboy.samurai.persist.maps;

import dev.lbuddyboy.samurai.persist.PersistMap;

import java.util.UUID;

public class ToggleClaimMessageMap extends PersistMap<Boolean> {

    public ToggleClaimMessageMap() {
        super("ClaimMessageToggles", "ClaimMessageEnabled");
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

    public void setClaimMessagesEnabled(UUID update, boolean toggled) {
        updateValueAsync(update, toggled);
    }

    public boolean areClaimMessagesEnabled(UUID check) {
        return contains(check) ? getValue(check) : true;
    }
}
