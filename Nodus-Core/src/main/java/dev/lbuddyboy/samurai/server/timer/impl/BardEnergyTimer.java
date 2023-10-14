package dev.lbuddyboy.samurai.server.timer.impl;

import dev.lbuddyboy.samurai.pvpclasses.pvpclasses.BardClass;
import dev.lbuddyboy.samurai.server.timer.PlayerTimer;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.entity.Player;

public class BardEnergyTimer extends PlayerTimer {

    @Override
    public String getName() {
        return "bard_energy";
    }

    @Override
    public String getDisplayName() {
        return CC.AQUA + "Bard Energy";
    }

    @Override
    public String getRemainingString(Player player) {
        return getBardEnergyScore(player);
    }

    @Override
    public long getDuration(Player player) {
        return 30_000L;
    }

    @Override
    public boolean onCooldown(Player player) {
        return getBardEnergyScore(player) != null;
    }

    @Override
    public void activate(Player player) {

    }

    @Override
    public void deactivate(Player player) {

    }

    public String getBardEnergyScore(Player player) {
        if (BardClass.getEnergy().containsKey(player.getName())) {
            float energy = BardClass.getEnergy().get(player.getName());

            if (energy > 0) {
                // No function here, as it's a "raw" value.
                return (String.valueOf(BardClass.getEnergy().get(player.getName())));
            }
        }

        return (null);
    }

}
