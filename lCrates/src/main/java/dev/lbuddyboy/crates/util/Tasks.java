package dev.lbuddyboy.crates.util;

import dev.lbuddyboy.crates.lCrates;
import org.bukkit.Bukkit;

public class Tasks {

    public static void run(Callable callable) {
        Bukkit.getServer().getScheduler().runTask(lCrates.getInstance(), callable::call);
    }

    public static void runAsync(Callable callable) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(lCrates.getInstance(), callable::call);
    }

    public static void runLater(Callable callable, long delay) {
        Bukkit.getServer().getScheduler().runTaskLater(lCrates.getInstance(), callable::call, delay);
    }

    public static void runAsyncLater(Callable callable, long delay) {
        Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(lCrates.getInstance(), callable::call, delay);
    }

    public static void runTimer(Callable callable, long delay, long interval) {
        Bukkit.getServer().getScheduler().runTaskTimer(lCrates.getInstance(), callable::call, delay, interval);
    }

    public static void runAsyncTimer(Callable callable, long delay, long interval) {
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(lCrates.getInstance(), callable::call, delay, interval);
    }

}
