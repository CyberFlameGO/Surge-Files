package dev.aurapvp.samurai.timer.thread;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.timer.PlayerTimer;
import dev.aurapvp.samurai.timer.ScheduledTask;
import dev.aurapvp.samurai.timer.ServerTimer;
import dev.aurapvp.samurai.util.*;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerTimerThread extends Thread {

    private long lastErrored = System.currentTimeMillis();

    @Override
    public void run() {
        while (Samurai.getInstance().isEnabled()) {
            try {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    List<PlayerTimer> timers = Samurai.getInstance().getTimerHandler().getPlayerTimers(player);

                    if (timers.isEmpty()) continue;
                    List<String> mapped = timers.stream().map(timer -> timer.getDisplayName() + "&7: &f" + timer.getCooldown().getRemaining(player) + "&7").collect(Collectors.toList());

                    ActionBarAPI.sendActionBar(player, CC.translate(StringUtils.join(mapped)));
                }
            } catch (Exception e) {
                if (lastErrored + 5_000L > System.currentTimeMillis()) continue;
                lastErrored = System.currentTimeMillis();
                e.printStackTrace();
            }

            try {
                Thread.sleep(400L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
