package dev.aurapvp.samurai.faction.logs;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import dev.aurapvp.samurai.faction.claim.Claim;
import dev.aurapvp.samurai.faction.logs.impl.ForceClaim;
import dev.aurapvp.samurai.util.gson.GSONUtils;
import lombok.Getter;
import org.bukkit.Material;

import java.lang.reflect.Type;
import java.util.List;

@Getter
public abstract class FactionLog {

    public long sentAt;

    public abstract String getId();
    public abstract Material getMaterial();
    public abstract byte getMaterialData();
    public abstract List<String> getLoreFormat(Object... replacements);
    public abstract JsonObject serialize();

    public static FactionLog deserialize(JsonObject object, Type type) {
        return GSONUtils.getGSON().fromJson(object, type);
    }

    public static JsonArray asArray(List<FactionLog> logs) {
        JsonArray array = new JsonArray();

//        logs.forEach(log -> array.add());

        return array;
    }

}
