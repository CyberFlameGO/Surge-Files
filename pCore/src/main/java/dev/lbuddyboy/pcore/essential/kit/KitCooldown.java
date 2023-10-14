package dev.lbuddyboy.pcore.essential.kit;

import com.google.gson.JsonObject;
import dev.lbuddyboy.pcore.pCore;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class KitCooldown {

    private String kit;
    private long addedAt, duration;

    public long getExpiry() {
        return (this.addedAt + this.duration) - System.currentTimeMillis();
    }

    public boolean isExpired() {
        return getExpiry() <= 0;
    }

    public boolean isValid() {
        return pCore.getInstance().getKitHandler().getKits().containsKey(this.kit);
    }

    public JsonObject serialize() {
        JsonObject object = new JsonObject();

        object.addProperty("kit", this.kit);
        object.addProperty("addedAt", this.addedAt);
        object.addProperty("duration", this.duration);

        return object;
    }

    public static KitCooldown deserialize(JsonObject object) {
        return new KitCooldown(object.get("kit").getAsString(), object.get("addedAt").getAsLong(), object.get("duration").getAsLong());
    }

}
