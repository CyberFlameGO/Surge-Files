package dev.lbuddyboy.hub.scoreboard.impl.bedrock;

import dev.lbuddyboy.hub.lHub;
import dev.lbuddyboy.hub.listener.BungeeListener;
import dev.lbuddyboy.hub.placeholder.Placeholder;
import dev.lbuddyboy.hub.placeholder.PlaceholderImpl;
import dev.lbuddyboy.hub.scoreboard.HubScoreboard;
import dev.lbuddyboy.hub.scoreboard.ScoreboardLine;
import dev.lbuddyboy.hub.scoreboard.ScoreboardTitle;
import dev.lbuddyboy.hub.util.CC;
import dev.lbuddyboy.hub.util.Config;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class DefaultBedrockScoreboard extends HubScoreboard {

    private final Config config;
    private final ScoreboardTitle title;
    private final List<ScoreboardLine> lines = new ArrayList<>();

    public DefaultBedrockScoreboard() {
        this.config = new Config(lHub.getInstance(), "bedrock-default", lHub.getInstance().getScoreboardHandler().getScoreboardDirectory());
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
        SimpleDateFormat sdf = new SimpleDateFormat();

        sdf.setTimeZone(TimeZone.getTimeZone("EST"));

        int online = BungeeListener.PLAYER_COUNT;

        for (String line : lines) {
            for (Placeholder placeholder : lHub.getInstance().getSettingsHandler().getPlaceholders()) {
                line = line.replaceAll(placeholder.getHolder(), placeholder.getReplacement());
            }
            for (PlaceholderImpl impl : lHub.getInstance().getPlaceholderHandler().getPlaceholderImpls()) {
                line = impl.applyPlaceholders(line, player);
                line = impl.applyPlaceholders(line);
            }
            replacement.add(line
                    .replaceAll("%rank_weight%", String.valueOf(lHub.getInstance().getRankCoreHandler().getRankCore().getRankWeight(player.getUniqueId())))
                    .replaceAll("%rank_color%", lHub.getInstance().getRankCoreHandler().getRankCore().getRankColor(player.getUniqueId()).toString())
                    .replaceAll("%rank_display%", lHub.getInstance().getRankCoreHandler().getRankCore().getRankDisplayName(player.getUniqueId()))
                    .replaceAll("%player_name%", player.getName())
                    .replaceAll("%player_display_name%", player.getDisplayName())
                    .replaceAll("%player_coins%", player.getDisplayName())
                    .replaceAll("%bungee_total%", NumberFormat.getInstance(Locale.ENGLISH).format(online))
                    .replaceAll("%date%", StringUtils.center(sdf.format(new Date()), CC.translate(getTitle().getActiveFrame()).length(), CC.translate(getTitle().getActiveFrame())))
            );
        }

        if (lHub.getInstance().getSettingsHandler().isPlaceholderAPI()) PlaceholderAPI.setPlaceholders(player, replacement);

        return replacement;
    }

    @Override
    public boolean qualifies(Player player) {
        return player.getName().startsWith(".");
    }
}
