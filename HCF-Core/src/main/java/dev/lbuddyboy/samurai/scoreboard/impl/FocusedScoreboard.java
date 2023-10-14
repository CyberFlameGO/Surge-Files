package dev.lbuddyboy.samurai.scoreboard.impl;

import dev.lbuddyboy.flash.util.Config;
import dev.lbuddyboy.samurai.scoreboard.SamuraiScoreboard;
import dev.lbuddyboy.samurai.scoreboard.ScoreboardLine;
import dev.lbuddyboy.samurai.scoreboard.ScoreboardTitle;
import lombok.SneakyThrows;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class FocusedScoreboard extends SamuraiScoreboard {

    private final Config config;
    private final ScoreboardTitle title;
    private final List<ScoreboardLine> lines = new ArrayList<>();

    public FocusedScoreboard() {
        this.config = new Config(Samurai.getInstance(), "focused", Samurai.getInstance().getScoreboardHandler().getScoreboardDirectory());
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
        Team team = Samurai.getInstance().getTeamHandler().getTeam(player), focusedTeam = team.getFocusedTeam();

        sdf.setTimeZone(TimeZone.getTimeZone("EST"));

        for (String line : lines) {
            Location hqLoc = focusedTeam.getHQ();
            String hq = hqLoc == null ? "None" : String.format("%d, %d", hqLoc.getBlockX(), hqLoc.getBlockZ());

            replacement.add(line
                    .replaceAll("%focused-dtr%", focusedTeam.getDTRColor() + Team.DTR_FORMAT.format(focusedTeam.getDTR()) + focusedTeam.getDTRSuffix())
                    .replaceAll("%focused-name%", focusedTeam.getName())
                    .replaceAll("%focused-online%", String.valueOf(focusedTeam.getOnlineMembers().size()))
                    .replaceAll("%focused-hq%", hq)
                    .replaceAll("%focused-size%", String.valueOf(focusedTeam.getMembers().size()))
                    .replaceAll("%date%", sdf.format(new Date()))
                    .replaceAll("%player_name%", player.getName())
                    .replaceAll("%online%", "" + Bukkit.getOnlinePlayers().size())
            );
        }

        return replacement;
    }

    @Override
    public boolean qualifies(Player player) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(player);

        if (team == null) return false;

        return team.getFocusedTeam() != null;
    }
}
