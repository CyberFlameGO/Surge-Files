package dev.lbuddyboy.samurai.custom.deepdark.entity;

import dev.lbuddyboy.samurai.util.cooldown.Cooldown;
import org.bukkit.Location;

public abstract class DarkAbility {

    public abstract String getName();
    public abstract double getChance();
    public abstract void activate(Location location);

}
