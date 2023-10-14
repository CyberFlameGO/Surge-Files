package dev.aurapvp.samurai.essential.locator;

import com.google.gson.JsonObject;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.util.LocationUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Location;

import java.util.UUID;

@AllArgsConstructor
@Data
public class ItemLocation {

    private Location location;
    private UUID holderUUID;
    private LocationType type;

    public Location getLocation() {
        return this.holderUUID == null ? this.location : Samurai.getInstance().getOfflineHandler().fetchCache(this.holderUUID).getLocation();
    }

    public JsonObject serialize() {
        JsonObject object = new JsonObject();

        if (this.location != null) object.addProperty("location", LocationUtils.serializeString(this.location));
        if (this.holderUUID != null) object.addProperty("holderUUID", this.holderUUID.toString());
        if (this.type != null) object.addProperty("type", this.type.name());

        return object;
    }

    public static ItemLocation deserialize(JsonObject object) {
        return new ItemLocation(
                object.has("location") ? LocationUtils.deserializeString(object.get("location").getAsString()) : null,
                object.has("holderUUID") ? UUID.fromString(object.get("holderUUID").getAsString()) : null,
                object.has("type") ? LocationType.valueOf(object.get("type").getAsString()) : LocationType.DESPAWNED
        );
    }

}
