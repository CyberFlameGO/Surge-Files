package dev.lbuddyboy.samurai.server.timer.impl;

import dev.lbuddyboy.samurai.listener.GoldenAppleListener;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.object.ScoreFunction;
import dev.lbuddyboy.samurai.server.timer.PlayerTimer;
import org.bukkit.entity.Player;

public class CrappleTimer extends PlayerTimer {

    @Override
    public String getName() {
        return "crapple";
    }

    @Override
    public String getDisplayName() {
        return CC.YELLOW + "Crapple";
    }

    @Override
    public String getRemainingString(Player player) {
        return getAppleScore(player);
    }

    @Override
    public long getDuration(Player player) {
        return 15_000L;
    }

    @Override
    public boolean onCooldown(Player player) {
        return getAppleScore(player) != null;
    }

    @Override
    public void activate(Player player) {
    }

    @Override
    public void deactivate(Player player) {
    }

    public String getAppleScore(Player player) {
        if (GoldenAppleListener.getCrappleCooldown().containsKey(player.getUniqueId()) && GoldenAppleListener.getCrappleCooldown().get(player.getUniqueId()) >= System.currentTimeMillis()) {
            float diff = GoldenAppleListener.getCrappleCooldown().get(player.getUniqueId()) - System.currentTimeMillis();

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return (null);
    }

}
