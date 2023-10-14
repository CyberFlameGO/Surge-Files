package dev.lbuddyboy.samurai.server.timer.thread;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.server.timer.ScheduledTask;
import dev.lbuddyboy.samurai.server.timer.ServerTimer;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ServerTimerThread extends Thread {

    private long lastErrored = System.currentTimeMillis();
    private long lastTimerUpdate = -1;

    public ServerTimerThread() {
        super("Server Timer - Thread");
        this.setDaemon(true);
    }

    @Override
    public void run() {
        while (Samurai.getInstance().isEnabled()) {
            try {
                List<ServerTimer> timers = new ArrayList<>(Samurai.getInstance().getTimerHandler().getServerTimers().values());
                ServerTimer timer = Samurai.getInstance().getTimerHandler().getCurrentlyDisplayed();

                if (timer == null && !timers.isEmpty()) {
                    Samurai.getInstance().getTimerHandler().setCurrentlyDisplayed(timers.get(0));
                    this.lastTimerUpdate = System.currentTimeMillis();
                } else if (this.lastTimerUpdate + 10_000L < System.currentTimeMillis()) {
                    if (timer != null) {
                        if (timer.getBossBar() != null) {
                            timer.getBossBar().removeAll();
                        }
                    }
                    this.lastTimerUpdate = System.currentTimeMillis();
                    int next = timers.indexOf(timer) + 1;

                    if (next >= timers.size()) {
                        if (timers.isEmpty()) continue;
                        Samurai.getInstance().getTimerHandler().setCurrentlyDisplayed(timers.get(0));
                        continue;
                    }

                    Samurai.getInstance().getTimerHandler().setCurrentlyDisplayed(timers.get(next));
                    timer = timers.get(next);
                }

                if (timer != null) {
                    if (timer.isOver()) {
                        timer.getBossBar().removeAll();
                        Bukkit.removeBossBar(new NamespacedKey(Samurai.getInstance(), timer.getName()));

                        Samurai.getInstance().getTimerHandler().getServerTimers().remove(timer.getName());
                        Samurai.getInstance().getTimerHandler().setCurrentlyDisplayed(null);
                        continue;
                    }

                    for (ScheduledTask task : timer.getTasks()) {
                        if (task.getTimeLeft() >= timer.getExpiry() && !task.isExecuted()) {
                            task.execute();
                        }
                    }

                    if (timer.getBossBar() == null) continue;

                    float percent = (float) timer.getExpiry() / timer.getDuration();
                    timer.getBossBar().setTitle(CC.translate(timer.getContext()
                            .replaceAll("%timer%", timer.getDisplayName())
                            .replaceAll("%time-left%", TimeUtils.formatLongIntoHHMMSS((int) timer.getExpiry() / 1000))));
                    switch (timer.getName()) {
                        case "SOTW" -> timer.getBossBar().setColor(BarColor.YELLOW);
                        case "Double-Airdrops", "Triple-Keys" -> timer.getBossBar().setColor(BarColor.BLUE);
                        default -> timer.getBossBar().setColor(BarColor.WHITE);
                    }

                    timer.getBossBar().setProgress(percent);

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        timer.getBossBar().addPlayer(player);
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
