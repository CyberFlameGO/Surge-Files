package dev.lbuddyboy.samurai.server.timer.impl;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.object.ScoreFunction;
import dev.lbuddyboy.samurai.server.timer.PlayerTimer;
import org.bukkit.entity.Player;

public class InvincibilityTimer extends PlayerTimer {

    @Override
    public String getName() {
        return "invincibility";
    }

    @Override
    public String getDisplayName() {
        return CC.GREEN + "Invincibility";
    }

    @Override
    public String getRemainingString(Player player) {
        return getPvPTimerScore(player);
    }

    @Override
    public long getDuration(Player player) {
        return 60_000L * 30;
    }

    @Override
    public boolean onCooldown(Player player) {
        return getPvPTimerScore(player) != null;
    }

    @Override
    public void activate(Player player) {
    }

    @Override
    public void deactivate(Player player) {

    }

    public String getPvPTimerScore(Player player) {
        if (Samurai.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
            int secondsRemaining = Samurai.getInstance().getPvPTimerMap().getSecondsRemaining(player.getUniqueId());

            if (secondsRemaining >= 0) {
                return (ScoreFunction.TIME_SIMPLE.apply((float) secondsRemaining));
            }
        }

        return (null);
    }

}
