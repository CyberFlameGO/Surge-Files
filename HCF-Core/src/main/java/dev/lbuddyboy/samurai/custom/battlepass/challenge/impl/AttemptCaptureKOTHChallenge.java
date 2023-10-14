package dev.lbuddyboy.samurai.custom.battlepass.challenge.impl;

import dev.lbuddyboy.samurai.custom.battlepass.challenge.Challenge;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;

public class AttemptCaptureKOTHChallenge extends Challenge {

    public AttemptCaptureKOTHChallenge(String id, String name, int experience, boolean daily) {
        super(id, name, experience, daily);
    }

    @Override
    public Type getAbstractType() {
        return AttemptCaptureKOTHChallenge.class;
    }

    @Override
    public String getText() {
        return "Attempt to capture a KOTH";
    }

    @Override
    public boolean meetsCompletionCriteria(Player player) {
        return Samurai.getInstance().getBattlePassHandler().getProgress(player.getUniqueId()).isAttemptCaptureKoth();
    }

}
