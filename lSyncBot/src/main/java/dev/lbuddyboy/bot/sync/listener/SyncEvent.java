package dev.lbuddyboy.bot.sync.listener;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SyncEvent extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent e) {
        if (e.getMember() == null)
            return;

        if (e.getAuthor().isBot()) return;
        if (!e.getChannel().getName().equalsIgnoreCase("sync")) return;

        e.getMessage().delete().queue();
    }
}
