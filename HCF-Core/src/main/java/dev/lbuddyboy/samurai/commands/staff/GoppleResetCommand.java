package dev.lbuddyboy.samurai.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

@CommandAlias("gopplereset")
@CommandPermission("foxtrot.gopplereset")
public class GoppleResetCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    public static void goppleReset(CommandSender sender, @Name("player") UUID player) {
        Samurai.getInstance().getOppleMap().resetCooldown(player);
        sender.sendMessage(ChatColor.RED + "" + UUIDUtils.name(player) + "'s gopple cooldown is now reset!");
    }

}