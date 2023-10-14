package dev.aurapvp.samurai.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.SettingsConfiguration;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.IModule;
import dev.aurapvp.samurai.util.LocationUtils;
import dev.aurapvp.samurai.util.PagedItem;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CommandAlias("samurai")
@CommandPermission("op")
public class SamuraiCommand extends BaseCommand {

    @Default
    @HelpCommand
    @Subcommand("help")
    public static void defp(CommandSender sender, @Name("help") @Optional CommandHelp help) {
        int page = help.getPage();
        List<String> header = CC.translate(Arrays.asList(
                CC.CHAT_BAR,
                "&a&lSamurai Command Help"
        ));
        List<String> entries = new ArrayList<>();

        for (HelpEntry entry : help.getHelpEntries()) {
            entries.add("&/" + entry.getCommand() + " " + entry.getParameterSyntax() + " &7- " + entry.getDescription());
        }

        PagedItem pagedItem = new PagedItem(entries, header, 10);

        pagedItem.send(help.getIssuer().getIssuer(), page);

        help.getIssuer().sendMessage(" ");
        help.getIssuer().sendMessage("&7You can do /samurai help <page> - You're currently viewing on page #" + page + ".");
        help.getIssuer().sendMessage(CC.CHAT_BAR);
    }

    @Subcommand("reload")
    public void reload(Player sender) {
        Samurai.getInstance().getModules().forEach(IModule::save);
        Samurai.getInstance().getModules().forEach(IModule::reload);

        sender.sendMessage(CC.translate("&aSuccessfully reloaded all handlers."));
    }

    @Subcommand("setspawn")
    public void setspawn(Player sender) {
        SettingsConfiguration.SPAWN_LOCATION.update(LocationUtils.serializeString(sender.getLocation()));
        sender.sendMessage(CC.translate("&aSpawn updated to your current location!"));
    }

}
