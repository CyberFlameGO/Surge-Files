package dev.lbuddyboy.pcore.coinflip;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.coinflip.command.CoinFlipCommand;
import dev.lbuddyboy.pcore.coinflip.listener.CoinFlipListener;
import dev.lbuddyboy.pcore.economy.EconomyType;
import dev.lbuddyboy.pcore.storage.IStorage;
import dev.lbuddyboy.pcore.user.MineUser;
import dev.lbuddyboy.pcore.user.model.CoinFlipInfo;
import dev.lbuddyboy.pcore.util.Config;
import dev.lbuddyboy.pcore.util.Cooldown;
import dev.lbuddyboy.pcore.util.IModule;
import dev.lbuddyboy.pcore.util.gson.GSONUtils;
import dev.lbuddyboy.pcore.util.menu.PagedGUISettings;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Getter
public class CoinFlipHandler implements IModule {

    private final Map<UUID, CoinFlip> coinFlips;
    private final Cooldown cooldown;
    private Config config;
    private PagedGUISettings guiSettings;

    public CoinFlipHandler() {
        this.coinFlips = new ConcurrentHashMap<>();
        this.cooldown = new Cooldown();
    }

    @Override
    public void load(pCore plugin) {
        this.config = new Config(pCore.getInstance(), "coin-flip");
        this.guiSettings = new PagedGUISettings(config);

        pCore.getInstance().getCommandManager().registerCommand(new CoinFlipCommand());
        pCore.getInstance().getServer().getPluginManager().registerEvents(new CoinFlipListener(), plugin);
    }

    @Override
    public void unload(pCore plugin) {
        this.coinFlips.values().forEach(CoinFlip::refund);
    }

    @Override
    public void reload() {
        unload(pCore.getInstance());
        load(pCore.getInstance());
    }

    @Override
    public void save() {
        this.cooldown.cleanUp();
    }

    public CoinFlip createCoinFlip(Player sender, double amount, EconomyType economy, CoinFlipType side) {
        return this.coinFlips.put(sender.getUniqueId(), new CoinFlip(sender.getUniqueId(), side, amount, economy, System.currentTimeMillis()));
    }

    public CoinFlipInfo getCoinFlipInfo(UUID uuid) {
        MineUser user = pCore.getInstance().getMineUserHandler().getMineUser(uuid);

        return user.getCoinFlipInfo();
    }

    public void saveCoinFlipInfo(UUID uuid, CoinFlipInfo info) {
        MineUser user = pCore.getInstance().getMineUserHandler().getMineUser(uuid);

        user.setCoinFlipInfo(info);
    }

    public int getWins(UUID uuid) {
        return getCoinFlipInfo(uuid).getWins();
    }

    public int getLosses(UUID uuid) {
        return getCoinFlipInfo(uuid).getLosses();
    }

    public double getProfit(UUID uuid) {
        return getCoinFlipInfo(uuid).getProfit();
    }

    public double incrementProfit(UUID uuid, double amount) {
        CoinFlipInfo info = getCoinFlipInfo(uuid);

        info.setProfit(info.getProfit() + amount);
        saveCoinFlipInfo(uuid, info);

        return info.getProfit();
    }

    public double decrementProfit(UUID uuid, double amount) {
        CoinFlipInfo info = getCoinFlipInfo(uuid);

        info.setProfit(info.getProfit() - amount);
        saveCoinFlipInfo(uuid, info);

        return info.getProfit();
    }

    public int incrementWins(UUID uuid) {
        CoinFlipInfo info = getCoinFlipInfo(uuid);

        info.setWins(info.getWins() + 1);
        saveCoinFlipInfo(uuid, info);

        return info.getWins();
    }

    public int incrementLosses(UUID uuid) {
        CoinFlipInfo info = getCoinFlipInfo(uuid);

        info.setLosses(info.getLosses() + 1);
        saveCoinFlipInfo(uuid, info);

        return info.getLosses();
    }

}
