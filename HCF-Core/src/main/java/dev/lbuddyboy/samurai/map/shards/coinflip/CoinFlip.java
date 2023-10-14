package dev.lbuddyboy.samurai.map.shards.coinflip;

import dev.lbuddyboy.samurai.map.shards.EconomyType;
import dev.lbuddyboy.samurai.map.shards.coinflip.event.CoinFlipStartEvent;
import dev.lbuddyboy.samurai.map.shards.coinflip.menu.WagerMenu;
import dev.lbuddyboy.samurai.map.shards.coinflip.task.CoinFlipTask;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.cooldown.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Data
public class CoinFlip {

    private final UUID sender;
    private final CoinFlipType side;
    private final int amount;
    private final EconomyType economy;
    private final long sentAt;
    private UUID challenger;
    private CoinFlipType challengerSide, winningSide;
    private int countdown, timesDone;

    public String getSenderName() {
        return Bukkit.getOfflinePlayer(this.sender).getName();
    }

    public List<UUID> getParticipants() {
        return new ArrayList<UUID>() {{
            add(sender);
            if (challenger != null) add(challenger);
        }};
    }

    public List<Player> getPlayers() {
        return getParticipants().stream().map(Bukkit::getPlayer).collect(Collectors.toList());
    }

    public void accept(Player player) {
        Cooldown cooldown = Samurai.getInstance().getShardHandler().getCoinFlipHandler().getCooldown();

        if (!economy.hasAmount(player.getUniqueId(), amount)) {
            player.sendMessage(CC.translate("&cYou do not have enough " + economy.getName().toLowerCase() + " to do this coin flip."));
            return;
        }

        economy.removeAmount(player.getUniqueId(), amount);

        this.challenger = player.getUniqueId();
        this.challengerSide = side == CoinFlipType.HEADS ? CoinFlipType.TAILS : CoinFlipType.HEADS;
        this.countdown = 3;

        WagerMenu menu = new WagerMenu(this);
        Player sender = Bukkit.getPlayer(this.sender);

        if (sender != null) menu.openMenu(sender);
        menu.openMenu(player);

        cooldown.applyCooldown(this.sender, 30);
        cooldown.applyCooldown(this.challenger, 30);
        Bukkit.getPluginManager().callEvent(new CoinFlipStartEvent(this.sender, challenger));

        new CoinFlipTask(this, menu).runTaskTimerAsynchronously(Samurai.getInstance(), 20, 20);
    }

    public void reward(UUID winner, UUID loser) {
        Samurai.getInstance().getShardHandler().getCoinFlipHandler().getCoinFlips().remove(this.sender);

        economy.addAmount(winner, amount * 2);

        Samurai.getInstance().getShardHandler().getCoinFlipHandler().incrementWins(winner);
        Samurai.getInstance().getShardHandler().getCoinFlipHandler().incrementProfit(winner, amount);
        Samurai.getInstance().getShardHandler().getCoinFlipHandler().incrementLosses(loser);
        Samurai.getInstance().getShardHandler().getCoinFlipHandler().decrementProfit(loser, amount);

        OfflinePlayer winnerPlayer = Bukkit.getOfflinePlayer(winner);
        OfflinePlayer loserPlayer = Bukkit.getOfflinePlayer(loser);

        try {
/*            Bukkit.broadcastMessage(CC.translate("&a&lCOIN FLIP &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " %winner% has just defeated %loser% in a %amount% coin flip.")
                    .replaceAll("%winner%", Objects.requireNonNull(winnerPlayer.getName()))
                    .replaceAll("%loser%", Objects.requireNonNull(loserPlayer.getName()))
                    .replaceAll("%amount%", getEconomy().getPrefix() + NumberFormat.getNumberInstance(Locale.US).format(this.amount)));
       */
        } catch (Exception ignored) {

        }

        for (String s : Samurai.getInstance().getShardHandler().getCoinFlipHandler().getConfig().getStringList("messages.win-message")) {
            Bukkit.broadcastMessage(CC.translate(s,
                    "%winner%", Objects.requireNonNull(winnerPlayer.getName())
                    , "%loser%", Objects.requireNonNull(loserPlayer.getName())
                    , "%amount%", getEconomy().getPrefix() + NumberFormat.getNumberInstance(Locale.US).format(this.amount)
            ));
        }
    }

    public void refund() {
        economy.addAmount(this.sender, amount);

        Samurai.getInstance().getShardHandler().getCoinFlipHandler().getCoinFlips().remove(sender);
        for (String s : Samurai.getInstance().getShardHandler().getCoinFlipHandler().getConfig().getStringList("messages.refunded")) {
            Bukkit.broadcastMessage(CC.translate(s
                    , "%amount%", getEconomy().getPrefix() + NumberFormat.getNumberInstance(Locale.US).format(this.amount)
            ));
        }
    }

    public CoinFlipType getSide(UUID player) {
        return player.toString().equals(this.sender.toString()) ? this.side : this.challengerSide;
    }

}
