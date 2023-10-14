package dev.drawethree.xprison.autosell.model;

import dev.drawethree.xprison.autosell.XPrisonAutoSell;
import dev.drawethree.xprison.utils.player.PlayerUtils;
import me.lucko.helper.Schedulers;
import me.lucko.helper.scheduler.Task;
import me.lucko.helper.utils.Players;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public final class AutoSellBroadcastTask implements Runnable {

    private final XPrisonAutoSell plugin;
    private Task task;

    public AutoSellBroadcastTask(XPrisonAutoSell plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {

        for (Player p : Players.all()) {

            double lastEarnings = this.plugin.getManager().getPlayerLastEarnings(p);

            if (lastEarnings <= 0.0) {
                continue;
            }

            long lastItems = this.plugin.getManager().getPlayerLastItemsAmount(p);
            long lastBlocks = this.plugin.getManager().getPlayerLastBlocks(p);
            double lastGems = this.plugin.getManager().getPlayerLastGems(p);
            double lastTokens = this.plugin.getManager().getPlayerLastTokens(p);

            this.sendAutoSellBroadcastMessage(p, lastEarnings, lastItems, lastBlocks, lastGems, lastTokens);
        }

        this.plugin.getManager().resetLastEarnings();
        this.plugin.getManager().resetLastItems();
        this.plugin.getManager().resetLastBlocks();

    }

    private void sendAutoSellBroadcastMessage(Player p, double lastEarnings, long lastItems, long lastBlocks, double lastGems, double lastTokens) {
        for (String s : this.plugin.getAutoSellConfig().getAutoSellBroadcastMessage()) {
            PlayerUtils.sendMessage(p, s.replace("%money%", String.format("%,.0f", lastEarnings))
                    .replace("%blocks%", String.format("%,d", lastBlocks))
                    .replace("%gems%", String.format("%,.0f", lastGems))
                    .replace("%tokens%", String.format("%,.0f", lastTokens))
                    .replace("%items%", String.format("%,d", lastItems)));
        }
    }


    public void start() {
        this.task = Schedulers.async().runRepeating(this, this.plugin.getAutoSellConfig().getAutoSellBroadcastTime(), TimeUnit.SECONDS, this.plugin.getAutoSellConfig().getAutoSellBroadcastTime(), TimeUnit.SECONDS);
    }

    public void stop() {
        if (this.task != null) {
            this.task.stop();
        }
    }
}
