package dev.lbuddyboy.pcore.essential.plot.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.pcore.SettingsConfiguration;
import dev.lbuddyboy.pcore.essential.plot.PrivatePlot;
import dev.lbuddyboy.pcore.mines.PrivateMine;
import dev.lbuddyboy.pcore.mines.menu.PrivateMineMenu;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.util.CC;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("plots|plot|plotme")
public class PlotCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        PrivatePlot cache = pCore.getInstance().getPlotHandler().fetchCache(sender.getUniqueId());

        if (cache == null) {
            cache = pCore.getInstance().getPlotHandler().createPlot(sender.getUniqueId());

            pCore.getInstance().getPlotHandler().updateCache(sender.getUniqueId(), cache);

            sender.sendMessage(CC.translate("&aCreating a new plot..."));
            sender.teleport(cache.getSpawnLocation().clone().add(0, 3, 0));
            return;
        }

        sender.teleport(cache.getSpawnLocation());
    }

    @Subcommand("disband")
    @CommandCompletion("@players")
    @CommandPermission("op")
    public void disband(CommandSender sender, @Name("player") OfflinePlayer player) {
        PrivatePlot cache = pCore.getInstance().getPlotHandler().fetchCache(player.getUniqueId());

        if (cache == null) {
            sender.sendMessage(CC.translate("&aNo plot exists..."));
            return;
        }

        for (Player p : cache.getBounds().getWorld().getPlayers()) {
            if (!cache.getBounds().contains(p.getLocation())) continue;

            p.teleport(SettingsConfiguration.SPAWN_LOCATION.getLocation());
            p.sendMessage(CC.translate("&cThis plot was disbanded, so you've been teleported away."));
        }

        pCore.getInstance().getPlotHandler().deletePlot(player.getUniqueId(), cache);
        for (Player other : cache.getPlayers()) {
            other.teleport(SettingsConfiguration.SPAWN_LOCATION.getLocation());
        }

    }

    @Subcommand("info")
    @CommandCompletion("@players")
    public void info(CommandSender sender, @Name("player") OfflinePlayer player) {
        PrivatePlot cache = pCore.getInstance().getPlotHandler().fetchCache(player.getUniqueId());

        if (cache == null) {
            sender.sendMessage(CC.translate("&aNo plot exists..."));
            return;
        }

        sender.sendMessage(CC.translate(player.getName() + "'s Plot"));
        sender.sendMessage(CC.translate("&7Trusted (" + cache.getAllowedUUIDs().size() + ")"));
        for (UUID uuid : cache.getAllowedUUIDs()) {
            sender.sendMessage(CC.translate("&e- " + uuid));
        }

    }

}
