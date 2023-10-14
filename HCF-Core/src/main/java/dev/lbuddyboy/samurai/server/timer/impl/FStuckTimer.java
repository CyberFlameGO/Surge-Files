package dev.lbuddyboy.samurai.server.timer.impl;

import dev.lbuddyboy.samurai.server.timer.PlayerTimer;
import dev.lbuddyboy.samurai.team.commands.TeamCommands;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.object.ScoreFunction;
import org.bukkit.entity.Player;

public class FStuckTimer extends PlayerTimer {

    @Override
    public String getName() {
        return "stuck_timer";
    }

    @Override
    public String getDisplayName() {
        return CC.DARK_RED + "Stuck";
    }

    @Override
    public String getRemainingString(Player player) {
        return getFStuckScore(player);
    }

    @Override
    public long getDuration(Player player) {
        return 30_000L;
    }

    @Override
    public boolean onCooldown(Player player) {
        return getFStuckScore(player) != null;
    }

    @Override
    public void activate(Player player) {

    }

    @Override
    public void deactivate(Player player) {

    }

    public String getFStuckScore(Player player) {
        if (TeamCommands.getWarping().containsKey(player.getName())) {
            float diff = TeamCommands.getWarping().get(player.getName()) - System.currentTimeMillis();

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return null;
    }

}
