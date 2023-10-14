package dev.lbuddyboy.pcore.timer.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.pcore.timer.PlayerTimer;
import dev.lbuddyboy.pcore.util.CC;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

@CommandAlias("playertimer")
@CommandPermission("pcore.command.playertimer")
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
