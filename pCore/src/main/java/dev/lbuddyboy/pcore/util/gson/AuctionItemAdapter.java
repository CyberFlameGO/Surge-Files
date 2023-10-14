package dev.lbuddyboy.pcore.util.gson;

import com.google.gson.*;
import dev.lbuddyboy.pcore.auction.AuctionItem;
import dev.lbuddyboy.pcore.economy.EconomyType;
import dev.lbuddyboy.pcore.util.ItemUtils;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.UUID;

public class AuctionItemAdapter implements JsonDeserializer<AuctionItem>, JsonSerializer<AuctionItem> {

    @Override
    public AuctionItem deserialize(JsonElement src, Type type, JsonDeserializationContext context) throws JsonParseException {
        return fromJson(src);
    }

    @Override
    public JsonElement serialize(AuctionItem src, Type type, JsonSerializationContext context) {
        return toJson(src);
    }

    public static JsonObject toJson(AuctionItem src) {
        if (src == null) {
            return null;
        }

        JsonObject object = new JsonObject();

        object.addProperty("id", src.getId().toString());
        object.addProperty("sender", src.getSender() == null ? null : src.getSender().toString());
        object.addProperty("item", ItemUtils.itemStackArrayToBase64(new ItemStack[]{src.getItem()}));
        object.addProperty("sentAt", src.getSentAt());
        object.addProperty("duration", src.getDuration());
        object.addProperty("price", src.getPrice());
        object.addProperty("economy", src.getEconomy());
        object.addProperty("cancelled", src.isCancelled());

        return object;
    }

    public static AuctionItem fromJson(JsonElement src) {
        if (src == null || !src.isJsonObject()) {
            return null;
        }
        JsonObject json = src.getAsJsonObject();

        UUID id = UUID.fromString(json.get("id").getAsString());
        UUID sender = null;
        if (!json.get("sender").isJsonNull()) {
            sender = UUID.fromString(json.get("sender").getAsString());
        }
        ItemStack stack = Objects.requireNonNull(ItemUtils.itemStackArrayFromBase64(json.get("item").getAsString()))[0];
        long sentAt = json.get("sentAt").getAsLong();
        long duration = json.get("duration").getAsLong();
        double price = json.get("price").getAsDouble();
        boolean cancelled = json.get("cancelled").getAsBoolean();
        EconomyType economy = EconomyType.valueOf(json.get("economy").getAsString());

        return new AuctionItem(
                id,
                sender,
                stack,
                sentAt,
                duration,
                price,
                economy.name(),
                cancelled
        );
    }

}

