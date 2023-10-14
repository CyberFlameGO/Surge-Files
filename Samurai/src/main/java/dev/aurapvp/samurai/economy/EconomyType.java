package dev.aurapvp.samurai.economy;

import dev.aurapvp.samurai.Samurai;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public enum EconomyType {

    COINS('⛁', "Coin", "Coins"),
    MONEY('$', "Money", "Dollars");

    private final char prefix;
    private final String name;
    private final String displayName;

    public double getAmount(UUID uuid) {
        BankAccount account = Samurai.getInstance().getEconomyHandler().getEconomy().getBankAccount(uuid, true);

        if (this == COINS) {
            return account.getCoins();
        }

        return account.getMoney();
    }

    public boolean addAmount(UUID uuid, double amount, IEconomy.EconomyChange change) {
        if (this == COINS) {
            return Samurai.getInstance().getEconomyHandler().getEconomy().addCoins(uuid, amount, change);
        }

        return Samurai.getInstance().getEconomyHandler().getEconomy().addMoney(uuid, amount, change);
    }

    public boolean removeAmount(UUID uuid, double amount, IEconomy.EconomyChange change) {
        if (this == COINS) {
            return Samurai.getInstance().getEconomyHandler().getEconomy().removeCoins(uuid, amount, change);
        }

        return Samurai.getInstance().getEconomyHandler().getEconomy().removeMoney(uuid, amount, change);
    }

    public boolean hasAmount(UUID uuid, double amount) {
        return getAmount(uuid) >= amount;
    }

}
