package dev.lbuddyboy.samurai.custom.battlepass.tier.serialize;

import com.google.gson.*;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.battlepass.tier.Tier;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class TierSetSerializer implements JsonSerializer<Set<Tier>>, JsonDeserializer<Set<Tier>> {

    @Override
    public JsonElement serialize(Set<Tier> tiers, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonArray array = new JsonArray();

        for (Tier tier : tiers) {
            array.add(new JsonPrimitive(tier.getNumber()));
        }

        return array;
    }

    @Override
    public Set<Tier> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Set<Tier> tiers = new HashSet<>();

        for (JsonElement element : jsonElement.getAsJsonArray()) {
            Tier tier = Samurai.getInstance().getBattlePassHandler().getTier(element.getAsInt());
            if (tier != null) {
                tiers.add(tier);
            }
        }

        return tiers;
    }

}