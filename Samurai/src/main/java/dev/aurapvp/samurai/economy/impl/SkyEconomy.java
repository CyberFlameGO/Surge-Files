package dev.aurapvp.samurai.economy.impl;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.SettingsConfiguration;
import dev.aurapvp.samurai.economy.BankAccount;
import dev.aurapvp.samurai.economy.EconomyType;
import dev.aurapvp.samurai.economy.IEconomy;
import dev.aurapvp.samurai.economy.transaction.TransactionType;
import dev.aurapvp.samurai.util.gson.GSONUtils;

import java.util.Map;
import java.util.UUID;

public class SkyEconomy implements IEconomy {

    public SkyEconomy(Map<UUID, BankAccount> accounts) {
        Samurai.getInstance().getStorageHandler().getStorage().loadData(
                accounts,
                "bank",
                GSONUtils.BANK_ACCOUNT,
                true
        );
    }

    @Override
    public BankAccount createBankAccount(UUID uuid, boolean async) {
        if (hasBankAccount(uuid)) return getBankAccount(uuid, async);

        int money = SettingsConfiguration.FIRST_JOIN_MONEY.getInt();
        int coins = SettingsConfiguration.FIRST_JOIN_COINS.getInt();

        Samurai.getInstance().getEconomyHandler().getBanks().put(uuid, BankAccount.builder()
                .money(money)
                .coins(coins)
                .receiving(true)
                .build());

        return saveBankAccount(uuid, async);
    }

    @Override
    public BankAccount saveBankAccount(UUID uuid, boolean async) {
        BankAccount account = getBankAccount(uuid, async);

        account.setNeedsSaving(false);

        Samurai.getInstance().getStorageHandler().getStorage().insertData(uuid,
                "bank",
                account,
                GSONUtils.BANK_ACCOUNT,
                async
        );

        return account;
    }

    @Override
    public void saveAll() {
        for (Map.Entry<UUID, BankAccount> entry : Samurai.getInstance().getEconomyHandler().getBanks().entrySet()) {
            if (!entry.getValue().isNeedsSaving()) continue;

            saveBankAccount(entry.getKey(), false);
        }
    }

    @Override
    public void wipeBankAccount(UUID uuid) {
        Samurai.getInstance().getStorageHandler().getStorage().wipeData(uuid, true);
        Samurai.getInstance().getEconomyHandler().getBanks().remove(uuid);

        createBankAccount(uuid, true);
    }

    @Override
    public void setReceiving(UUID uuid, boolean toggle) {
        BankAccount account = getBankAccount(uuid, true);
        account.setNeedsSaving(true);
        account.setReceiving(toggle);
    }

    @Override
    public boolean hasBankAccount(UUID uuid) {
        return getBankAccount(uuid, false) != null;
    }

    @Override
    public boolean addCoins(UUID target, double amount, EconomyChange change) {
        BankAccount account = getBankAccount(target, true);

        if (change.isForced()) {
            account.setNeedsSaving(true);
            account.setCoins(account.getCoins() + amount);
            account.addTransaction(null, target, amount, TransactionType.ADD, EconomyType.COINS);
            return true;
        }

        if (!account.isReceiving()) return false;
        if (change.getPredicate() != null && !change.getPredicate().test()) return false;

        account.setNeedsSaving(true);
        account.setCoins(account.getCoins() + amount);
        account.addTransaction(null, target, amount, TransactionType.ADD, EconomyType.COINS);
        return true;
    }

    @Override
    public boolean removeCoins(UUID target, double amount, EconomyChange change) {
        BankAccount account = getBankAccount(target, true);

        if (change.isForced()) {
            account.setNeedsSaving(true);
            account.setCoins(account.getCoins() - amount);
            account.addTransaction(null, target, amount, TransactionType.REMOVE, EconomyType.COINS);
            return true;
        }

        if (account.getCoins() < amount) return false;
        if (change.getPredicate() != null && !change.getPredicate().test()) return false;

        account.setNeedsSaving(true);
        account.setCoins(account.getCoins() - amount);
        account.addTransaction(null, target, amount, TransactionType.REMOVE, EconomyType.COINS);
        return true;
    }

    @Override
    public boolean setCoins(UUID target, double amount, EconomyChange change) {
        BankAccount account = getBankAccount(target, true);

        if (change.isForced()) {
            account.setNeedsSaving(true);
            account.setCoins(amount);
            account.addTransaction(null, target, amount, TransactionType.SET, EconomyType.COINS);
            return true;
        }

        if (change.getPredicate() != null && !change.getPredicate().test()) return false;

        account.setNeedsSaving(true);
        account.setCoins(amount);
        account.addTransaction(null, target, amount, TransactionType.SET, EconomyType.COINS);
        return true;
    }

    @Override
    public boolean addMoney(UUID target, double amount, EconomyChange change) {
        BankAccount account = getBankAccount(target, true);

        if (change.isForced()) {
            account.setNeedsSaving(true);
            account.setMoney(account.getMoney() + amount);
            account.addTransaction(null, target, amount, TransactionType.ADD, EconomyType.MONEY);
            return true;
        }

        if (!account.isReceiving()) {
            return false;
        }

        if (change.getPredicate() != null && !change.getPredicate().test()) return false;

        account.setNeedsSaving(true);
        account.setMoney(account.getMoney() + amount);
        account.addTransaction(null, target, amount, TransactionType.ADD, EconomyType.MONEY);
        return true;
    }

    @Override
    public boolean removeMoney(UUID target, double amount, EconomyChange change) {
        BankAccount account = getBankAccount(target, true);

        if (change.isForced()) {
            account.setNeedsSaving(true);
            account.setMoney(account.getMoney() - amount);
            account.addTransaction(null, target, amount, TransactionType.REMOVE, EconomyType.MONEY);
            return true;
        }

        if (account.getMoney() < amount) return false;
        if (change.getPredicate() != null && !change.getPredicate().test()) return false;

        account.setNeedsSaving(true);
        account.setMoney(account.getMoney() - amount);
        account.addTransaction(null, target, amount, TransactionType.REMOVE, EconomyType.MONEY);
        return true;
    }

    @Override
    public boolean setMoney(UUID target, double amount, EconomyChange change) {
        BankAccount account = getBankAccount(target, true);

        if (change.isForced()) {
            account.setNeedsSaving(true);
            account.setMoney(amount);
            account.addTransaction(null, target, amount, TransactionType.SET, EconomyType.MONEY);
            return true;
        }

        if (change.getPredicate() != null && !change.getPredicate().test()) return false;

        account.setNeedsSaving(true);
        account.setMoney(amount);
        account.addTransaction(null, target, amount, TransactionType.SET, EconomyType.MONEY);
        return true;
    }

    @Override
    public BankAccount getBankAccount(UUID target, boolean searchDB) {
        return Samurai.getInstance().getEconomyHandler().getBankAccount(target);
    }
}
