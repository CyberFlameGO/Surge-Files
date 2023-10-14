package dev.lbuddyboy.pcore.essential.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.pcore.essential.rollback.PlayerDeath;
import dev.lbuddyboy.pcore.essential.rollback.menu.DeathsMenu;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.util.CC;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@CommandAlias("deaths")
@CommandPermission("pcore.command.deaths")
public class DeathsCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    public void def(CommandSender sender, @Name("target") OfflinePlayer target) {
        List<PlayerDeath> cache = pCore.getInstance().getRollbackHandler().fetchCache(target.getUniqueId());

        if (!(sender instanceof Player)) {
            for (PlayerDeath death : cache) {
                sender.sendMessage(CC.translate(death.getId().toString() + " - " + new SimpleDateFormat().format(new Date(death.getDeathTime()))));
            }
            return;
        }

        new DeathsMenu(target, cache).openMenu((Player) sender);
    }

}
