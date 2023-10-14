package dev.lbuddyboy.samurai.custom.battlepass.challenge.impl.visit;

import dev.lbuddyboy.samurai.custom.battlepass.challenge.Challenge;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;

public class VisitNetherChallenge extends Challenge {

    public VisitNetherChallenge(String id, String name, int experience, boolean daily) {
        super(id, name, experience, daily);
    }

    @Override
    public Type getAbstractType() {
        return VisitNetherChallenge.class;
    }

    @Override
    public String getText() {
        return "Visit the Nether";
    }

    @Override
    public boolean meetsCompletionCriteria(Player player) {
        return Samurai.getInstance().getBattlePassHandler().getProgress(player.getUniqueId()).isVisitedNether();
    }

}
