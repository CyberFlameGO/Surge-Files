package dev.lbuddyboy.samurai.team.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

@CommandAlias("exportteamdata")
@CommandPermission("op")
public class TeamDataCommands extends BaseCommand {


    @Default
    @Description("Exports all teams to a file")
    public static void exportTeamData(CommandSender sender,  String fileName) {
        File file = new File(fileName);

        if (file.exists()) {
            sender.sendMessage(ChatColor.RED + "An export under that name already exists.");
            return;
        }

        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(file));

            out.writeUTF(sender.getName());
            out.writeUTF(new Date().toString());
            out.writeInt(Samurai.getInstance().getTeamHandler().getTeams().size());

            for (Team team : Samurai.getInstance().getTeamHandler().getTeams()) {
                out.writeUTF(team.getName());
                out.writeUTF(team.saveString(false));
            }

            sender.sendMessage(ChatColor.GOLD + "Saved " + Samurai.getInstance().getTeamHandler().getTeams().size() + " teams to " + ChatColor.GREEN + file.getAbsolutePath() + ChatColor.GOLD + ".");
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "Could not import teams! Check console for errors.");
        }
    }



}