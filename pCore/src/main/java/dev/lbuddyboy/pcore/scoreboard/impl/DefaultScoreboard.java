package dev.lbuddyboy.pcore.scoreboard.impl;

import dev.drawethree.xprison.XPrison;
import dev.drawethree.xprison.multipliers.enums.MultiplierType;
import dev.drawethree.xprison.utils.misc.ProgressBar;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.economy.BankAccount;
import dev.lbuddyboy.pcore.economy.IEconomy;
import dev.lbuddyboy.pcore.scoreboard.SamuraiScoreboard;
import dev.lbuddyboy.pcore.scoreboard.ScoreboardLine;
import dev.lbuddyboy.pcore.scoreboard.ScoreboardTitle;
import dev.lbuddyboy.pcore.user.MineUser;
import dev.lbuddyboy.pcore.util.Config;
import dev.lbuddyboy.pcore.util.NumberFormat;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.*;

public class DefaultScoreboard extends SamuraiScoreboard {

    private final Config config;
    private final ScoreboardTitle title;
    private final List<ScoreboardLine> lines = new ArrayList<>();

    public DefaultScoreboard() {
        this.config = new Config(pCore.getInstance(), "default", pCore.getInstance().getScoreboardHandler().getScoreboardDirectory());
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
        IEconomy economy = pCore.getInstance().getEconomyHandler().getEconomy();
        BankAccount account = economy.getBankAccount(player.getUniqueId(), true);
        SimpleDateFormat sdf = new SimpleDateFormat();
        MineUser user = pCore.getInstance().getMineUserHandler().getMineUser(player.getUniqueId());
        sdf.setTimeZone(TimeZone.getTimeZone("EST"));

        for (String line : lines) {
            replacement.add(line
                    .replaceAll("%player%", player.getName())
                    .replaceAll("%balance%", NumberFormat.formatNumber(account.getMoney()))
                    .replaceAll("%tokens%", NumberFormat.formatNumber(XPrison.getInstance().getTokens().getApi().getPlayerTokens(player)))
                    .replaceAll("%online%", "" + Bukkit.getOnlinePlayers().size())
                    .replaceAll("%prestige%", "" + XPrison.getInstance().getRanks().getCore().getPrestiges().getApi().getPlayerPrestige(player).getId())
                    .replaceAll("%tokens%", NumberFormat.formatNumber(XPrison.getInstance().getTokens().getTokensManager().getPlayerTokens(player)))
                    .replaceAll("%prestige%", "" + XPrison.getInstance().getRanks().getCore().getPrestiges().getApi().getPlayerPrestige(player).getId())
                    .replaceAll("%mined%", NumberFormat.formatNumber(XPrison.getInstance().getTokens().getTokensManager().getPlayerBrokenBlocks(player)))
                    .replaceAll("%token-multiplier%", NumberFormat.formatNumber(XPrison.getInstance().getMultipliers().getApi().getPlayerMultiplier(player, MultiplierType.TOKENS)))
                    .replaceAll("%sell-multiplier%", NumberFormat.formatNumber(XPrison.getInstance().getMultipliers().getApi().getPlayerMultiplier(player, MultiplierType.SELL)))
                    .replaceAll("%gems%", NumberFormat.formatNumber(XPrison.getInstance().getGems().getApi().getPlayerGems(player)))
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
