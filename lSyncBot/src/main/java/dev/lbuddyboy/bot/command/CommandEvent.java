package dev.lbuddyboy.bot.command;

import dev.lbuddyboy.bot.Bot;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class CommandEvent extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        for (Command command : Bot.getInstance().getCommandHandler().getCommands()) {
            if (command.getCmd().equalsIgnoreCase(event.getName())) {
                command.send(event);
            }
        }
    }

}
