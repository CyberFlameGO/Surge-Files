package dev.lbuddyboy.samurai.custom.battlepass.challenge.impl;

import dev.lbuddyboy.samurai.custom.battlepass.challenge.Challenge;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.Formats;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;

@Getter
public class KillstreakChallenge extends Challenge {

    private int streak;

    public KillstreakChallenge(String id, String name, int experience, boolean daily, int streak) {
        super(id, name, experience, daily);

        this.streak = streak;
    }

    @Override
    public Type getAbstractType() {
        return KillstreakChallenge.class;
    }

    @Override
    public String getText() {
        return "Reach a " + streak + " killstreak";
    }

    @Override
    public boolean hasStarted(Player player) {
        return Samurai.getInstance().getKillstreakMap().getKillstreak(player.getUniqueId()) > 0;
    }

    @Override
    public boolean meetsCompletionCriteria(Player player) {
        return Samurai.getInstance().getKillstreakMap().getKillstreak(player.getUniqueId()) >= streak;
    }

    @Override
    public String getProgressText(Player player) {
        int amount = Samurai.getInstance().getKillstreakMap().getKillstreak(player.getUniqueId());
        int remaining = streak - amount;
        return "You need to kill " + Formats.formatNumber(remaining) + " more player" + (remaining <= 1 ? "" : "s") + " to reach a killstreak of " + streak + ".";
    }

}
