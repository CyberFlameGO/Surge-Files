package dev.lbuddyboy.pcore.timer.thread;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.timer.ScheduledTask;
import dev.lbuddyboy.pcore.timer.ServerTimer;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.TimeUtils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.inventivetalent.bossbar.BossBarAPI;

import java.util.ArrayList;
import java.util.List;

public class ServerTimerThread extends Thread {

    private long lastErrored = System.currentTimeMillis();
    private long lastTimerUpdate = -1;

    @Override
    public void run() {
        while (pCore.getInstance().isEnabled()) {
            try {
                List<ServerTimer> timers = new ArrayList<>(pCore.getInstance().getTimerHandler().getServerTimers().values());
                ServerTimer timer = pCore.getInstance().getTimerHandler().getCurrentlyDisplayed();

                if (timer == null && !timers.isEmpty()) {
                    pCore.getInstance().getTimerHandler().setCurrentlyDisplayed(timers.get(0));
                    this.lastTimerUpdate = System.currentTimeMillis();
                } else if (this.lastTimerUpdate + 10_000L < System.currentTimeMillis()) {
                    this.lastTimerUpdate = System.currentTimeMillis();
                    int next = timers.indexOf(timer) + 1;

                    if (next >= timers.size()) {
                        if (timers.isEmpty()) continue;
                        pCore.getInstance().getTimerHandler().setCurrentlyDisplayed(timers.get(0));
                        continue;
                    }

                    pCore.getInstance().getTimerHandler().setCurrentlyDisplayed(timers.get(next));
                }

                if (timer != null) {
                    if (timer.isOver()) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            BossBarAPI.removeAllBars(player);
                        }
                        pCore.getInstance().getTimerHandler().getServerTimers().remove(timer.getName());
                        pCore.getInstance().getTimerHandler().setCurrentlyDisplayed(null);
                        continue;
                    }

                    for (ScheduledTask task : timer.getTasks()) {
                        if (task.getTimeLeft() >= timer.getExpiry() && !task.isExecuted()) {
                            task.execute();
                        }
                    }

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        float percent = (float) timer.getExpiry() / timer.getDuration();
                        BossBarAPI.removeAllBars(player);
                        BossBarAPI.addBar(player, // The receiver of the BossBar
                                new TextComponent(CC.translate(timer.getContext()
                                        .replaceAll("%timer%", timer.getDisplayName())
                                        .replaceAll("%time-left%", TimeUtils.formatLongIntoHHMMSS((int) timer.getExpiry() / 1000)))), // Displayed message
                                BossBarAPI.Color.BLUE, // Color of the bar
                                BossBarAPI.Style.NOTCHED_20, // Bar style
                                percent);
                    }
                }

            } catch (Exception e) {
                if (lastErrored + 5_000L > System.currentTimeMillis()) continue;
                lastErrored = System.currentTimeMillis();
                e.printStackTrace();
            }

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
