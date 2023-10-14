package dev.lbuddyboy.pcore.scoreboard.impl;

import dev.drawethree.xprison.XPrison;
import dev.drawethree.xprison.gangs.model.Gang;
import dev.drawethree.xprison.multipliers.enums.MultiplierType;
import dev.drawethree.xprison.ranks.model.Rank;
import dev.drawethree.xprison.utils.misc.ProgressBar;
import dev.lbuddyboy.pcore.events.Event;
import dev.lbuddyboy.pcore.events.koth.KoTH;
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

public class GangKoTHScoreboard extends SamuraiScoreboard {

    private final Config config;
    private final ScoreboardTitle title;
    private final List<ScoreboardLine> lines = new ArrayList<>();

    public GangKoTHScoreboard() {
        this.config = new Config(pCore.getInstance(), "gang-koth", pCore.getInstance().getScoreboardHandler().getScoreboardDirectory());
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
        Optional<Gang> gangOpt = XPrison.getInstance().getGangs().getApi().getPlayerGang(player);

        SimpleDateFormat sdf = new SimpleDateFormat();

        if (!gangOpt.isPresent()) return replacement;

        Gang gang = gangOpt.get();
        KoTH koTH = pCore.getInstance().getScoreboardHandler().getActiveKoTH();
        MineUser user = pCore.getInstance().getMineUserHandler().getMineUser(player.getUniqueId());

        sdf.setTimeZone(TimeZone.getTimeZone("EST"));

        for (String line : lines) {
            replacement.add(line
                    .replaceAll("%player%", player.getName())
                    .replaceAll("%player_balance%", NumberFormat.formatNumber(account.getMoney()))
                    .replaceAll("%player_tokens%", NumberFormat.formatNumber(XPrison.getInstance().getTokens().getApi().getPlayerTokens(player)))
                    .replaceAll("%player_prestige%", "" + XPrison.getInstance().getRanks().getCore().getPrestiges().getApi().getPlayerPrestige(player).getId())
                    .replaceAll("%server_online%", "" + Bukkit.getOnlinePlayers().size())
                    .replaceAll("%player_rank_level%", NumberFormat.formatNumber(user.getPrivateMine().getRankInfo().getRank()))
                    .replaceAll("%player_rank_progress_bar%", ProgressBar.getProgressBar(20, "|", user.getPrivateMine().getRankInfo().getProgress(), user.getPrivateMine().getRankInfo().getNeededProgress()))
                    .replaceAll("%player_blocks_mined%", NumberFormat.formatNumber(XPrison.getInstance().getTokens().getTokensManager().getPlayerBrokenBlocks(player)))
                    .replaceAll("%player_token_multiplier%", NumberFormat.formatNumber(XPrison.getInstance().getMultipliers().getApi().getPlayerMultiplier(player, MultiplierType.TOKENS)))
                    .replaceAll("%player_sell_multiplier%", NumberFormat.formatNumber(XPrison.getInstance().getMultipliers().getApi().getPlayerMultiplier(player, MultiplierType.SELL)))
                    .replaceAll("%player_gems%", NumberFormat.formatNumber(XPrison.getInstance().getGems().getApi().getPlayerGems(player)))
                    .replaceAll("%date%", sdf.format(new Date()))
                    .replaceAll("%gang_size%", "" + (gang.getMembersOffline().size() + gang.getOnlinePlayers().size()))
                    .replaceAll("%gang_online%", "" + gang.getOnlinePlayers().size())
                    .replaceAll("%gang_name%", gang.getName())
                    .replaceAll("%gang_balance%", "" + NumberFormat.formatNumber(gang.getValue()))
                    .replaceAll("%koth_x%", String.valueOf(koTH.getCenter().getBlockX()))
                    .replaceAll("%koth_y%", String.valueOf(koTH.getCenter().getBlockY()))
                    .replaceAll("%koth_z%", String.valueOf(koTH.getCenter().getBlockZ()))
                    .replaceAll("%koth_cap_time%", koTH.getTimeLeftString())
                    .replaceAll("%koth_capping%", koTH.getCapper() == null ? "&cNone" : koTH.getCapper().getName())
            );
        }

        return replacement;
    }

    @Override
    public boolean qualifies(Player player) {
        Optional<Gang> gangOpt = XPrison.getInstance().getGangs().getApi().getPlayerGang(player);

        if (!gangOpt.isPresent()) return false;

        KoTH koTH = pCore.getInstance().getScoreboardHandler().getActiveKoTH();

        return koTH != null;
    }
}
