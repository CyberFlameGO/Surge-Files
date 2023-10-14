package dev.lbuddyboy.samurai.server.timer.impl;

import dev.lbuddyboy.samurai.server.pearl.EnderpearlCooldownHandler;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.object.ScoreFunction;
import dev.lbuddyboy.samurai.server.timer.PlayerTimer;
import org.bukkit.entity.Player;

public class PearlTimer extends PlayerTimer {

    @Override
    public String getName() {
        return "epearl";
    }

    @Override
    public String getDisplayName() {
        return CC.DARK_PURPLE + "Ender Pearl";
    }

    @Override
    public String getRemainingString(Player player) {
        return getEnderpearlScore(player);
    }

    @Override
    public long getDuration(Player player) {
        return 16_000L;
    }

    @Override
    public boolean onCooldown(Player player) {
        return getEnderpearlScore(player) != null;
    }

    @Override
    public void activate(Player player) {
    }

    @Override
    public void deactivate(Player player) {

    }

    public String getEnderpearlScore(Player player) {
        if (EnderpearlCooldownHandler.getEnderpearlCooldown().containsKey(player.getName()) && EnderpearlCooldownHandler.getEnderpearlCooldown().get(player.getName()) >= System.currentTimeMillis()) {
            float diff = EnderpearlCooldownHandler.getEnderpearlCooldown().get(player.getName()) - System.currentTimeMillis();

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return (null);
    }

}
