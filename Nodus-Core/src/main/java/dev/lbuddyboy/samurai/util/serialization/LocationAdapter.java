//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dev.lbuddyboy.samurai.util.serialization;

import com.google.gson.*;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Type;

public class LocationAdapter implements JsonDeserializer<Location>, JsonSerializer<Location> {
    public LocationAdapter() {
    }

    public JsonElement serialize(Location src, Type typeOfSrc, JsonSerializationContext context) {
        return toJson(src);
    }

    public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return fromJson(json);
    }

    public static JsonObject toJson(Location location) {
        if (location == null) {
            return null;
        } else {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("world", location.getWorld().getName());
            jsonObject.addProperty("x", location.getX());
            jsonObject.addProperty("y", location.getY());
            jsonObject.addProperty("z", location.getZ());
            jsonObject.addProperty("yaw", location.getYaw());
            jsonObject.addProperty("pitch", location.getPitch());
            return jsonObject;
        }
    }

    public static Location fromJson(JsonElement jsonElement) {
        if (jsonElement != null && jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            World world = Samurai.getInstance().getServer().getWorld(jsonObject.get("world").getAsString());
            double x = jsonObject.get("x").getAsDouble();
            double y = jsonObject.get("y").getAsDouble();
            double z = jsonObject.get("z").getAsDouble();
            float yaw = jsonObject.get("yaw").getAsFloat();
            float pitch = jsonObject.get("pitch").getAsFloat();
            return new Location(world, x, y, z, yaw, pitch);
        } else {
            return null;
        }
    }
}
