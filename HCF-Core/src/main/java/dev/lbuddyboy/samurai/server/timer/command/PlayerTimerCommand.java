package dev.lbuddyboy.samurai.server.timer.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.samurai.server.timer.PlayerTimer;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

@CommandAlias("playertimer")
@CommandPermission("samurai.command.playertimer")
public class PlayerTimerCommand extends BaseCommand {

    @Subcommand("create|start")
    @CommandCompletion("@players @playertimers")
    public void create(CommandSender sender, @Name("target") OfflinePlayer player, @Name("timer") PlayerTimer timer, @Name("seconds") int seconds) {
        if (seconds <= 0) {
            return;
        }

        timer.getCooldown().applyCooldown(player.getUniqueId(), seconds);
        sender.sendMessage(CC.translate("&a" + player.getName() + " now has the " + timer.getName() + " player timer for " + seconds + " seconds!"));
    }

}
