package dev.lbuddyboy.pcore.economy.transaction;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.lbuddyboy.pcore.economy.EconomyType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class Transaction {

    private UUID id, sender, target;
    private long sentAt;
    private double amount;
    private String transaction, economy;

    public TransactionType getTransactionType() {
        return TransactionType.valueOf(this.transaction);
    }

    public EconomyType getEconomyType() {
        return EconomyType.valueOf(this.economy);
    }

    public JsonObject serialize() {
        JsonObject object = new JsonObject();

        object.addProperty("id", this.id.toString());
        object.addProperty("sender", this.sender == null ? null : this.sender.toString());
        object.addProperty("target", this.target.toString());
        object.addProperty("sentAt", this.sentAt);
        object.addProperty("amount", this.amount);
        object.addProperty("transaction", this.transaction);
        object.addProperty("economy", this.economy);

        return object;
    }

    public static Transaction deserialize(JsonObject object) {
        UUID id = UUID.fromString(object.get("id").getAsString());
        UUID sender = null;
        if (!object.get("sender").isJsonNull()) {
            sender = UUID.fromString(object.get("sender").getAsString());
        }
        UUID target = UUID.fromString(object.get("target").getAsString());
        long sentAt = object.get("sentAt").getAsLong();
        double amount = object.get("amount").getAsDouble();
        TransactionType transaction = TransactionType.valueOf(object.get("transaction").getAsString());
        EconomyType economy = EconomyType.valueOf(object.get("economy").getAsString());

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
