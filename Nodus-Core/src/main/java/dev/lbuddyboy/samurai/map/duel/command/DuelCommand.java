package dev.lbuddyboy.samurai.map.duel.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.duel.DuelHandler;
import dev.lbuddyboy.samurai.map.duel.menu.SelectWagerMenu;
import org.bukkit.entity.Player;

@CommandAlias("duel")
public class DuelCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    public static void duel(Player sender, @Name("player") OnlinePlayer onlinePlayer) {

        DuelHandler duelHandler = Samurai.getInstance().getMapHandler().getDuelHandler();

        if (!duelHandler.canDuel(sender, onlinePlayer.getPlayer())) {
            return;
        }

        new SelectWagerMenu(wager -> {
            sender.closeInventory();
            duelHandler.sendDuelRequest(sender, onlinePlayer.getPlayer(), wager);
        }).openMenu(sender);
    }

}
