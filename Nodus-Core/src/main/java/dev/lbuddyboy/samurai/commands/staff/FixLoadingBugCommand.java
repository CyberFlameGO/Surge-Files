package dev.lbuddyboy.samurai.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Name;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

@CommandAlias("fixloading|fixloadingbug")
@CommandPermission("op")
public class FixLoadingBugCommand extends BaseCommand {

    @Default
    public void string(CommandSender sender, @Name("player") UUID uuid) throws IOException {
        if (Bukkit.getPlayer(uuid) != null) {
            sender.sendMessage(CC.translate("&cThe player has to be offline."));
            return;
        }

        Files.deleteIfExists(new File(Bukkit.getWorld("world").getWorldFolder(), "playerdata" + File.separator + "" + uuid + ".dat").toPath());
        Files.deleteIfExists(new File(Bukkit.getWorld("world").getWorldFolder(), "playerdata" + File.separator + "" + uuid + ".dat_old").toPath());
        sender.sendMessage(CC.translate("&aFixed " + uuid + "'s player data."));
    }

}
