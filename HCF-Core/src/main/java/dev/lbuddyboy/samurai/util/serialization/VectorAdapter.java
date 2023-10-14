//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dev.lbuddyboy.samurai.util.serialization;

import com.google.gson.*;
import org.bukkit.util.Vector;

import java.lang.reflect.Type;

public class VectorAdapter implements JsonDeserializer<Vector>, JsonSerializer<Vector> {
    public VectorAdapter() {
    }

    public Vector deserialize(JsonElement src, Type type, JsonDeserializationContext context) throws JsonParseException {
        return fromJson(src);
    }

    public JsonElement serialize(Vector src, Type type, JsonSerializationContext context) {
        return toJson(src);
    }

    public static JsonObject toJson(Vector src) {
        if (src == null) {
            return null;
        } else {
            JsonObject object = new JsonObject();
            object.addProperty("x", src.getX());
            object.addProperty("y", src.getY());
            object.addProperty("z", src.getZ());
            return object;
        }
    }

    public static Vector fromJson(JsonElement src) {
        if (src != null && src.isJsonObject()) {
            JsonObject json = src.getAsJsonObject();
            double x = json.get("x").getAsDouble();
            double y = json.get("y").getAsDouble();
            double z = json.get("z").getAsDouble();
            return new Vector(x, y, z);
        } else {
            return null;
        }
    }
}
