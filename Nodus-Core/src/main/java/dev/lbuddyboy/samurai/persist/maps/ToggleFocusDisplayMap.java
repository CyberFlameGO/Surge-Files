package dev.lbuddyboy.samurai.persist.maps;

import dev.lbuddyboy.samurai.persist.PersistMap;

import java.util.UUID;

public class ToggleFocusDisplayMap extends PersistMap<Boolean> {

    public ToggleFocusDisplayMap() {
        super("FocusDisplayToggles", "FocusDisplayEnabled");
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

    public void setFocusDisplayEnabled(UUID update, boolean toggled) {
        updateValueAsync(update, toggled);
    }

    public boolean isFocusDisplayEnabled(UUID check) {
        return contains(check) ? getValue(check) : true;
    }
}
