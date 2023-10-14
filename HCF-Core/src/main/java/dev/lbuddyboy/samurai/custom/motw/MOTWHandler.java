package dev.lbuddyboy.samurai.custom.motw;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("motw")
public class MOTWHandler extends BaseCommand {

    @Subcommand("start")
    @CommandPermission("op")
    public void start(CommandSender sender) {
        reset();
    }

    public void reset() {
        for (UUID player : Samurai.getInstance().getReclaimHandler().getReclaimMap().getPlayers()) {
            Samurai.getInstance().getReclaimHandler().getReclaimMap().setReclaimed(player, false);
            Player bukkitPlayer = Bukkit.getPlayer(player);
            if (bukkitPlayer == null) continue;

            bukkitPlayer.sendMessage(CC.translate("&6&l[RECLAIM RESET]&e Your reclaim has just been reset!"));
        }

    }

}
