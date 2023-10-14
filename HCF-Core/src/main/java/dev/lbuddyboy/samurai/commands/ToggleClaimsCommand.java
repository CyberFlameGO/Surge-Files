package dev.lbuddyboy.samurai.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.team.claims.LandBoard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("toggleclaims")
@CommandPermission("op")
public class ToggleClaimsCommand extends BaseCommand {

    @Default
    public static void toggleClaims(Player sender) {
        LandBoard.getInstance().setClaimsEnabled(!LandBoard.getInstance().isClaimsEnabled());
        sender.sendMessage(ChatColor.YELLOW + "Claims enabled? " + ChatColor.LIGHT_PURPLE + (LandBoard.getInstance().isClaimsEnabled() ? "Yes" : "No"));
    }

}