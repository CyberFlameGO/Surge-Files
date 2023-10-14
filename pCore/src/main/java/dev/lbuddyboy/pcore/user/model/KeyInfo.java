package dev.lbuddyboy.pcore.user.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.lbuddyboy.pcore.essential.offline.OfflineData;
import dev.lbuddyboy.pcore.util.ItemUtils;
import dev.lbuddyboy.pcore.util.LocationUtils;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class KeyInfo {

    private Map<String, Integer> virtualKeys = new HashMap<>();

    public JsonObject serialize() {
        JsonObject object = new JsonObject();

        for (Map.Entry<String, Integer> entry : virtualKeys.entrySet()) {
            object.addProperty(entry.getKey(), entry.getValue());
        }

        return object;
    }

    public static KeyInfo deserialize(JsonObject object) {
        KeyInfo info = new KeyInfo();

        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            info.getVirtualKeys().put(entry.getKey(), entry.getValue().getAsInt());
        }

        return info;
    }

}
