package dev.aurapvp.samurai.economy;

import dev.aurapvp.samurai.economy.transaction.Transaction;
import dev.aurapvp.samurai.economy.transaction.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class BankAccount {

    private double money, coins;
    private boolean receiving, needsSaving;
    private List<Transaction> transactions = new ArrayList<>();

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

}
