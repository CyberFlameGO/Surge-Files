package dev.lbuddyboy.samurai.scoreboard.impl;

import dev.lbuddyboy.flash.util.Config;
import lombok.SneakyThrows;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.events.Event;
import dev.lbuddyboy.samurai.events.koth.KOTH;
import dev.lbuddyboy.samurai.scoreboard.SamuraiScoreboard;
import dev.lbuddyboy.samurai.scoreboard.ScoreboardLine;
import dev.lbuddyboy.samurai.scoreboard.ScoreboardTitle;
import dev.lbuddyboy.samurai.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class EOTWScoreboard extends SamuraiScoreboard {

    private final Config config;
    private final ScoreboardTitle title;
    private final List<ScoreboardLine> lines = new ArrayList<>();

    public EOTWScoreboard() {
        this.config = new Config(Samurai.getInstance(), "eotw", Samurai.getInstance().getScoreboardHandler().getScoreboardDirectory());
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

        for (Event event : Samurai.getInstance().getEventHandler().getEvents()) {
            if (!event.isActive() || event.isHidden()) {
                continue;
            }

            if (event.getName().equalsIgnoreCase("EOTW")) {
                KOTH koth = (KOTH) event;
                for (String line : lines) {
                    replacement.add(line
                            .replaceAll("%eotw-world%", koth.getWorld())
                            .replaceAll("%eotw-time-left%", TimeUtils.formatIntoMMSS(koth.getRemainingCapTime()))
                            .replaceAll("%eotw-type%", koth.getType().name())
                            .replaceAll("%eotw-name%", koth.getName())
                            .replaceAll("%eotw-capturing%", koth.getCurrentCapper() == null ? "&cNone" : koth.getCurrentCapper())
                            .replaceAll("%eotw-location%", "" + koth.getCapLocation().getBlockX() + ", " + koth.getCapLocation().getBlockY() + ", " + koth.getCapLocation().getBlockZ())
                            .replaceAll("%eotw-kills%", "" + 1)
                            .replaceAll("%date%", sdf.format(new Date()))
                            .replaceAll("%player_name%", player.getName())
                            .replaceAll("%world-border%", NumberFormat.getInstance(Locale.ENGLISH).format(player.getWorldBorder().getSize() / 2))
                            .replaceAll("%online%", "" + Bukkit.getOnlinePlayers().size())
                    );
                }
                return replacement;
            }
        }

        return replacement;
    }

    @Override
    public boolean qualifies(Player player) {
        return Samurai.getInstance().getServerHandler().isEOTW();
    }
}
