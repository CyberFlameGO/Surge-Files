package dev.aurapvp.samurai.economy.transaction;

import dev.aurapvp.samurai.economy.EconomyType;
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

}
