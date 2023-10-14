package dev.aurapvp.samurai.util.gson;

import com.google.gson.*;
import dev.aurapvp.samurai.economy.EconomyType;
import dev.aurapvp.samurai.economy.transaction.Transaction;
import dev.aurapvp.samurai.economy.transaction.TransactionType;
import dev.aurapvp.samurai.faction.Faction;

import java.lang.reflect.Type;
import java.util.UUID;

public class FactionAdapter implements JsonDeserializer<Faction>, JsonSerializer<Faction> {

    @Override
    public Faction deserialize(JsonElement src, Type type, JsonDeserializationContext context) throws JsonParseException {
        return fromJson(src);
    }

    @Override
    public JsonElement serialize(Faction src, Type type, JsonSerializationContext context) {
        return toJson(src);
    }

    public static JsonObject toJson(Faction src) {
        if (src == null) {
            return null;
        }
        return src.saveJSON();
    }

    public static Faction fromJson(JsonElement src) {
        if (src == null || !src.isJsonObject()) {
            return null;
        }

        return new Faction(UUID.fromString(src.getAsJsonObject().get("uniqueId").getAsString()), src);
    }

}

