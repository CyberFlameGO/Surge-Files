package dev.lbuddyboy.pcore.economy.impl;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.SettingsConfiguration;
import dev.lbuddyboy.pcore.economy.BankAccount;
import dev.lbuddyboy.pcore.economy.EconomyType;
import dev.lbuddyboy.pcore.economy.IEconomy;
import dev.lbuddyboy.pcore.economy.transaction.TransactionType;
import dev.lbuddyboy.pcore.user.MineUser;
import dev.lbuddyboy.pcore.util.gson.GSONUtils;

import java.util.Map;
import java.util.UUID;

public class CustomEconomy implements IEconomy {

    @Override
    public BankAccount createBankAccount(UUID uuid, boolean async) {
        if (hasBankAccount(uuid)) return getBankAccount(uuid, async);

        int money = SettingsConfiguration.FIRST_JOIN_MONEY.getInt();
        int coins = SettingsConfiguration.FIRST_JOIN_COINS.getInt();

        MineUser user = pCore.getInstance().getMineUserHandler().tryMineUserAsync(uuid);
        if (user != null) {
            user.setBankAccount(new BankAccount(money, coins, true));
            user.flagUpdate();
        }

        return saveBankAccount(uuid, async);
    }

    @Override
    public BankAccount saveBankAccount(UUID uuid, boolean async) {
        BankAccount account = getBankAccount(uuid, async);

        MineUser user = pCore.getInstance().getMineUserHandler().tryMineUserAsync(uuid);
        if (user != null) {
            user.flagUpdate();
        }

        return account;
    }

    @Override
    public void wipeBankAccount(UUID uuid) {
        createBankAccount(uuid, true);
    }

    @Override
    public void setReceiving(UUID uuid, boolean toggle) {
        BankAccount account = getBankAccount(uuid, true);
        account.setReceiving(toggle);
        saveBankAccount(uuid, true);
    }

    @Override
    public boolean hasBankAccount(UUID uuid) {
        return getBankAccount(uuid, false) != null;
    }

    @Override
    public boolean addCoins(UUID target, double amount, EconomyChange change) {
        BankAccount account = getBankAccount(target, true);

        if (change.isForced()) {
            account.setCoins(account.getCoins() + amount);
            account.addTransaction(null, target, amount, TransactionType.ADD, EconomyType.COINS);
            saveBankAccount(target, true);
            return true;
        }

        if (!account.isReceiving()) return false;
        if (change.getPredicate() != null && !change.getPredicate().test()) return false;

        account.setCoins(account.getCoins() + amount);
        account.addTransaction(null, target, amount, TransactionType.ADD, EconomyType.COINS);
        saveBankAccount(target, true);
        return true;
    }

    @Override
    public boolean removeCoins(UUID target, double amount, EconomyChange change) {
        BankAccount account = getBankAccount(target, true);

        if (change.isForced()) {
            account.setCoins(account.getCoins() - amount);
            account.addTransaction(null, target, amount, TransactionType.REMOVE, EconomyType.COINS);
            saveBankAccount(target, true);
            return true;
        }

        if (account.getCoins() < amount) return false;
        if (change.getPredicate() != null && !change.getPredicate().test()) return false;

        account.setCoins(account.getCoins() - amount);
        account.addTransaction(null, target, amount, TransactionType.REMOVE, EconomyType.COINS);
        saveBankAccount(target, true);
        return true;
    }

    @Override
    public boolean setCoins(UUID target, double amount, EconomyChange change) {
        BankAccount account = getBankAccount(target, true);

        if (change.isForced()) {
            account.setCoins(amount);
            account.addTransaction(null, target, amount, TransactionType.SET, EconomyType.COINS);
            saveBankAccount(target, true);
            return true;
        }

        if (change.getPredicate() != null && !change.getPredicate().test()) return false;

        account.setCoins(amount);
        account.addTransaction(null, target, amount, TransactionType.SET, EconomyType.COINS);
        saveBankAccount(target, true);
        return true;
    }

    @Override
    public boolean addMoney(UUID target, double amount, EconomyChange change) {
        BankAccount account = getBankAccount(target, true);

        if (change.isForced()) {
            account.setMoney(account.getMoney() + amount);
            account.addTransaction(null, target, amount, TransactionType.ADD, EconomyType.MONEY);
            saveBankAccount(target, true);
            return true;
        }

        if (!account.isReceiving()) {
            return false;
        }

        if (change.getPredicate() != null && !change.getPredicate().test()) return false;

        account.setMoney(account.getMoney() + amount);
        account.addTransaction(null, target, amount, TransactionType.ADD, EconomyType.MONEY);
        saveBankAccount(target, true);
        return true;
    }

    @Override
    public boolean removeMoney(UUID target, double amount, EconomyChange change) {
        BankAccount account = getBankAccount(target, true);

        if (change.isForced()) {
            account.setMoney(account.getMoney() - amount);
            account.addTransaction(null, target, amount, TransactionType.REMOVE, EconomyType.MONEY);
            saveBankAccount(target, true);
            return true;
        }

        if (account.getMoney() < amount) return false;
        if (change.getPredicate() != null && !change.getPredicate().test()) return false;

        account.setMoney(account.getMoney() - amount);
        account.addTransaction(null, target, amount, TransactionType.REMOVE, EconomyType.MONEY);
        saveBankAccount(target, true);
        return true;
    }

    @Override
    public boolean setMoney(UUID target, double amount, EconomyChange change) {
        BankAccount account = getBankAccount(target, true);

        if (change.isForced()) {
            account.setMoney(amount);
            account.addTransaction(null, target, amount, TransactionType.SET, EconomyType.MONEY);
            saveBankAccount(target, true);
            return true;
        }

        if (change.getPredicate() != null && !change.getPredicate().test()) return false;

        account.setMoney(amount);
        account.addTransaction(null, target, amount, TransactionType.SET, EconomyType.MONEY);
        saveBankAccount(target, true);
        return true;
    }

    @Override
    public BankAccount getBankAccount(UUID target, boolean searchDB) {
        return pCore.getInstance().getEconomyHandler().getBankAccount(target);
    }
}
