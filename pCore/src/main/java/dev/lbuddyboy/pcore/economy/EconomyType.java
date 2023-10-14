package dev.lbuddyboy.pcore.economy;

import dev.drawethree.xprison.XPrison;
import dev.drawethree.xprison.api.enums.LostCause;
import dev.drawethree.xprison.api.enums.ReceiveCause;
import dev.lbuddyboy.pcore.pCore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.UUID;

@AllArgsConstructor
@Getter
public enum EconomyType {

    COINS('⛁', "Token", "Tokens"),
    MONEY('$', "Money", "Dollars");

    private final char prefix;
    private final String name;
    private final String displayName;

    public double getAmount(UUID uuid) {
        if (this == COINS) {
            return XPrison.getInstance().getTokens().getApi().getPlayerTokens(Bukkit.getOfflinePlayer(uuid));
        }
        BankAccount account = pCore.getInstance().getEconomyHandler().getEconomy().getBankAccount(uuid, true);

        return account.getMoney();
    }

    public boolean addAmount(UUID uuid, double amount, IEconomy.EconomyChange change) {
        if (this == COINS) {
            XPrison.getInstance().getTokens().getApi().addTokens(Bukkit.getOfflinePlayer(uuid), (int) amount, ReceiveCause.GIVE);
            return true;
        }

        return pCore.getInstance().getEconomyHandler().getEconomy().addMoney(uuid, amount, change);
    }

    public boolean removeAmount(UUID uuid, double amount, IEconomy.EconomyChange change) {
        if (this == COINS) {
            XPrison.getInstance().getTokens().getApi().removeTokens(Bukkit.getOfflinePlayer(uuid), (int) amount, LostCause.ADMIN);
            return true;
        }

        return pCore.getInstance().getEconomyHandler().getEconomy().removeMoney(uuid, amount, change);
    }

    public boolean hasAmount(UUID uuid, double amount) {
        return getAmount(uuid) >= amount;
    }

}
