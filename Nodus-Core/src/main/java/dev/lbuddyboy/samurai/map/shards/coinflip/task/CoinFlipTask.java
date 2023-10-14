package dev.lbuddyboy.samurai.map.shards.coinflip.task;

import dev.lbuddyboy.flash.util.bukkit.Tasks;
import dev.lbuddyboy.flash.util.menu.Menu;
import dev.lbuddyboy.samurai.map.shards.coinflip.CoinFlipType;
import dev.lbuddyboy.samurai.map.shards.coinflip.event.CoinFlipEndEvent;
import dev.lbuddyboy.samurai.map.shards.coinflip.menu.WagerMenu;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.shards.coinflip.CoinFlip;
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
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f, 2.0f);
                if (Menu.openedMenus.containsKey(player.getName()) && Menu.openedMenus.get(player.getName()) == menu) {
                    this.menu.update(player, new WagerMenu.CountdownButton(cf));
                }
            });
            if (cf.getCountdown() <= 0) {
                cancel();
                new CoinFlipTask(this.cf, this.menu).runTaskTimerAsynchronously(Samurai.getInstance(), 3, 3);
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

            Tasks.run(() -> Bukkit.getPluginManager().callEvent(new CoinFlipEndEvent(cf.getSender(), cf.getChallenger(), winner)));

            cf.getPlayers().forEach(player -> {
                update(player);

                Tasks.runLater(player::closeInventory, 20 * 3);
            });

            cancel();
            return;
        }

        cf.setWinningSide(cf.getWinningSide() == null || cf.getWinningSide() == CoinFlipType.HEADS ? CoinFlipType.TAILS : CoinFlipType.HEADS);
        cf.setTimesDone(cf.getTimesDone() + 1);
        cf.getPlayers().forEach(player -> {
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2.0f, 2.0f);
            update(player);
        });
    }

    public void update(Player player) {
        if (Menu.openedMenus.containsKey(player.getName()) && Menu.openedMenus.get(player.getName()) == menu) {
            this.menu.update(player, new WagerMenu.RollButton(cf));
        }
    }

}
