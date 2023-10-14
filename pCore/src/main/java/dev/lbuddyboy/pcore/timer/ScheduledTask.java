package dev.lbuddyboy.pcore.timer;

import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.Tasks;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;

@AllArgsConstructor
@Data
public class ScheduledTask {

    private Task task;
    private String value;
    private long timeLeft;
    private boolean executed;

    public void execute() {
        Tasks.run(() -> {
            if (task == Task.COMMAND) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), this.value);
            } else {
                CC.broadcast(this.value);
            }
        });

        this.executed = true;
    }

    public enum Task {

        COMMAND, BROADCAST

    }

    public static ScheduledTask DEFAULT_TASK() {
        return new ScheduledTask(Task.COMMAND, "broadcast global %timer% will commence in %time-left%", 30_000L, false);
    }

}
