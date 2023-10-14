package dev.lbuddyboy.samurai.map.shards.coinflip;

import dev.lbuddyboy.flash.util.menu.PagedGUISettings;
import dev.lbuddyboy.samurai.map.shards.EconomyType;
import dev.lbuddyboy.samurai.map.shards.coinflip.listener.CoinFlipListener;
import dev.lbuddyboy.samurai.map.shards.coinflip.stat.CoinFlipLossesMap;
import dev.lbuddyboy.samurai.map.shards.coinflip.stat.CoinFlipProfitMap;
import dev.lbuddyboy.samurai.map.shards.coinflip.stat.CoinFlipWinsMap;
import dev.lbuddyboy.samurai.server.commands.PriceAmountContext;
import dev.lbuddyboy.samurai.util.object.Config;
import dev.lbuddyboy.samurai.util.PriceAmount;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.shards.coinflip.command.CoinFlipCommand;
import dev.lbuddyboy.samurai.util.cooldown.Cooldown;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class CoinFlipHandler {

    private final Map<UUID, CoinFlip> coinFlips;
    private final Cooldown cooldown;
    private Config config;
    private PagedGUISettings guiSettings;
    private final CoinFlipLossesMap coinFlipLossesMap;
    private final CoinFlipProfitMap coinFlipProfitMap;
    private final CoinFlipWinsMap coinFlipWinsMap;

    public CoinFlipHandler() {
        this.coinFlips = new ConcurrentHashMap<>();
        this.cooldown = new Cooldown();
        (coinFlipLossesMap = new CoinFlipLossesMap()).loadFromRedis();
        (coinFlipProfitMap = new CoinFlipProfitMap()).loadFromRedis();
        (coinFlipWinsMap = new CoinFlipWinsMap()).loadFromRedis();
        Samurai.getInstance().getPaperCommandManager().getCommandContexts().registerContext(PriceAmount.class, new PriceAmountContext());
        Samurai.getInstance().getPaperCommandManager().registerCommand(new CoinFlipCommand());
        Samurai.getInstance().getServer().getPluginManager().registerEvents(new CoinFlipListener(), Samurai.getInstance());

        reload();
    }

    public void unload(Samurai plugin) {
        this.coinFlips.values().forEach(CoinFlip::refund);
    }

    public void reload() {
        unload(Samurai.getInstance());
        this.config = new Config(Samurai.getInstance(), "coin-flip");
        this.guiSettings = new PagedGUISettings(config);
    }

    public CoinFlip createCoinFlip(Player sender, int amount, EconomyType economy, CoinFlipType side) {
        return this.coinFlips.put(sender.getUniqueId(), new CoinFlip(sender.getUniqueId(), side, amount, economy, System.currentTimeMillis()));
    }

    public int getWins(UUID uuid) {
        return this.coinFlipWinsMap.getStatistic(uuid);
    }

    public int getLosses(UUID uuid) {
        return this.coinFlipLossesMap.getStatistic(uuid);
    }

    public int getProfit(UUID uuid) {
        return this.coinFlipProfitMap.getStatistic(uuid);
    }

    public double incrementProfit(UUID uuid, int amount) {
        this.coinFlipProfitMap.incrementStatistic(uuid, amount);

        return getProfit(uuid);
    }

    public double decrementProfit(UUID uuid, int amount) {
        this.coinFlipProfitMap.setStatistic(uuid, getProfit(uuid) - amount);

        return getProfit(uuid);
    }

    public int incrementWins(UUID uuid) {
        this.coinFlipWinsMap.incrementStatistic(uuid, 1);

        return getWins(uuid);
    }

    public int incrementLosses(UUID uuid) {
        this.coinFlipLossesMap.incrementStatistic(uuid, 1);

        return getLosses(uuid);
    }

}
