package dev.lbuddyboy.samurai.events.conquest.commands.conquestadmin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.samurai.events.conquest.ConquestHandler;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.events.conquest.game.ConquestGame;
import dev.lbuddyboy.samurai.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandAlias("conquestadmin")
@CommandPermission("op")
public class ConquestAdminCommand extends BaseCommand {

    @Subcommand("setscore")
    @CommandCompletion("@team")
    public static void conquestAdminSetScore(CommandSender sender, @Name("team") Team team, @Name("score") int score) {
        ConquestGame game = Samurai.getInstance().getConquestHandler().getGame();

        if (game == null) {
            sender.sendMessage(ChatColor.RED + "Conquest is not active.");
            return;
        }

        game.getTeamPoints().put(team.getUniqueId(), score);
        sender.sendMessage(ConquestHandler.PREFIX + " " + ChatColor.GOLD + "Updated the score for " + team.getName() + ChatColor.GOLD + ".");
    }

    @Subcommand("start")
    public static void conquestAdminStart(CommandSender sender) {
        ConquestGame game = Samurai.getInstance().getConquestHandler().getGame();

        if (game != null) {
            sender.sendMessage(ChatColor.RED + "Conquest is already active.");
            return;
        }

        new ConquestGame();
    }

    @Subcommand("stop")
    public static void conquestAdminStop(CommandSender sender) {
        ConquestGame game = Samurai.getInstance().getConquestHandler().getGame();

        if (game == null) {
            sender.sendMessage(ChatColor.RED + "Conquest is not active.");
            return;
        }

        game.endGame(null);
    }

}