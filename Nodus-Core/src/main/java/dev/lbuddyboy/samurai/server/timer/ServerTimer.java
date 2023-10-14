package dev.lbuddyboy.samurai.server.timer;

import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.TimeUtils;
import dev.lbuddyboy.samurai.util.object.Config;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.boss.CraftBossBar;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ServerTimer {

    private String name, displayName, context;
    private long sentAt, duration;
    private List<ScheduledTask> tasks = new ArrayList<>();
    private BossBar bossBar;

    public ServerTimer(String name, String displayName, String context, long sentAt, long duration) {
        this.name = name;
        this.displayName = displayName;
        this.context = context;
        this.sentAt = sentAt;
        this.duration = duration;
        this.bossBar = Bukkit.createBossBar(this.name, BarColor.BLUE, BarStyle.SOLID);
        this.bossBar.setTitle(CC.translate(this.context
                .replaceAll("%timer%", this.displayName)
                .replaceAll("%time-left%", TimeUtils.formatLongIntoHHMMSS((int) this.getExpiry() / 1000))));
    }

    public boolean isOver() {
        return getExpiry() <= 0;
    }

    public long getExpiry() {
        return (this.sentAt + this.duration) - System.currentTimeMillis();
    }

    public ServerTimer addReminder(String message, long timeLeft) {
        tasks.add(new ScheduledTask(
                ScheduledTask.Task.BROADCAST,
                message, null, timeLeft, false

        ));
        return this;
    }

    public ServerTimer addReminderTitle(String title, String message, long timeLeft) {
        tasks.add(new ScheduledTask(
                ScheduledTask.Task.TITLE,
                title, message, timeLeft, false

        ));
        return this;
    }

}
