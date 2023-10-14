package dev.lbuddyboy.samurai.map.duel.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.duel.DuelHandler;
import dev.lbuddyboy.samurai.map.duel.DuelInvite;
import org.bukkit.entity.Player;

@CommandAlias("accept")
public class AcceptCommand extends BaseCommand {

    @Default
    public static void accept(Player sender, @Name("player") OnlinePlayer target) {
        DuelHandler duelHandler = Samurai.getInstance().getMapHandler().getDuelHandler();

        if (!duelHandler.canAccept(sender, target.getPlayer())) {
            return;
        }

        DuelInvite invite = duelHandler.getInvite(target.getPlayer().getUniqueId(), sender.getUniqueId());
        duelHandler.acceptDuelRequest(invite);
    }

}
