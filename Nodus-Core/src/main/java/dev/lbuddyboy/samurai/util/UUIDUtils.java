package dev.lbuddyboy.samurai.util;

import com.mongodb.BasicDBList;
import dev.lbuddyboy.samurai.economy.uuid.FrozenUUIDCache;

import java.util.Collection;
import java.util.UUID;

public final class UUIDUtils {
    private UUIDUtils() {
    }

    public static String name(UUID uuid) {
        String name= FrozenUUIDCache.name(uuid);
        return name == null ? "null" : name;
    }

    public static UUID uuid(String name) {
        return FrozenUUIDCache.uuid(name);
    }

    public static String formatPretty(UUID uuid) {
        return UUIDUtils.name(uuid) + " [" + uuid + "]";
    }

    public static BasicDBList uuidsToStrings(Collection<UUID> toConvert) {
        if (toConvert == null || toConvert.isEmpty()) {
            return new BasicDBList();
        }
        BasicDBList dbList=new BasicDBList();
        for ( UUID uuid : toConvert ) {
            dbList.add(uuid.toString());
        }
        return dbList;
    }
}

