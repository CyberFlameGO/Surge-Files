package dev.lbuddyboy.pcore.coinflip;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.api.event.CoinFlipStartEvent;
import dev.lbuddyboy.pcore.coinflip.menu.WagerMenu;
import dev.lbuddyboy.pcore.coinflip.task.CoinFlipTask;
import dev.lbuddyboy.pcore.economy.EconomyType;
import dev.lbuddyboy.pcore.economy.IEconomy;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.Cooldown;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Data
public class CoinFlip {

    private final UUID sender;
    private final CoinFlipType side;
    private final double amount;
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
        Cooldown cooldown = pCore.getInstance().getCoinFlipHandler().getCooldown();

        if (!economy.hasAmount(player.getUniqueId(), amount)) {
            player.sendMessage(CC.translate("&cYou do not have enough " + economy.getName().toLowerCase() + " to do this coin flip."));
            return;
        }

        economy.removeAmount(player.getUniqueId(), amount, IEconomy.EconomyChange.builder().build());

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

        new CoinFlipTask(this, menu).runTaskTimerAsynchronously(pCore.getInstance(), 20, 20);
    }

    public void reward(UUID winner, UUID loser) {
        pCore.getInstance().getCoinFlipHandler().getCoinFlips().remove(this.sender);

        if (economy == EconomyType.COINS) {
            pCore.getInstance().getEconomyHandler().getEconomy().addCoins(winner, amount * 2, IEconomy.EconomyChange.builder().forced(true).build());
        } else {
            pCore.getInstance().getEconomyHandler().getEconomy().addMoney(winner, amount * 2, IEconomy.EconomyChange.builder().forced(true).build());
        }

        pCore.getInstance().getCoinFlipHandler().incrementWins(winner);
        pCore.getInstance().getCoinFlipHandler().incrementProfit(winner, amount);
        pCore.getInstance().getCoinFlipHandler().incrementLosses(loser);
        pCore.getInstance().getCoinFlipHandler().decrementProfit(loser, amount);

        OfflinePlayer winnerPlayer = Bukkit.getOfflinePlayer(winner);
        OfflinePlayer loserPlayer = Bukkit.getOfflinePlayer(loser);

        if (winnerPlayer == null) return;
        if (loserPlayer == null) return;

        CC.broadcast(CC.translate(
                "&a&lCOIN FLIP &7" + CC.UNICODE_ARROW_RIGHT + " %winner% has just defeated %loser% in a %amount% coin flip.",
                "%winner%", winnerPlayer.getName(),
                "%loser%", loserPlayer.getName(),
                "%amount%", getEconomy().getPrefix() + pCore.getInstance().getEconomyHandler().getEconomy().formatMoney(getAmount())
        ));
    }

    public void refund() {
        boolean added = economy == EconomyType.COINS ? pCore.getInstance().getEconomyHandler().getEconomy().addCoins(sender, amount, IEconomy.EconomyChange.builder().forced(true).build()) :
                pCore.getInstance().getEconomyHandler().getEconomy().addMoney(sender, amount, IEconomy.EconomyChange.builder().forced(true).build());

        pCore.getInstance().getCoinFlipHandler().getCoinFlips().remove(sender);
        if (!added) return;
    }

    public CoinFlipType getSide(UUID player) {
        return player.toString().equals(this.sender.toString()) ? this.side : this.challengerSide;
    }

}
