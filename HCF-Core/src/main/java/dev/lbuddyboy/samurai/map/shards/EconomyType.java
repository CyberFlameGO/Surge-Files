package dev.lbuddyboy.samurai.map.shards;

import dev.lbuddyboy.samurai.economy.FrozenEconomyHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;

import java.util.UUID;

@AllArgsConstructor
@Getter
public enum EconomyType {

    SHARDS('◆', "Shard", "Shards"),
    MONEY('$', "Money", "Dollars");

    private final char prefix;
    private final String name;
    private final String displayName;

    public double getAmount(UUID uuid) {
        if (this == SHARDS) {
            return Samurai.getInstance().getShardMap().getShards(uuid);
        }

        return FrozenEconomyHandler.getBalance(uuid);
    }

    public void addAmount(UUID uuid, int amount) {
        if (this == SHARDS) {
            Samurai.getInstance().getShardMap().addShards(uuid, amount);
            return;
        }

        FrozenEconomyHandler.deposit(uuid, amount);
    }

    public boolean removeAmount(UUID uuid, int amount) {
        if (this == SHARDS) {
            if (!hasAmount(uuid, amount)) return false;

            Samurai.getInstance().getShardMap().removeShards(uuid, amount);
            return true;
        }
        if (!hasAmount(uuid, amount)) return false;

        FrozenEconomyHandler.withdraw(uuid, amount);
        return true;
    }

    public boolean hasAmount(UUID uuid, double amount) {
        return getAmount(uuid) >= amount;
    }

}
