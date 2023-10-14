package dev.lbuddyboy.samurai.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.economy.FrozenEconomyHandler;
import dev.lbuddyboy.samurai.economy.uuid.FrozenUUIDCache;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

@CommandAlias("balance|bal|eco|econ")
public class BalanceCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    public static void balance(Player sender, @Name("player") @Flags("self") @Optional UUID player) {
        if (player == null) player = sender.getUniqueId();

        if (sender.getUniqueId().equals(player)) {
            sender.sendMessage(ChatColor.GOLD + "Balance: $" + ChatColor.WHITE + NumberFormat.getNumberInstance(Locale.US).format(FrozenEconomyHandler.getBalance(sender.getUniqueId())));
            return;
        }

        sender.sendMessage(ChatColor.GOLD + "Balance of " + FrozenUUIDCache.name(player) + ": $" + ChatColor.WHITE + NumberFormat.getNumberInstance(Locale.US).format(FrozenEconomyHandler.getBalance(player)));
    }
}