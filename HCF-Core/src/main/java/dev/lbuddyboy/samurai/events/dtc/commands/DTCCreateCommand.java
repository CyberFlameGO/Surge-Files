package dev.lbuddyboy.samurai.events.dtc.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.samurai.events.dtc.DTC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("dtr")
@CommandPermission("foxtrot.dtc.admin")
public class DTCCreateCommand extends BaseCommand {

    @Subcommand("create")
    public static void kothCreate(Player sender, @Name("koth") String koth) {
        new DTC(koth, sender.getLocation());
        sender.sendMessage(ChatColor.GRAY + "Created a DTC named " + koth + ".");
    }

}
