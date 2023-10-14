package dev.lbuddyboy.samurai.economy.uuid;

import com.google.common.base.Preconditions;
import dev.lbuddyboy.flash.Flash;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.MojangUser;

import java.io.IOException;
import java.util.UUID;

public class FrozenUUIDCache {

    public static UUID uuid(String name) {
        return Flash.getInstance().getCacheHandler().getUserCache().getUUID(name.toLowerCase());
    }

    public static String name(UUID uuid) {
        return Flash.getInstance().getCacheHandler().getUserCache().getName(uuid);
    }

    public static void ensure(UUID uuid) {
        if (String.valueOf(name(uuid)).equals("null")) {
            try {
                MojangUser user = new MojangUser(uuid);
                update(user.getUuid(), user.getName());
            } catch (IOException e) {
                Samurai.getInstance().getLogger().warning(uuid + " didn't have a cached name.");
            }
        }
    }

    public static void update(UUID uuid, String name) {
        Flash.getInstance().getCacheHandler().getUserCache().update(uuid, name, true);
    }

}

