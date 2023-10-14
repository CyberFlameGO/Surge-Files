package dev.lbuddyboy.samurai.events.koth.commands.kothschedule;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

@CommandAlias("kothschedule")
@CommandPermission("foxtrot.koth.admin")
public class KOTHScheduleCommand extends BaseCommand {

    @Subcommand("enable")
    public static void kothScheduleDisable(CommandSender sender) {
        Samurai.getInstance().getEventHandler().setScheduleEnabled(false);

        sender.sendMessage(ChatColor.YELLOW + "The KOTH schedule has been " + ChatColor.RED + "disabled" + ChatColor.YELLOW + ".");
    }

    @Subcommand("enable")
    public static void kothScheduleEnable(CommandSender sender) {
        Samurai.getInstance().getEventHandler().setScheduleEnabled(true);

        sender.sendMessage(ChatColor.YELLOW + "The KOTH schedule has been " + ChatColor.GREEN + "enabled" + ChatColor.YELLOW + ".");
    }

    @Subcommand("regen|regenerate")
    public static void regen(CommandSender sender) {
        File kothSchedule = new File(Samurai.getInstance().getDataFolder(), "eventSchedule.json");

        if (kothSchedule.delete()) {
            Samurai.getInstance().getEventHandler().loadSchedules();

            sender.sendMessage(ChatColor.YELLOW + "The event schedule has been regenerated.");
        } else {
            sender.sendMessage(ChatColor.RED + "Couldn't delete event schedule file.");
        }
    }

    @Subcommand("reload")
    public static void kothScheduleReload(Player sender) {
        Samurai.getInstance().getEventHandler().loadSchedules();
        sender.sendMessage(ChatColor.GOLD + "[King of the Hill] " + ChatColor.YELLOW + "Reloaded the KOTH schedule.");
    }

}
