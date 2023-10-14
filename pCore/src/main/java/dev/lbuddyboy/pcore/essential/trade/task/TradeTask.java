package dev.lbuddyboy.pcore.essential.trade.task;

import dev.lbuddyboy.pcore.essential.trade.Trade;
import dev.lbuddyboy.pcore.essential.trade.inventory.TradeInventory;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

@AllArgsConstructor
public class TradeTask extends BukkitRunnable {

    private Trade trade;

    @Override
    public void run() {
        trade.setCountdown(trade.getCountdown() - 1);
        TradeInventory.updateInventory(this.trade);

        for (UUID uuid : trade.getInventories().keySet()) {
            Player player = Bukkit.getPlayer(uuid);

            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.5f, 1.5f);
        }

        if (trade.getCountdown() <= 0) {
            trade.end();
            cancel();
        }
    }

}
