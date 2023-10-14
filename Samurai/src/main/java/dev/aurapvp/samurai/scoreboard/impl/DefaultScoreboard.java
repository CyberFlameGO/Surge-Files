package dev.aurapvp.samurai.scoreboard.impl;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.economy.BankAccount;
import dev.aurapvp.samurai.economy.IEconomy;
import dev.aurapvp.samurai.scoreboard.SamuraiScoreboard;
import dev.aurapvp.samurai.scoreboard.ScoreboardLine;
import dev.aurapvp.samurai.scoreboard.ScoreboardTitle;
import dev.aurapvp.samurai.util.Config;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DefaultScoreboard extends SamuraiScoreboard {

    private final Config config;
    private final ScoreboardTitle title;
    private final List<ScoreboardLine> lines = new ArrayList<>();

    public DefaultScoreboard() {
        this.config = new Config(Samurai.getInstance(), "default", Samurai.getInstance().getScoreboardHandler().getScoreboardDirectory());
        this.title = new ScoreboardTitle(this.config.getConfigurationSection("title"));
        for (String key : this.config.getConfigurationSection("lines").getKeys(false)) {
            this.lines.add(new ScoreboardLine(this.config.getConfigurationSection("lines." + key)));
        }
    }

    @Override
    public Config getFile() {
        return this.config;
    }

    @Override
    public ScoreboardTitle getTitle() {
        return this.title;
    }

    @Override
    public List<ScoreboardLine> getLines() {
        return this.lines;
    }

    @Override
    public List<String> translateLines(List<String> lines, Player player) {
        List<String> replacement = new ArrayList<>();
        IEconomy economy = Samurai.getInstance().getEconomyHandler().getEconomy();
        BankAccount account = economy.getBankAccount(player.getUniqueId(), true);
        SimpleDateFormat sdf = new SimpleDateFormat();

        sdf.setTimeZone(TimeZone.getTimeZone("EST"));

        for (String line : lines) {
            replacement.add(line
                    .replaceAll("%player%", player.getName())
                    .replaceAll("%balance%", economy.formatMoney(account.getMoney()))
                    .replaceAll("%coins%", economy.formatMoney(account.getCoins()))
                    .replaceAll("%online%", "" + Bukkit.getOnlinePlayers().size())
                    .replaceAll("%date%", sdf.format(new Date()))
            );
        }

        return replacement;
    }

    @Override
    public boolean qualifies(Player player) {
        return true;
    }
}
