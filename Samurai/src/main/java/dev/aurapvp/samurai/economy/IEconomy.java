package dev.aurapvp.samurai.economy;

import dev.aurapvp.samurai.util.SPredicate;
import lombok.Builder;
import lombok.Getter;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

public interface IEconomy {

    BankAccount createBankAccount(UUID uuid, boolean async);
    BankAccount saveBankAccount(UUID uuid, boolean async);
    void saveAll();
    void wipeBankAccount(UUID uuid);
    void setReceiving(UUID uuid, boolean toggle);
    boolean hasBankAccount(UUID uuid);
    boolean addCoins(UUID target, double amount, EconomyChange change);
    boolean removeCoins(UUID target, double amount, EconomyChange change);
    boolean setCoins(UUID target, double amount, EconomyChange change);
    boolean addMoney(UUID target, double amount, EconomyChange change);
    boolean removeMoney(UUID target, double amount, EconomyChange change);
    boolean setMoney(UUID target, double amount, EconomyChange change);
    BankAccount getBankAccount(UUID target, boolean async);
    default String formatMoney(double money) {
        return NumberFormat.getInstance(Locale.US).format(money);
    }

    @Builder
    @Getter
    class EconomyChange {
        private boolean forced;
        private SPredicate predicate;
    }

}
