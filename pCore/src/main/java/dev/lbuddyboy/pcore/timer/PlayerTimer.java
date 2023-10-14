package dev.lbuddyboy.pcore.timer;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.util.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class PlayerTimer implements Listener {

    public PlayerTimer() {
        Bukkit.getPluginManager().registerEvents(this, pCore.getInstance());
    }

    public abstract String getName();
    public abstract String getDisplayName();
    public abstract long getDuration(Player player);
    public abstract Cooldown getCooldown();
    public abstract void activate(Player player);
    public abstract void deactivate(Player player);

}
