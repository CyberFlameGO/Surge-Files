package dev.lbuddyboy.pcore.essential.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Name;
import dev.lbuddyboy.pcore.essential.offline.OfflineData;
import dev.lbuddyboy.pcore.essential.offline.menu.OfflineEditorMenu;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.util.Tasks;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("editoffline")
@CommandPermission("pcore.command.editoffline")
public class EditOfflineCommand extends BaseCommand {

    @Default
    public void def(CommandSender sender, @Name("target") OfflinePlayer target) {
        Tasks.runAsync(() -> {
            OfflineData cache = pCore.getInstance().getOfflineHandler().fetchCache(target.getUniqueId());

            Tasks.run(() -> new OfflineEditorMenu(target, cache).openMenu((Player) sender));
        });
    }

}
