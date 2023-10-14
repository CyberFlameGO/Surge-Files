package dev.lbuddyboy.pcore.essential.trade;

import dev.lbuddyboy.pcore.essential.trade.command.TradeCommand;
import dev.lbuddyboy.pcore.essential.trade.listener.TradeListener;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.util.IModule;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

@Getter
public class TradeHandler implements IModule {

    private final List<Trade> trades;
    private final Map<UUID, TradeRequest> requests;

    public TradeHandler() {
        this.trades = new ArrayList<>();
        this.requests = new HashMap<>();
    }

    @Override
    public void load(pCore plugin) {
        plugin.getCommandManager().registerCommand(new TradeCommand());
        plugin.getServer().getPluginManager().registerEvents(new TradeListener(), plugin);
    }

    @Override
    public void unload(pCore plugin) {
        for (Trade trade : trades) {
            trade.refund();
        }
    }

    @Override
    public void reload() {

    }

    public Trade findTrade(UUID uuid) {
        for (Trade trade : this.trades) {
            if (trade.getSender() == uuid) return trade;
            if (trade.getTarget() == uuid) return trade;
        }

        return null;
    }

}
