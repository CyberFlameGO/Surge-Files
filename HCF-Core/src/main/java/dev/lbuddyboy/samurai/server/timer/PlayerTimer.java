package dev.lbuddyboy.samurai.server.timer;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.cooldown.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class PlayerTimer implements Listener {

    public PlayerTimer() {
        Bukkit.getPluginManager().registerEvents(this, Samurai.getInstance());
    }

    public abstract String getName();
    public abstract String getDisplayName();
    public abstract String getRemainingString(Player player);
    public abstract long getDuration(Player player);
    public abstract boolean onCooldown(Player player);
    public Cooldown getCooldown() {
        return null;
    }
    public abstract void activate(Player player);
    public abstract void deactivate(Player player);

}
