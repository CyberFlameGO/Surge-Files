package dev.lbuddyboy.samurai.server.timer.impl;

import dev.lbuddyboy.samurai.server.SpawnTagHandler;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.object.ScoreFunction;
import dev.lbuddyboy.samurai.server.timer.PlayerTimer;
import org.bukkit.entity.Player;

public class CombatTagTimer extends PlayerTimer {

    @Override
    public String getName() {
        return "combat_tag";
    }

    @Override
    public String getDisplayName() {
        return CC.RED + "Combat Tag";
    }

    @Override
    public String getRemainingString(Player player) {
        return getSpawnTagScore(player);
    }

    @Override
    public long getDuration(Player player) {
        return 30_000L;
    }

    @Override
    public boolean onCooldown(Player player) {
        return getSpawnTagScore(player) != null;
    }

    @Override
    public void activate(Player player) {

    }

    @Override
    public void deactivate(Player player) {

    }

    public String getSpawnTagScore(Player player) {
        if (SpawnTagHandler.isTagged(player)) {
            float diff = SpawnTagHandler.getTag(player);

            if (diff >= 0) {
                return (ScoreFunction.TIME_SIMPLE.apply(diff / 1000F));
            }
        }

        return (null);
    }

}
