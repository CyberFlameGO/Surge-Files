package dev.lbuddyboy.samurai.server.timer.impl;

import dev.lbuddyboy.samurai.server.ServerHandler;
import dev.lbuddyboy.samurai.server.timer.PlayerTimer;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.object.Logout;
import dev.lbuddyboy.samurai.util.object.ScoreFunction;
import org.bukkit.entity.Player;

public class LogoutTimer extends PlayerTimer {

    @Override
    public String getName() {
        return "logout";
    }

    @Override
    public String getDisplayName() {
        return CC.DARK_RED + "Logout";
    }

    @Override
    public String getRemainingString(Player player) {
        return getLogoutScore(player);
    }

    @Override
    public long getDuration(Player player) {
        return 30_000L;
    }

    @Override
    public boolean onCooldown(Player player) {
        return getLogoutScore(player) != null;
    }

    @Override
    public void activate(Player player) {

    }

    @Override
    public void deactivate(Player player) {

    }
    public String getLogoutScore(Player player) {
        Logout logout = ServerHandler.getTasks().get(player.getName());

        if (logout != null) {
            float diff = logout.getLogoutTime() - System.currentTimeMillis();

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return null;
    }

}
