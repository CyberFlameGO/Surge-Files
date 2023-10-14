package dev.lbuddyboy.pcore.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import dev.lbuddyboy.pcore.SettingsConfiguration;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.IModule;
import dev.lbuddyboy.pcore.util.LocationUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("spawn")
@CommandPermission("pcore.command.spawn")
public class SpawnCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        sender.teleport(SettingsConfiguration.SPAWN_LOCATION.getLocation());
    }

}
