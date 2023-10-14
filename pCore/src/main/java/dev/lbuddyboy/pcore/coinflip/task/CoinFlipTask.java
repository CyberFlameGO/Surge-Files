package dev.lbuddyboy.pcore.coinflip.task;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.api.event.CoinFlipEndEvent;
import dev.lbuddyboy.pcore.coinflip.CoinFlip;
import dev.lbuddyboy.pcore.coinflip.CoinFlipType;
import dev.lbuddyboy.pcore.coinflip.menu.WagerMenu;
import dev.lbuddyboy.pcore.util.Tasks;
import dev.lbuddyboy.pcore.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@AllArgsConstructor
public class CoinFlipTask extends BukkitRunnable {

    private CoinFlip cf;
    private WagerMenu menu;

    @Override
    public void run() {
        if (cf.getCountdown() > 0) {
            cf.setCountdown(cf.getCountdown() - 1);

            cf.getPlayers().forEach(player -> {
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 2.0f, 2.0f);
                if (Menu.openedMenus.containsKey(player.getName()) && Menu.openedMenus.get(player.getName()) == menu) {
                    this.menu.update(player, new WagerMenu.CountdownButton(cf));
                }
            });
            if (cf.getCountdown() <= 0) {
                cancel();
                new CoinFlipTask(this.cf, this.menu).runTaskTimerAsynchronously(pCore.getInstance(), 3, 3);
            }
            return;
        }

        if (cf.getTimesDone() >= 25) {
            int chance = ThreadLocalRandom.current().nextInt(10);
            CoinFlipType winningSide = chance <= 5 ? CoinFlipType.TAILS : CoinFlipType.HEADS;
            UUID winner = cf.getSide() == winningSide ? cf.getSender() : cf.getChallenger();
            UUID loser = cf.getSide() == winningSide ? cf.getChallenger() : cf.getSender();

            cf.setWinningSide(winningSide);
            cf.reward(winner, loser);

            Bukkit.getPluginManager().callEvent(new CoinFlipEndEvent(cf.getSender(), cf.getChallenger(), winner));

            cf.getPlayers().forEach(player -> {
                update(player);
                cancel();

                Tasks.runLater(player::closeInventory, 20 * 3);
            });

            return;
        }

        cf.setWinningSide(cf.getWinningSide() == null || cf.getWinningSide() == CoinFlipType.HEADS ? CoinFlipType.TAILS : CoinFlipType.HEADS);
        cf.setTimesDone(cf.getTimesDone() + 1);
        cf.getPlayers().forEach(player -> {
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 2.0f, 2.0f);
            update(player);
        });
    }

    public void update(Player player) {
        if (Menu.openedMenus.containsKey(player.getName()) && Menu.openedMenus.get(player.getName()) == menu) {
            this.menu.update(player, new WagerMenu.RollButton(cf));
        }
    }

}
