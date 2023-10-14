package dev.lbuddyboy.samurai.persist.maps;

import dev.lbuddyboy.samurai.persist.PersistMap;

import java.util.UUID;

public class ToggleGlobalChatMap extends PersistMap<Boolean> {

    public ToggleGlobalChatMap() {
        super("GlobalChatToggles", "GlobalChat");
    }

    @Override
    public String getRedisValue(Boolean toggled){
        return (String.valueOf(toggled));
    }

    @Override
    public Boolean getJavaObject(String str){
        return (Boolean.valueOf(str));
    }

    @Override
    public Object getMongoValue(Boolean toggled) {
        return (toggled);
    }

    public void setGlobalChatToggled(UUID update, boolean toggled) {
        updateValueAsync(update, toggled);
    }

    public boolean isGlobalChatToggled(UUID check) {
        return (contains(check) ? getValue(check) : true);
    }

}