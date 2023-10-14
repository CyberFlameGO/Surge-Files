package dev.lbuddyboy.samurai.server.timer.impl;

import dev.lbuddyboy.samurai.pvpclasses.pvpclasses.ArcherClass;
import dev.lbuddyboy.samurai.server.timer.PlayerTimer;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.object.ScoreFunction;
import org.bukkit.entity.Player;

public class ArcherMarkTimer extends PlayerTimer {

    @Override
    public String getName() {
        return "archer_mark";
    }

    @Override
    public String getDisplayName() {
        return CC.YELLOW + "Archer Mark";
    }

    @Override
    public String getRemainingString(Player player) {
        return getArcherMarkScore(player);
    }

    @Override
    public long getDuration(Player player) {
        return 30_000L;
    }

    @Override
    public boolean onCooldown(Player player) {
        return getArcherMarkScore(player) != null;
    }

    @Override
    public void activate(Player player) {

    }

    @Override
    public void deactivate(Player player) {

    }

    public String getArcherMarkScore(Player player) {
        if (ArcherClass.isMarked(player)) {
            long diff = ArcherClass.getMarkedPlayers().get(player.getName()) - System.currentTimeMillis();

            if (diff > 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return (null);
    }

}
