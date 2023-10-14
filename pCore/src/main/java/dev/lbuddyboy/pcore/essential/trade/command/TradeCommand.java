package dev.lbuddyboy.pcore.essential.trade.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.lbuddyboy.pcore.essential.trade.Trade;
import dev.lbuddyboy.pcore.essential.trade.TradeRequest;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.util.CC;
import org.bukkit.entity.Player;

@CommandAlias("trade")
public class TradeCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    public void def(Player sender, @Name("target") OnlinePlayer targetOnline) {
        Player target = targetOnline.getPlayer();

        if (target == null || target == sender) {
            return;
        }

        TradeRequest existing = pCore.getInstance().getTradeHandler().getRequests().get(sender.getUniqueId());

        if (existing != null) {
            if (!existing.isExpired() && existing.getTarget() == target.getUniqueId()) {
                sender.sendMessage(CC.translate("&cYou already have an existing trade request to this player"));
                return;
            }
        }

        TradeRequest request = new TradeRequest(sender.getUniqueId(), target.getUniqueId(), System.currentTimeMillis());

        pCore.getInstance().getTradeHandler().getRequests().put(sender.getUniqueId(), request);
        sender.sendMessage(CC.translate("&aSuccessfully sent a trade request!"));
        target.sendMessage(CC.translate("&6&lTRADE &7" + CC.UNICODE_ARROWS_RIGHT + " &e" + sender.getName() + "&f has sent you a trade request! &e(/trade accept " + sender.getName() + ")"));
    }

    @Subcommand("accept")
    @CommandCompletion("@players")
    public void accept(Player sender, @Name("target") OnlinePlayer targetOnline) {
        Player target = targetOnline.getPlayer();

        if (target == null || target == sender) {
            return;
        }

        TradeRequest request = pCore.getInstance().getTradeHandler().getRequests().get(target.getUniqueId());

        if (request.isExpired() || request.getTarget() != sender.getUniqueId()) {
            return;
        }

        Trade trade = new Trade(target.getUniqueId(), sender.getUniqueId());

        trade.start();
        pCore.getInstance().getTradeHandler().getRequests().remove(target.getUniqueId());
    }

}
