package dev.lbuddyboy.samurai.custom.battlepass.challenge.impl;

import dev.lbuddyboy.samurai.custom.battlepass.challenge.Challenge;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.Formats;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;

@Getter
public class UsePartnerItemChallenge extends Challenge {

    private int uses;

    public UsePartnerItemChallenge(String id, String name, int experience, boolean daily, int uses) {
        super(id, name, experience, daily);

        this.uses = uses;
    }

    @Override
    public Type getAbstractType() {
        return UsePartnerItemChallenge.class;
    }

    @Override
    public String getText() {
        return "Use " + uses + " Ability Items";
    }

    @Override
    public boolean meetsCompletionCriteria(Player player) {
        return Samurai.getInstance().getBattlePassHandler().getProgress(player.getUniqueId()).getPartnerItemsUsed() >= uses;
    }

    @Override
    public String getProgressText(Player player) {
        int amount = Samurai.getInstance().getBattlePassHandler().getProgress(player.getUniqueId()).getPartnerItemsUsed();
        int remaining = uses - amount;
        return "You need to use " + Formats.formatNumber(remaining) + " more ability item" + (remaining <= 1 ? "" : "s") + ".";
    }

}
