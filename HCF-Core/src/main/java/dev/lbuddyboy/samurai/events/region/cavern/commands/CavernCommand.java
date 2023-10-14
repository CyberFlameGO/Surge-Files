package dev.lbuddyboy.samurai.events.region.cavern.commands;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.events.region.cavern.Cavern;
import dev.lbuddyboy.samurai.events.region.cavern.CavernHandler;
import dev.lbuddyboy.samurai.team.Team;

@CommandAlias("cavern")
@CommandPermission("op")
public class CavernCommand extends BaseCommand {

    @Default
    public static void def(Player sender) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam("Cavern");

        team.sendTeamInfo(sender);
    }

    @Subcommand("scan")
    public static void cavernScan(Player sender) {
        if (!Samurai.getInstance().getConfig().getBoolean("cavern", false)) {
            sender.sendMessage(RED + "Cavern is currently disabled. Check config.yml to toggle.");
            return;
        }

        Team team = Samurai.getInstance().getTeamHandler().getTeam(CavernHandler.getCavernTeamName());

        // Make sure we have a team
        if (team == null) {
            sender.sendMessage(ChatColor.RED + "You must first create the team (" + CavernHandler.getCavernTeamName() + ") and claim it!");
            return;
        }

        // Make sure said team has a claim
        if (team.getClaims().isEmpty()) {
            sender.sendMessage(ChatColor.RED + "You must claim land for '" + CavernHandler.getCavernTeamName() + "' before scanning it!");
            return;
        }

        // We have a claim, and a team, now do we have a glowstone?
        if (!Samurai.getInstance().getCavernHandler().hasCavern()) {
            Samurai.getInstance().getCavernHandler().setCavern(new Cavern());
        }

        // We have a glowstone now, we're gonna scan and save the area
        Samurai.getInstance().getCavernHandler().getCavern().scan();
        Samurai.getInstance().getCavernHandler().save(); // save to file :D

        sender.sendMessage(GREEN + "[Cavern] Scanned all ores and saved Cavern to file!");
    }

    @Subcommand("reset")
    public static void cavernReset(Player sender) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(CavernHandler.getCavernTeamName());

        // Make sure we have a team, claims, and a mountain!
        if (team == null || team.getClaims().isEmpty() || !Samurai.getInstance().getCavernHandler().hasCavern()) {
            sender.sendMessage(RED + "Create the team '" + CavernHandler.getCavernTeamName() + "', then make a claim for it, finally scan it! (/cavern scan)");
            return;
        }

        // Check, check, check, LIFT OFF! (reset the mountain)
        Samurai.getInstance().getCavernHandler().getCavern().reset();

        Bukkit.broadcastMessage(AQUA + "[Cavern]" + GREEN + " All ores have been reset!");
    }
}