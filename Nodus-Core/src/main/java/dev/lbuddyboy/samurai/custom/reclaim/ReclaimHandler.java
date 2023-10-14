package dev.lbuddyboy.samurai.custom.reclaim;

import lombok.Getter;
import dev.lbuddyboy.samurai.persist.PersistMap;

import java.util.UUID;

@Getter
public class ReclaimHandler {

    private final ReclaimMap reclaimMap;

    public ReclaimHandler() {

        (reclaimMap = new ReclaimMap()).loadFromRedis();
    }

    public static class ReclaimMap extends PersistMap<Boolean> {
        public ReclaimMap() {
            super("Reclaimed", "Reclaimed");
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

        public void setReclaimed(UUID update, boolean toggled) {
            updateValueAsync(update, toggled);
        }

        public boolean isReclaimed(UUID check) {
            return (contains(check) ? getValue(check) : false);
        }
    }

}
