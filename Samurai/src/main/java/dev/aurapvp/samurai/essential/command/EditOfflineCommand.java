package dev.aurapvp.samurai.essential.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.aurapvp.samurai.essential.offline.OfflineData;
import dev.aurapvp.samurai.essential.offline.menu.OfflineEditorMenu;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.Tasks;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

@CommandAlias("editoffline")
@CommandPermission("samurai.command.editoffline")
public class EditOfflineCommand extends BaseCommand {

    @Default
    public void def(CommandSender sender, @Name("target") OfflinePlayer target) {
        Tasks.runAsync(() -> {
            OfflineData cache = Samurai.getInstance().getOfflineHandler().fetchCache(target.getUniqueId());

            Tasks.run(() -> new OfflineEditorMenu(target, cache).openMenu((Player) sender));
        });
    }

}
