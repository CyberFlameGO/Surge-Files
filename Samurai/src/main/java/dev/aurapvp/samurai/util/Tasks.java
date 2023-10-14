package dev.aurapvp.samurai.util;

import dev.aurapvp.samurai.Samurai;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Tasks {

    public static void run(Callable callable) {
        Bukkit.getServer().getScheduler().runTask(Samurai.getInstance(), callable::call);
    }

    public static void runAsync(Callable callable) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(Samurai.getInstance(), callable::call);
    }

    public static void runLater(Callable callable, long delay) {
        Bukkit.getServer().getScheduler().runTaskLater(Samurai.getInstance(), callable::call, delay);
    }

    public static void runAsyncLater(Callable callable, long delay) {
        Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(Samurai.getInstance(), callable::call, delay);
    }

    public static void runTimer(Callable callable, long delay, long interval) {
        Bukkit.getServer().getScheduler().runTaskTimer(Samurai.getInstance(), callable::call, delay, interval);
    }

    public static void runAsyncTimer(Callable callable, long delay, long interval) {
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(Samurai.getInstance(), callable::call, delay, interval);
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
