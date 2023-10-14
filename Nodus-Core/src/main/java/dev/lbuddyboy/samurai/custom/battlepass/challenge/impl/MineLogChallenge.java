package dev.lbuddyboy.samurai.custom.battlepass.challenge.impl;

import dev.lbuddyboy.samurai.custom.battlepass.challenge.Challenge;
import lombok.Getter;
import dev.lbuddyboy.samurai.util.Formats;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;

@Getter
public class MineLogChallenge extends Challenge {

    private int amount;

    public MineLogChallenge(String id, String text, int experience, boolean daily, int amount) {
        super(id, text, experience, daily);

        this.amount = amount;
    }

    public String getBlockName() {
        return "Logs";
    }

    public int getMined(Player player) {

        int amount = 0;

        amount += getProgress(player).getBlocksMined(Material.OAK_LOG, isDaily());

        return amount;
    }

    @Override
    public Type getAbstractType() {
        return MineLogChallenge.class;
    }

    @Override
    public String getText() {
        return "Mine " + Formats.formatNumber(amount) + " " + getBlockName();
    }

    @Override
    public boolean hasStarted(Player player) {
        return getMined(player) > 0;
    }

    @Override
    public boolean meetsCompletionCriteria(Player player) {
        return getMined(player) >= amount;
    }

    @Override
    public String getProgressText(Player player) {
        int remaining = amount - getMined(player);
        return "You need to mine " + Formats.formatNumber(remaining) + " more " + getBlockName() + (remaining <= 1 ? "" : "s") + ".";
    }

}
