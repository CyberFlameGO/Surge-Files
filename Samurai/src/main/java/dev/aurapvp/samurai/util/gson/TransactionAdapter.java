package dev.aurapvp.samurai.util.gson;

import dev.aurapvp.samurai.economy.EconomyType;
import dev.aurapvp.samurai.economy.transaction.Transaction;

import com.google.gson.*;
import dev.aurapvp.samurai.economy.transaction.TransactionType;

import java.lang.reflect.Type;
import java.util.UUID;

public class TransactionAdapter implements JsonDeserializer<Transaction>, JsonSerializer<Transaction> {

    @Override
    public Transaction deserialize(JsonElement src, Type type, JsonDeserializationContext context) throws JsonParseException {
        return fromJson(src);
    }

    @Override
    public JsonElement serialize(Transaction src, Type type, JsonSerializationContext context) {
        return toJson(src);
    }

    public static JsonObject toJson(Transaction src) {
        if (src == null) {
            return null;
        }

        final JsonObject object = new JsonObject();

        object.addProperty("id", src.getId().toString());
        object.addProperty("sender", src.getSender() == null ? null : src.getSender().toString());
        object.addProperty("target", src.getTarget().toString());
        object.addProperty("sentAt", src.getSentAt());
        object.addProperty("amount", src.getAmount());
        object.addProperty("transaction", src.getTransaction());
        object.addProperty("economy", src.getEconomy());

        return object;
    }

    public static Transaction fromJson(JsonElement src) {
        if (src == null || !src.isJsonObject()) {
            return null;
        }
        JsonObject json = src.getAsJsonObject();

        UUID id = UUID.fromString(json.get("id").getAsString());
        UUID sender = null;
        if (!json.get("sender").isJsonNull()) {
            sender = UUID.fromString(json.get("sender").getAsString());
        }
        UUID target = UUID.fromString(json.get("target").getAsString());
        long sentAt = json.get("sentAt").getAsLong();
        double amount = json.get("amount").getAsDouble();
        TransactionType transaction = TransactionType.valueOf(json.get("transaction").getAsString());
        EconomyType economy = EconomyType.valueOf(json.get("economy").getAsString());

        return Transaction.builder()
                .id(id)
                .sender(sender)
                .target(target)
                .sentAt(sentAt)
                .amount(amount)
                .transaction(transaction.name())
                .economy(economy.name())
                .build();
    }

}

