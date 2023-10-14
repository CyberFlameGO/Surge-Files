package dev.lbuddyboy.samurai.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.economy.FrozenEconomyHandler;
import dev.lbuddyboy.samurai.economy.uuid.FrozenUUIDCache;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

@CommandAlias("reboot")
@CommandPermission("op")
public class RebootCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    public static void balance(CommandSender sender) {
        sender.sendMessage(CC.translate("Rebooting in 1.5 seconds."));

        Bukkit.getScheduler().runTaskAsynchronously(Samurai.getInstance(), () -> {
            Samurai.getInstance().saveAll();
        });

        Bukkit.getScheduler().runTaskLater(Samurai.getInstance(), Bukkit::shutdown, 30);
    }
}