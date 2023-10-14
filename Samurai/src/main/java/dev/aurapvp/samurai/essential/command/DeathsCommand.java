package dev.aurapvp.samurai.essential.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.aurapvp.samurai.essential.rollback.PlayerDeath;
import dev.aurapvp.samurai.essential.rollback.menu.DeathsMenu;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.Tasks;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@CommandAlias("deaths")
@CommandPermission("samurai.command.deaths")
public class DeathsCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    public void def(CommandSender sender, @Name("target") OfflinePlayer target) {
        List<PlayerDeath> cache = Samurai.getInstance().getRollbackHandler().fetchCache(target.getUniqueId());

        if (!(sender instanceof Player)) {
            for (PlayerDeath death : cache) {
                sender.sendMessage(CC.translate(death.getId().toString() + " - " + new SimpleDateFormat().format(new Date(death.getDeathTime()))));
            }
            return;
        }

        new DeathsMenu(target, cache).openMenu((Player) sender);
    }

}
