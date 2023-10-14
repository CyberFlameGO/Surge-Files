package dev.lbuddyboy.pcore.util;

import dev.lbuddyboy.pcore.pCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Tasks {

    public static void run(Callable callable) {
        Bukkit.getServer().getScheduler().runTask(pCore.getInstance(), callable::call);
    }

    public static void runAsync(Callable callable) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(pCore.getInstance(), callable::call);
    }

    public static void runLater(Callable callable, long delay) {
        Bukkit.getServer().getScheduler().runTaskLater(pCore.getInstance(), callable::call, delay);
    }

    public static void runAsyncLater(Callable callable, long delay) {
        Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(pCore.getInstance(), callable::call, delay);
    }

    public static void runTimer(Callable callable, long delay, long interval) {
        Bukkit.getServer().getScheduler().runTaskTimer(pCore.getInstance(), callable::call, delay, interval);
    }

    public static void runAsyncTimer(Callable callable, long delay, long interval) {
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(pCore.getInstance(), callable::call, delay, interval);
    }

    public static void delayed(Callable callable) {
        runLater(callable, 10);
    }

    public static void delayedAsync(Callable callable) {
        runAsyncLater(callable, 10);
    }

    public interface Callable {
        void call();
    }

    public interface Callback {
        void call(Player player);
    }

}
