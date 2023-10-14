package dev.lbuddyboy.samurai.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.economy.FrozenEconomyHandler;
import dev.lbuddyboy.samurai.economy.uuid.FrozenUUIDCache;
import dev.lbuddyboy.samurai.server.SpawnTagHandler;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

@CommandAlias("rtp")
public class RTPCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    public static void balance(Player sender) {
        if (!Samurai.getInstance().getMapHandler().isKitMap()) return;

        if (SOTWCommand.isSOTWTimer()) {
            sender.sendMessage(CC.translate("&cYou cannot teleport down road during SOTW Timer."));
            return;
        }

        if (SpawnTagHandler.isTagged(sender)) {
            sender.sendMessage(CC.translate("&cYou cannot teleport down road while spawn tagged."));
            return;
        }

        if (!DTRBitmask.SAFE_ZONE.appliesAt(sender.getLocation())) {
            sender.sendMessage(CC.translate("&cYou cannot teleport down road while not in spawn."));
            return;
        }

        sender.teleport(new Location(sender.getWorld(), 0, 80, Samurai.getInstance().getMapHandler().getWorldBuffer()));
    }
}