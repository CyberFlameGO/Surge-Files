package dev.lbuddyboy.samurai.custom.battlepass.challenge.impl;

import dev.lbuddyboy.samurai.custom.battlepass.challenge.Challenge;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.Formats;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;

public class ArcherTagsChallenge extends Challenge {

    private int tags;

    public ArcherTagsChallenge(String id, String name, int experience, boolean daily, int tags) {
        super(id, name, experience, daily);

        this.tags = tags;
    }

    @Override
    public Type getAbstractType() {
        return ArcherTagsChallenge.class;
    }

    @Override
    public String getText() {
        return "Archer tag " + Formats.formatNumber(tags) + " players";
    }

    @Override
    public boolean hasStarted(Player player) {
        return Samurai.getInstance().getBattlePassHandler().getProgress(player.getUniqueId()).getArcherTags() > 0;
    }

    @Override
    public boolean meetsCompletionCriteria(Player player) {
        return Samurai.getInstance().getBattlePassHandler().getProgress(player.getUniqueId()).getArcherTags() >= tags;
    }

    @Override
    public String getProgressText(Player player) {
        int remaining = tags - Samurai.getInstance().getBattlePassHandler().getProgress(player.getUniqueId()).getArcherTags();
        return "You need to tag " + Formats.formatNumber(remaining) + " more player" + (remaining <= 1 ? "" : "s") + ".";
    }

}
