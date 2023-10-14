package dev.lbuddyboy.pcore.timer.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.timer.ServerTimer;
import dev.lbuddyboy.pcore.timer.menu.ServerTimerMenu;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.TimeUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

@CommandAlias("servertimer")
@CommandPermission("pcore.command.servertimer")
public class ServerTimerCommand extends BaseCommand {

    @Subcommand("create|start")
    public void create(Player sender, @Name("name") @Single String name, @Name("duration") @Single String duration, @Name("display") String displayName) {
        ServerTimer.ServerTimerBuilder builder = ServerTimer.builder()
                .name(name)
                .duration(TimeUtils.parseTime(duration) * 1000L)
                .displayName(displayName);
        
        sender.setMetadata("server_timer_creation", new FixedMetadataValue(pCore.getInstance(), builder));
        sender.sendMessage(CC.translate("&aPlease type the context this should be displayed as. Type 'cancel' to stop this process."));
        sender.sendMessage(CC.translate("&aExample&7: &f%timer% will end in %time-left%&f!"));
    }

    @Subcommand("delete|stop|end")
    @CommandCompletion("@servertimers")
    public void delete(CommandSender sender, @Name("timer") ServerTimer timer) {
        pCore.getInstance().getTimerHandler().getServerTimers().remove(timer.getName());
        sender.sendMessage(CC.translate("&aSuccessfully ended the " + timer.getName() + " server timer!"));
    }

    @Subcommand("editor")
    public void editor(Player sender) {
        new ServerTimerMenu().openMenu(sender);
    }

    @HelpCommand
    public void help(CommandSender sender, CommandHelp help){
        sender.sendMessage(CC.CHAT_BAR);
        sender.sendMessage(CC.translate("&9&lTimer Help"));
        sender.sendMessage(CC.CHAT_BAR);
        for (HelpEntry entry : help.getHelpEntries()) {
            if (entry.getDescription().isEmpty()) {
                sender.sendMessage(CC.translate("&c/" + entry.getCommand()));
            } else {
                sender.sendMessage(CC.translate("&c/" + entry.getCommand() + " - " + entry.getDescription()));
            }
        }
        sender.sendMessage(CC.CHAT_BAR);
    }

}
