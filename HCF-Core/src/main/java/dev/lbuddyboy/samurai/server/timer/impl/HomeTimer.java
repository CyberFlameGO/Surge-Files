package dev.lbuddyboy.samurai.server.timer.impl;

import dev.lbuddyboy.samurai.server.ServerHandler;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.object.ScoreFunction;
import dev.lbuddyboy.samurai.server.timer.PlayerTimer;
import org.bukkit.entity.Player;

public class HomeTimer extends PlayerTimer {

    @Override
    public String getName() {
        return "home";
    }

    @Override
    public String getDisplayName() {
        return CC.BLUE + "Home";
    }

    @Override
    public String getRemainingString(Player player) {
        return getHomeScore(player);
    }

    @Override
    public long getDuration(Player player) {
        return 0;
    }

    @Override
    public boolean onCooldown(Player player) {
        return getHomeScore(player) != null;
    }

    @Override
    public void activate(Player player) {
    }

    @Override
    public void deactivate(Player player) {
    }

    public String getHomeScore(Player player) {
        if (ServerHandler.getHomeTimer().containsKey(player.getName()) && ServerHandler.getHomeTimer().get(player.getName()) >= System.currentTimeMillis()) {
            float diff = ServerHandler.getHomeTimer().get(player.getName()) - System.currentTimeMillis();

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return (null);
    }

}
