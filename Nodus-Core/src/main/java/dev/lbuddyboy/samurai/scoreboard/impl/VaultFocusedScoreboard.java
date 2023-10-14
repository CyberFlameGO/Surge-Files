package dev.lbuddyboy.samurai.scoreboard.impl;

import dev.lbuddyboy.flash.util.Config;
import lombok.SneakyThrows;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.vaults.VaultHandler;
import dev.lbuddyboy.samurai.events.Event;
import dev.lbuddyboy.samurai.events.koth.KOTH;
import dev.lbuddyboy.samurai.map.stats.StatsEntry;
import dev.lbuddyboy.samurai.scoreboard.SamuraiScoreboard;
import dev.lbuddyboy.samurai.scoreboard.ScoreboardLine;
import dev.lbuddyboy.samurai.scoreboard.ScoreboardTitle;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.util.ProgressBarUtil;
import dev.lbuddyboy.samurai.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class VaultFocusedScoreboard extends SamuraiScoreboard {

    private final Config config;
    private final ScoreboardTitle title;
    private final List<ScoreboardLine> lines = new ArrayList<>();

    public VaultFocusedScoreboard() {
        this.config = new Config(Samurai.getInstance(), "vault-focused", Samurai.getInstance().getScoreboardHandler().getScoreboardDirectory());
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
        StatsEntry stats = Samurai.getInstance().getMapHandler().getStatsHandler().getStats(player.getUniqueId());
        SimpleDateFormat sdf = new SimpleDateFormat();
        Team team = Samurai.getInstance().getTeamHandler().getTeam(player), focusedTeam = team.getFocusedTeam();
        Location hqLoc = focusedTeam.getHQ();
        String hq = hqLoc == null ? "None" : String.format("%d, %d", hqLoc.getBlockX(), hqLoc.getBlockZ());

        sdf.setTimeZone(TimeZone.getTimeZone("EST"));

        for (Event event : Samurai.getInstance().getEventHandler().getEvents()) {
            if (!event.isActive() || event.isHidden()) {
                continue;
            }

            if (event.getName().equalsIgnoreCase(VaultHandler.TEAM_NAME)) {
                KOTH koth = (KOTH) event;
                for (String line : lines) {
                    replacement.add(line
                            .replaceAll("%vault-stage%", Samurai.getInstance().getVaultHandler().isContested() ? "CONTESTED" : Samurai.getInstance().getVaultHandler().getVaultStage().getStageName())
                            .replaceAll("%vault-progress%", ProgressBarUtil.getProgressBar(koth.getCapTime() - koth.getRemainingCapTime(), koth.getCapTime()))
                            .replaceAll("%vault-world%", koth.getWorld())
                            .replaceAll("%vault-time-left%", TimeUtils.formatIntoMMSS(koth.getRemainingCapTime()))
                            .replaceAll("%vault-type%", koth.getType().name())
                            .replaceAll("%vault-name%", koth.getName())
                            .replaceAll("%vault-capturing%", koth.getCurrentCapper() == null ? "&cNone" : koth.getCurrentCapper())
                            .replaceAll("%vault-location%", "" + koth.getCapLocation().getBlockX() + ", " + koth.getCapLocation().getBlockY() + ", " + koth.getCapLocation().getBlockZ())
                            .replaceAll("%player_name%", player.getName())
                            .replaceAll("%player_kills%", String.valueOf(stats.getKills()))
                            .replaceAll("%player_deaths%", String.valueOf(stats.getDeaths()))
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
        }

        return replacement;
    }

    @Override
    public boolean qualifies(Player player) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(player);

        if (team == null) return false;
        if (team.getFocusedTeam() == null) return false;

        for (Event event : Samurai.getInstance().getEventHandler().getEvents()) {
            if (!event.isActive() || event.isHidden()) {
                continue;
            }

            return event.getName().equalsIgnoreCase(VaultHandler.TEAM_NAME);
        }
        return false;
    }
}
