package dev.lbuddyboy.lmotd.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import dev.lbuddyboy.lmotd.lMOTD;
import dev.lbuddyboy.lmotd.model.MOTDTimer;
import dev.lbuddyboy.lmotd.util.CC;
import dev.lbuddyboy.lmotd.util.JavaUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.UUID;

public class MOTDTimerCommand implements SimpleCommand {

    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();
        // Get the arguments after the command alias
        String[] args = invocation.arguments();

        if (args.length == 0) {
            source.sendMessage(CC.translate("&cUsage: /motdtimer start|stop"));
            return;
        }

        if (args[0].equalsIgnoreCase("start")) {

            if (args.length == 1) {
                source.sendMessage(CC.translate("&cUsage: /motdtimer start <display> <duration>"));
                return;
            }

            if (args.length == 2) {
                source.sendMessage(CC.translate("&cUsage: /motdtimer start <display> <duration>"));
                return;
            }

            String title = args[1];
            long duration = JavaUtils.parse(args[2]);

            lMOTD.getInstance().setActiveTimer(new MOTDTimer(UUID.randomUUID(), title.replaceAll("-", " "), System.currentTimeMillis(), duration));

            source.sendMessage(Component.text("Created a new timer!").color(NamedTextColor.AQUA));
        } else if (args[0].equalsIgnoreCase("stop")) {

            if (lMOTD.getInstance().getActiveTimer() == null) {
                source.sendMessage(Component.text("Cant find that timer.").color(NamedTextColor.RED));
                return;
            }

            lMOTD.getInstance().getActiveTimer().delete();

            source.sendMessage(Component.text("Deleted a timer!").color(NamedTextColor.AQUA));
        } else if (args[0].equalsIgnoreCase("reload")) {
            lMOTD.getInstance().getConfigYML().reload();
        }
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("command.test");
    }
}
