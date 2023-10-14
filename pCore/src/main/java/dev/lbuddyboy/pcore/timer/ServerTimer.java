package dev.lbuddyboy.pcore.timer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.inventivetalent.bossbar.BossBar;

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
    }

    public boolean isOver() {
        return getExpiry() <= 0;
    }

    public long getExpiry() {
        return (this.sentAt + this.duration) - System.currentTimeMillis();
    }

}
