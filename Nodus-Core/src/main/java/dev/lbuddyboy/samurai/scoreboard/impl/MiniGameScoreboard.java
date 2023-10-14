package dev.lbuddyboy.samurai.scoreboard.impl;

import dev.lbuddyboy.flash.util.Config;
import dev.lbuddyboy.samurai.util.object.LinkedList;
import lombok.SneakyThrows;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.game.Game;
import dev.lbuddyboy.samurai.map.stats.StatsEntry;
import dev.lbuddyboy.samurai.scoreboard.SamuraiScoreboard;
import dev.lbuddyboy.samurai.scoreboard.ScoreboardLine;
import dev.lbuddyboy.samurai.scoreboard.ScoreboardTitle;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MiniGameScoreboard extends SamuraiScoreboard {

    private final Config config;
    private final ScoreboardTitle title;
    private final List<ScoreboardLine> lines = new ArrayList<>();

    public MiniGameScoreboard() {
        this.config = new Config(Samurai.getInstance(), "minigame", Samurai.getInstance().getScoreboardHandler().getScoreboardDirectory());
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
        LinkedList<String> replacement = new LinkedList<>();

        SimpleDateFormat sdf = new SimpleDateFormat();
        Game ongoingGame = Samurai.getInstance().getMapHandler().getGameHandler().getOngoingGame();
        StatsEntry stats = Samurai.getInstance().getMapHandler().getStatsHandler().getStats(player.getUniqueId());

        sdf.setTimeZone(TimeZone.getTimeZone("EST"));

        for (String line : lines) {
            if (line.equalsIgnoreCase("%minigame-lines%")) {
                replacement.addAll(ongoingGame.getScoreboardLines(player));
                continue;
            }
            replacement.add(line
                    .replaceAll("%date%", sdf.format(new Date()))
                    .replaceAll("%player_kills%", String.valueOf(stats.getKills()))
                    .replaceAll("%player_deaths%", String.valueOf(stats.getDeaths()))
                    .replaceAll("%player_name%", player.getName())
                    .replaceAll("%online%", "" + Bukkit.getOnlinePlayers().size())
            );
        }

        return replacement;
    }

    @Override
    public boolean qualifies(Player player) {
        if (Samurai.getInstance().getMapHandler().getGameHandler() != null && Samurai.getInstance().getMapHandler().getGameHandler().isOngoingGame()) {
            Game ongoingGame = Samurai.getInstance().getMapHandler().getGameHandler().getOngoingGame();
            return ongoingGame != null && ongoingGame.isPlayingOrSpectating(player.getUniqueId());
        }
        return false;
    }
}
