package dev.lbuddyboy.pcore.economy.impl;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.economy.BankAccount;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.economy.IEconomy;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.ServicePriority;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class VaultEconomy extends CustomEconomy implements Economy {

    public VaultEconomy() {
        pCore.getInstance().getServer().getServicesManager().register(Economy.class, this, pCore.getInstance(), ServicePriority.Highest);
        Bukkit.getConsoleSender().sendMessage(CC.translate("[Economy] Vault was found and hooked properly!"));
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "pCore Economy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double v) {
        return this.formatMoney(v);
    }

    @Override
    public String currencyNamePlural() {
        return "Dollars";
    }

    @Override
    public String currencyNameSingular() {
        return "Dollar";
    }

    @Override
    public boolean hasAccount(String s) {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return hasAccount(offlinePlayer, null);
    }

    @Override
    public boolean hasAccount(String s, String s1) {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
        return getBankAccount(offlinePlayer.getUniqueId(), true) != null;
    }

    @Override
    public double getBalance(String s) {
        return 0;
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        return getBalance(offlinePlayer, null);
    }

    @Override
    public double getBalance(String s, String s1) {
        return 0;
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        return getBankAccount(offlinePlayer.getUniqueId(), true).getMoney();
    }

    @Override
    public boolean has(String s, double v) {
        return false;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double v) {
        return getBalance(offlinePlayer) >= v;
    }

    @Override
    public boolean has(String s, String s1, double v) {
        return false;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
        return getBalance(offlinePlayer) >= v;
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double v) {
        return withdrawPlayer(offlinePlayer, null, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        boolean success = removeMoney(offlinePlayer.getUniqueId(), v, IEconomy.EconomyChange.builder()
                .forced(false)
                .predicate(() -> has(offlinePlayer, v))
                .build());

        return new EconomyResponse(v, getBalance(offlinePlayer), success ? EconomyResponse.ResponseType.SUCCESS : EconomyResponse.ResponseType.FAILURE, CC.RED + "Insufficient funds.");
    }

    @Override
    public EconomyResponse depositPlayer(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double v) {
        return depositPlayer(offlinePlayer, null, v);
    }

    @Override
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        addMoney(offlinePlayer.getUniqueId(), v, IEconomy.EconomyChange.builder()
                .forced(false)
                .predicate(() -> getBankAccount(offlinePlayer.getUniqueId(), true).isReceiving())
                .build());

        return new EconomyResponse(v, getBalance(offlinePlayer), has(offlinePlayer, v) ? EconomyResponse.ResponseType.SUCCESS : EconomyResponse.ResponseType.FAILURE, CC.RED + "Insufficient funds.");
    }

    @Override
    public EconomyResponse createBank(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String s) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        return createPlayerAccount(offlinePlayer, null);
    }

    @Override
    public boolean createPlayerAccount(String s, String s1) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        BankAccount account = createBankAccount(offlinePlayer.getUniqueId(), true);
        return account != null;
    }

}
