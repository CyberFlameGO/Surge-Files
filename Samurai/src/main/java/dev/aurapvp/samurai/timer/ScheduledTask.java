package dev.aurapvp.samurai.timer;

import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.Tasks;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Data
public class ScheduledTask {

    private Task task;
    private String value, value2;
    private long timeLeft;
    private boolean executed;

    public void execute() {
        Tasks.run(() -> {
            if (task == Task.COMMAND) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), this.value);
            } else if (task == Task.BROADCAST) {
                CC.broadcast(this.value);
            } else if (task == Task.TITLE) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendTitle(CC.translate(this.value), CC.translate(this.value2));
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2.0f, 2.0f);
                }
            }
        });

        this.executed = true;
    }

    public enum Task {

        COMMAND, BROADCAST, TITLE

    }

    public static ScheduledTask DEFAULT_TASK() {
        return new ScheduledTask(Task.COMMAND, "broadcast global %timer% will commence in %time-left%", null, 30_000L, false);
    }

}
