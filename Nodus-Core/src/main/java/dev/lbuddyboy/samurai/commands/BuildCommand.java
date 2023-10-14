package dev.lbuddyboy.samurai.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

@CommandAlias("build|buildmode")
@CommandPermission("foxtrot.build")
public class BuildCommand extends BaseCommand {

    @Default
    public static void build(Player sender) {
        if (sender.hasMetadata("Build")) {
            sender.removeMetadata("Build", Samurai.getInstance());
        } else {
            sender.setMetadata("Build", new FixedMetadataValue(Samurai.getInstance(), true));
        }
        sender.sendMessage(CC.translate("&eToggled build mode &b" + (sender.hasMetadata("Build") ? "on" : "off")));
    }

}
