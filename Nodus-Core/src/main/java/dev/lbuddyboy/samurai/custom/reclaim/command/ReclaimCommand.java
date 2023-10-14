package dev.lbuddyboy.samurai.custom.reclaim.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.samurai.economy.uuid.FrozenUUIDCache;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Collectors;

@CommandAlias("reclaim|dontaorreclaim|donorreclaim")
public class ReclaimCommand extends BaseCommand {

    @Default
    public static void execute(Player player) {
        if (Samurai.getInstance().getReclaimHandler().getReclaimMap().isReclaimed(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED.toString() + "You've already reclaimed your donator perks!");
            return;
        }

        Configuration config = Samurai.getInstance().getConfig();

        for (String key : config.getConfigurationSection("reclaims").getKeys(false).stream().sorted(Comparator.comparingInt(key -> (int) config.getLong("reclaims." + key + ".priority", 99L))).collect(Collectors.toList())) {
            if (player.hasPermission(config.getString("reclaims." + key + ".permission"))) {

                for (String command : config.getStringList("reclaims." + key + ".commands")) {
                    try {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()).replace("%uuid%", player.getUniqueId().toString()));
                    } catch (Exception e) {
                        Samurai.getInstance().getLogger().severe("[Reclaims] Failed to execute command: " + command + " for player " + player.getName());
                        org.bukkit.command.Command.broadcastCommandMessage(player, "[Reclaims] Failed to execute command: " + command + " for player " + player.getName(), false);
                        e.printStackTrace();
                    }
                }

                Samurai.getInstance().getReclaimHandler().getReclaimMap().setReclaimed(player.getUniqueId(), true);

                player.sendMessage(ChatColor.GREEN.toString() + "You've reclaimed your " + ChatColor.BOLD + key + ChatColor.GREEN + " donator perks!");
                return;
            }
        }

        player.sendMessage(ChatColor.RED + "You have nothing to reclaim!");
    }

    @Subcommand("reset")
    @CommandCompletion("@players")
    @CommandPermission("foxtrot.admin")
    public static void reset(CommandSender sender, @Name("player") UUID target) {
        if (Samurai.getInstance().getReclaimHandler().getReclaimMap().isReclaimed(target)) {
            Samurai.getInstance().getReclaimHandler().getReclaimMap().setReclaimed(target, false);
            sender.sendMessage(ChatColor.GREEN + "Reset " + FrozenUUIDCache.name(target) + "'s reclaim status!");
        } else {
            sender.sendMessage(ChatColor.RED + FrozenUUIDCache.name(target) + " hasn't claimed their reclaim!");
        }
    }


}
