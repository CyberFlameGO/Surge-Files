package dev.lbuddyboy.samurai.persist.maps;

import dev.lbuddyboy.samurai.chat.enums.ChatMode;
import dev.lbuddyboy.samurai.persist.PersistMap;
import dev.lbuddyboy.samurai.team.TeamFilter;

import java.util.UUID;

public class FilterModeMap extends PersistMap<TeamFilter> {

    public FilterModeMap() {
        super("TeamFilters", "TeamFilter");
    }

    @Override
    public String getRedisValue(TeamFilter chatMode) {
        return (chatMode.name());
    }

    @Override
    public TeamFilter getJavaObject(String str) {
        return (TeamFilter.valueOf(str));
    }

    @Override
    public Object getMongoValue(TeamFilter chatMode) {
        return (chatMode.name());
    }

    public TeamFilter getFilter(UUID check) {
        return (contains(check) ? getValue(check) : TeamFilter.DEFAULT);
    }

    public void setFilter(UUID update, TeamFilter chatMode) {
        updateValueAsync(update, chatMode);
    }

}