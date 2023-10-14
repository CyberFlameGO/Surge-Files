package dev.aurapvp.samurai.timer.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.timer.ServerTimer;
import dev.aurapvp.samurai.timer.menu.ServerTimerMenu;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.TimeUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

@CommandAlias("servertimer")
@CommandPermission("samurai.command.servertimer")
public class ServerTimerCommand extends BaseCommand {

    @Subcommand("create|start")
    public void create(Player sender, @Name("name") @Single String name, @Name("duration") @Single String duration, @Name("display") String displayName) {
        ServerTimer.ServerTimerBuilder builder = ServerTimer.builder()
                .name(name)
                .duration(TimeUtils.parseTime(duration) * 1000L)
                .displayName(displayName);
        
        sender.setMetadata("server_timer_creation", new FixedMetadataValue(Samurai.getInstance(), builder));
        sender.sendMessage(CC.translate("&aPlease type the context this should be displayed as. Type 'cancel' to stop this process."));
        sender.sendMessage(CC.translate("&aExample&7: &f%timer% will end in %time-left%&f!"));
    }

    @Subcommand("delete|stop|end")
    @CommandCompletion("@servertimers")
    public void delete(CommandSender sender, @Name("timer") ServerTimer timer) {
        Samurai.getInstance().getTimerHandler().getServerTimers().remove(timer.getName());
        sender.sendMessage(CC.translate("&aSuccessfully ended the " + timer.getName() + " server timer!"));
    }

    @Subcommand("editor")
    public void editor(Player sender) {
        new ServerTimerMenu().openMenu(sender);
    }

}
