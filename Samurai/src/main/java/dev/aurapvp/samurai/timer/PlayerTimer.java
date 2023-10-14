package dev.aurapvp.samurai.timer;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.util.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class PlayerTimer implements Listener {

    public PlayerTimer() {
        Bukkit.getPluginManager().registerEvents(this, Samurai.getInstance());
    }

    public abstract String getName();
    public abstract String getDisplayName();
    public abstract long getDuration(Player player);
    public abstract Cooldown getCooldown();
    public abstract void activate(Player player);
    public abstract void deactivate(Player player);

}
