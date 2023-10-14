package dev.lbuddyboy.samurai.lunar;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class LunarHandler {

    public LunarHandler(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(new LunarListener(), plugin);
        /*Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new LunarWaypointTask(), 120, 120);*/
        Bukkit.getScheduler().runTaskTimer(plugin, new LunarNametagTask(), 120, 120);
    }

}