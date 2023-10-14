package dev.lbuddyboy.samurai.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

@CommandAlias("staffrevive")
@CommandPermission("foxtrot.staff")
public class ReviveCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    public static void revive(CommandSender sender, @Name("player") UUID uuid, @Name("reason") String reason) {
        if (reason.equals(".")) {
            sender.sendMessage(ChatColor.RED + ". is not a good reason...");
            return;
        }

        if (Samurai.getInstance().getDeathbanMap().isDeathbanned(uuid)) {
            Samurai.getInstance().getDeathbanMap().revive(uuid);
            sender.sendMessage(ChatColor.GREEN + "Revived " + UUIDUtils.name(uuid) + "!");
        } else {
            sender.sendMessage(ChatColor.RED + "That player is not deathbanned!");
        }
    }

}
