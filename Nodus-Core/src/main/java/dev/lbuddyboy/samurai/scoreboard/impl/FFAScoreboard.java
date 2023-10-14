package dev.lbuddyboy.samurai.scoreboard.impl;

import dev.lbuddyboy.flash.util.Config;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.commands.staff.EOTWCommand;
import dev.lbuddyboy.samurai.events.Event;
import dev.lbuddyboy.samurai.events.koth.KOTH;
import dev.lbuddyboy.samurai.scoreboard.SamuraiScoreboard;
import dev.lbuddyboy.samurai.scoreboard.ScoreboardLine;
import dev.lbuddyboy.samurai.scoreboard.ScoreboardTitle;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.util.TimeUtils;
import dev.lbuddyboy.samurai.util.object.ScoreFunction;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class FFAScoreboard extends SamuraiScoreboard {

    private final Config config;
    private final ScoreboardTitle title;
    private final List<ScoreboardLine> lines = new ArrayList<>();

    public FFAScoreboard() {
        this.config = new Config(Samurai.getInstance(), "ffa", Samurai.getInstance().getScoreboardHandler().getScoreboardDirectory());
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

    @SneakyThrows
    @Override
    public List<String> translateLines(List<String> lines, Player player) {
        List<String> replacement = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat();

        sdf.setTimeZone(TimeZone.getTimeZone("EST"));

        for (String line : lines) {
            long ffaEnabledAt = EOTWCommand.getFfaActiveAt();
            if (System.currentTimeMillis() < ffaEnabledAt) {
                long difference = ffaEnabledAt - System.currentTimeMillis();
                replacement.add(line
                        .replaceAll("%ffa-time%", ScoreFunction.TIME_SIMPLE.apply(difference / 1000F))
                        .replaceAll("%date%", sdf.format(new Date()))
                        .replaceAll("%player_name%", player.getName())
                        .replaceAll("%online%", "" + Bukkit.getOnlinePlayers().size())
                );
            } else {
                replacement.add(line
                        .replaceAll("%ffa-time%", "&a&lCOMMENCED")
                        .replaceAll("%date%", sdf.format(new Date()))
                        .replaceAll("%player_name%", player.getName())
                        .replaceAll("%online%", "" + Bukkit.getOnlinePlayers().size())
                );
            }
        }

        return replacement;
    }

    @Override
    public boolean qualifies(Player player) {
        return EOTWCommand.isFfaEnabled();
    }
}
