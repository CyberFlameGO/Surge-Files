package dev.lbuddyboy.samurai.events.region.glowmtn.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.events.region.glowmtn.GlowHandler;
import dev.lbuddyboy.samurai.events.region.glowmtn.GlowMountain;
import dev.lbuddyboy.samurai.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.*;

@CommandAlias("glow|gmountain")
public class GlowCommand extends BaseCommand {

    @Default
    public static void def(Player sender) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam("Glowstone");

        team.sendTeamInfo(sender);
    }

    @Subcommand("scan")
    @CommandPermission("op")
    public static void glowScan(Player sender) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(GlowHandler.getGlowTeamName());

        // Make sure we have a team
        if (team == null) {
            sender.sendMessage(ChatColor.RED + "You must first create the team (" + GlowHandler.getGlowTeamName() + ") and claim it!");
            return;
        }

        // Make sure said team has a claim
        if (team.getClaims().isEmpty()) {
            sender.sendMessage(ChatColor.RED + "You must claim land for '" + GlowHandler.getGlowTeamName() + "' before scanning it!");
            return;
        }

        // We have a claim, and a team, now do we have a glowstone?
        if (!Samurai.getInstance().getGlowHandler().hasGlowMountain()) {
            Samurai.getInstance().getGlowHandler().setGlowMountain(new GlowMountain());
        }

        // We have a glowstone now, we're gonna scan and save the area
        Samurai.getInstance().getGlowHandler().getGlowMountain().scan();
        Samurai.getInstance().getGlowHandler().save(); // save to file :D

        sender.sendMessage(GREEN + "[Glowstone Mountain] Scanned all glowstone and saved glowstone mountain to file!");
    }

    @Subcommand("reset")
    @CommandPermission("op")
    public static void glowReset(Player sender) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(GlowHandler.getGlowTeamName());

        // Make sure we have a team, claims, and a mountain!
        if (team == null || team.getClaims().isEmpty() || !Samurai.getInstance().getGlowHandler().hasGlowMountain()) {
            sender.sendMessage(RED + "Create the team '" + GlowHandler.getGlowTeamName() + "', then make a claim for it, finally scan it! (/glow scan)");
            return;
        }

        // Check, check, check, LIFT OFF! (reset the mountain)
        Samurai.getInstance().getGlowHandler().getGlowMountain().reset();

        Bukkit.broadcastMessage(GOLD + "[Glowstone Mountain]" + GREEN + " All glowstone has been reset!");
    }
}