package dev.lbuddyboy.pcore.economy;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.lbuddyboy.pcore.economy.transaction.Transaction;
import dev.lbuddyboy.pcore.economy.transaction.TransactionType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class BankAccount {

    private double money, coins;
    private boolean receiving;
    private List<Transaction> transactions = new ArrayList<>();

    public BankAccount(double money, double coins, boolean receiving) {
        this.money = money;
        this.coins = coins;
        this.receiving = receiving;
    }

    public void addTransaction(UUID sender, UUID target, double amount, TransactionType transaction, EconomyType economy) {
        if (this.transactions == null) this.transactions = new ArrayList<>();
        this.transactions.add(Transaction.builder()
                .id(UUID.randomUUID())
                .sender(sender)
                .target(target)
                .sentAt(System.currentTimeMillis())
                .transaction(transaction.name())
                .economy(economy.name())
                .amount(amount)
                .build()
        );
    }

    public JsonObject serialize() {
        JsonObject object = new JsonObject();

        object.addProperty("money", this.money);
        object.addProperty("coins", this.coins);
        object.addProperty("receiving", this.receiving);

        if (!this.transactions.isEmpty()) {
            JsonArray array = new JsonArray();
            this.transactions.forEach(transaction -> array.add(transaction.serialize()));
            object.addProperty("transactions", array.toString());
        }

        return object;
    }

    public static BankAccount deserialize(JsonObject object) {
        BankAccount account = new BankAccount(
                object.has("money") ? object.get("money").getAsDouble() : 0,
                object.has("coins") ? object.get("coins").getAsDouble() : 0,
                object.has("receiving") && object.get("receiving").getAsBoolean()
        );

        if (object.has("transactions")) {
            JsonArray array = new JsonParser().parse(object.get("transactions").getAsString()).getAsJsonArray();
            array.forEach(element -> account.getTransactions().add(Transaction.deserialize(element.getAsJsonObject())));
        }


        return account;
    }

}
