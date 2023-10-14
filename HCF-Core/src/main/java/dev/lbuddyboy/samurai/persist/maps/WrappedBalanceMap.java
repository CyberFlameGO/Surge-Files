package dev.lbuddyboy.samurai.persist.maps;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.persist.PersistMap;

import java.util.UUID;

public class WrappedBalanceMap extends PersistMap<Double> {

    public WrappedBalanceMap() {
        super("WrappedBalances", "Balance");
        //Basic.get().getEconomyManager().registerListener(new FoxEconomyListener());
    }

    @Override
    public String getRedisValue(Double balance) {
        return (String.valueOf(balance));
    }

    @Override
    public Double getJavaObject(String str) {
        return (Double.parseDouble(str));
    }

    @Override
    public Object getMongoValue(Double balance) {
        return (balance);
    }

    public double getBalance(UUID check) {
        return (contains(check) ? getValue(check) : 0);
    }

    public void setBalance(UUID update, double balance) {
        updateValueAsync(update, balance);
        if (balance > Samurai.getInstance().getHighestBalanceMap().getStatistic(update)) {
            Samurai.getInstance().getHighestBalanceMap().setStatistic(update, (int) balance);
        }
    }

}