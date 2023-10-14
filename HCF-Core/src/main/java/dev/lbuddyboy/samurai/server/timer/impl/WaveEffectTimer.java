package dev.lbuddyboy.samurai.server.timer.impl;

import dev.lbuddyboy.samurai.pvpclasses.pvpclasses.BardClass;
import dev.lbuddyboy.samurai.pvpclasses.pvpclasses.WaveRiderClass;
import dev.lbuddyboy.samurai.server.timer.PlayerTimer;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.object.ScoreFunction;
import org.bukkit.entity.Player;

public class WaveEffectTimer extends PlayerTimer {

    @Override
    public String getName() {
        return "wave_effect";
    }

    @Override
    public String getDisplayName() {
        return CC.GREEN + "Wave Effect";
    }

    @Override
    public String getRemainingString(Player player) {
        return getBardEffectScore(player);
    }

    @Override
    public long getDuration(Player player) {
        return 30_000L;
    }

    @Override
    public boolean onCooldown(Player player) {
        return getBardEffectScore(player) != null;
    }

    @Override
    public void activate(Player player) {

    }

    @Override
    public void deactivate(Player player) {

    }

    public String getBardEffectScore(Player player) {
        if (WaveRiderClass.getLastEffectUsage().containsKey(player.getName()) && WaveRiderClass.getLastEffectUsage().get(player.getName()) >= System.currentTimeMillis()) {
            float diff = WaveRiderClass.getLastEffectUsage().get(player.getName()) - System.currentTimeMillis();

            if (diff > 0) {
                return (ScoreFunction.TIME_SIMPLE.apply(diff / 1000F));
            }
        }

        return (null);
    }

}
