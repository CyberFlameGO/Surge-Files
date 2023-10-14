package dev.lbuddyboy.samurai.custom.battlepass.challenge.impl;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.battlepass.challenge.Challenge;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;

public class UsePetChallenge extends Challenge {

    public UsePetChallenge(String id, String name, int experience, boolean daily) {
        super(id, name, experience, daily);
    }

    @Override
    public Type getAbstractType() {
        return UsePetChallenge.class;
    }

    @Override
    public String getText() {
        return "Activate a pet";
    }

    @Override
    public boolean meetsCompletionCriteria(Player player) {
        return Samurai.getInstance().getBattlePassHandler().getProgress(player.getUniqueId()).isUsePet();
    }

}
