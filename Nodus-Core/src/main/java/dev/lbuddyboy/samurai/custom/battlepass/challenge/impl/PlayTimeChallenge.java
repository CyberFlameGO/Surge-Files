package dev.lbuddyboy.samurai.custom.battlepass.challenge.impl;

import dev.lbuddyboy.samurai.custom.battlepass.challenge.Challenge;
import dev.lbuddyboy.samurai.util.TimeUtils;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;

@Getter
public class PlayTimeChallenge extends Challenge {

    private long playTime;

    public PlayTimeChallenge(String id, String name, int experience, boolean daily, long playTime) {
        super(id, name, experience, daily);

        this.playTime = playTime;
    }

    @Override
    public Type getAbstractType() {
        return PlayTimeChallenge.class;
    }

    @Override
    public String getText() {
        return "Reach " + TimeUtils.formatIntoDetailedString((int) (playTime / 1000)) + " of play time";
    }

    @Override
    public boolean hasStarted(Player player) {
        return true;
    }

    @Override
    public boolean meetsCompletionCriteria(Player player) {
        return Samurai.getInstance().getPlaytimeMap().getPlaytime(player.getUniqueId()) + Samurai.getInstance().getPlaytimeMap().getCurrentSession(player.getUniqueId()) >= playTime;
    }

    @Override
    public String getProgressText(Player player) {
        long playerTime = Samurai.getInstance().getPlaytimeMap().getPlaytime(player.getUniqueId()) + Samurai.getInstance().getPlaytimeMap().getCurrentSession(player.getUniqueId());
        long remaining = playTime - playerTime;
        return "You need to play for another " + TimeUtils.formatIntoDetailedString((int) (remaining / 1000L)) + ".";
    }

}
